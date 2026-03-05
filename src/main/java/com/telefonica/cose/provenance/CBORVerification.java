package com.telefonica.cose.provenance;

import COSE.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Iterator;

public class CBORVerification  extends  CBORFileManagement implements  CBORVerificationInterface{

    static {
        // Register BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @param kid key ID of the key to use
     * @return COSE key structure
     * @throws KeyStoreException        indicates an issue with the keystore
     *                                  operations
     * @throws NoSuchAlgorithmException occurs when a requested cryptographic
     *                                  algorithm is not available in the
     *                                  environment
     * @throws CertificateException     indicates a problem with a certificate
     * @throws IOException              exception that occurs during Input/Output
     *                                  (I/O) operations
     * @throws CoseException            indicates issues specific to COSE operations
     */
    private OneKey publicKey(String kid) {

        OneKey keyPair = null;
        Parameters param = new Parameters();

        char pswd[] = param.getProperty("Password").toCharArray();

        KeyStore ks;
        try {
            ks = KeyStore.getInstance(param.getProperty("KeyStore Instance"));

            // Load Keystore from resources inside JAR
            InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream("sender_keystore.p12");
            if (keystoreStream == null) {
                throw new FileNotFoundException("Keystore not found in resources");
            }

            ks.load(keystoreStream, pswd);

            Certificate certificate = ks.getCertificate(kid);
            PublicKey publicKey = certificate.getPublicKey();

            // Specify key type
            if (publicKey.getAlgorithm().equals("EC")) {

                keyPair = new OneKey(publicKey, null);
                keyPair.add(KeyKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR());

            } else if (publicKey.getAlgorithm().equals("RSA")) {

                keyPair = new OneKey(publicKey, null);
                keyPair.add(KeyKeys.KeyType, KeyKeys.KeyType_RSA);
                keyPair.add(KeyKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR());

            } else if (publicKey.getAlgorithm().equals("EdDSA")) {

                keyPair = new OneKey(publicKey, null); // error al crear clave COSE con eddsa
                keyPair.add(KeyKeys.Algorithm, AlgorithmID.EDDSA.AsCBOR());

            }

            keyPair.add(KeyKeys.KeyId, CBORObject.FromObject(kid));

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | CoseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return keyPair;

    }


    /**
     * Signature extraction in CBORObject format --> bytes encoded to CBOR
     */

    private byte[] extractSignatureCBOR(CBORObject node) throws COSESignatureException {

        if (node.getType() == CBORType.Map) {

            for (CBORObject key : node.getKeys()) {

                String field = key.AsString();

                // detectar campo de firma
                if (field.contains("provenance")) {

                    CBORObject value = node.get(key);

                    node.Remove(key); // quitar la firma del objeto

                    return value.GetByteString();
                }

                // recursion
                byte[] result = extractSignatureCBOR(node.get(key));
                if (result != null) {
                    return result;
                }
            }
        }

        else if (node.getType() == CBORType.Array) {

            for (CBORObject element : node.getValues()) {

                byte[] result = extractSignatureCBOR(element);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }


    /* ============================================================
     *                   MAIN VERIFY METHOD
     * ============================================================ */





    public boolean verify(CBORObject signedCbor) throws CoseException, COSESignatureException {

        // extraer firma
        byte[] signature = extractSignatureCBOR(signedCbor);

        if (signature == null) {
            throw new COSESignatureException("No provenance signature found");
        }

        // canonicalizar contenido restante
        byte[] canonical = canonicalizeCbor(signedCbor);

        // decodificar COSE
        Sign1Message sign1 =
                (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);

        sign1.SetContent(canonical);

        String kid = sign1
                .findAttribute(HeaderKeys.KID, Attribute.PROTECTED)
                .AsString();

        OneKey publicKey = publicKey(kid);

        return sign1.validate(publicKey);
    }

    // YANG MODULES


    private void extractSignatureRecursivelyYANG(JsonNode currentNode, String moduleName, String signatureField, byte[][] signatureWrapper) {
        if (currentNode.isObject()) {
            ObjectNode obj = (ObjectNode) currentNode;

            String fullName = moduleName + ":" + signatureField;

            // Direct match: <moduleName>:<signatureField>
            if (obj.has(fullName)) {
                String signString = obj.get(fullName).asText();
                signatureWrapper[0] = Base64.getDecoder().decode(signString);
                obj.remove(fullName);
                return;
            }

            // Fallbacks for alternative naming (compatibilidad con versiones antiguas)
            if (obj.has(signatureField)) {
                String signString = obj.get(signatureField).asText();
                signatureWrapper[0] = Base64.getDecoder().decode(signString);
                obj.remove(signatureField);
                return;
            } else if (obj.has("@ypmd:provenance-string")) {
                String signString = obj.get("@ypmd:provenance-string").asText();
                signatureWrapper[0] = Base64.getDecoder().decode(signString);
                obj.remove("@ypmd:provenance-string");
                return;
            }

            // Recurse into child fields
            Iterator<String> fieldNames = obj.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                extractSignatureRecursivelyYANG(obj.get(field), moduleName, signatureField, signatureWrapper);
                if (signatureWrapper[0] != null) return;
            }

        } else if (currentNode.isArray()) {
            ArrayNode arr = (ArrayNode) currentNode;
            for (JsonNode element : arr) {
                extractSignatureRecursivelyYANG(element, moduleName, signatureField, signatureWrapper);
                if (signatureWrapper[0] != null) return;
            }
        }
    }

    public byte[] readSignatureYANG(JsonNode yangJson, String moduleName, String signatureField)
            throws COSESignatureException {

        final byte[][] signatureHolder = { null };

        extractSignatureRecursivelyYANG(yangJson, moduleName, signatureField, signatureHolder);

        if (signatureHolder[0] == null) {
            throw new COSESignatureException(
                    "No signature field found for module '" +
                            moduleName + ":" + signatureField + "'");
        }

        return signatureHolder[0];
    }

    public boolean verifyYANG(JsonNode yangJson, String moduleName, String signatureField)
            throws CoseException, COSESignatureException {

        ObjectMapper mapper = new ObjectMapper();

        // Extraer y eliminar firma
        byte[] signature = readSignatureYANG(yangJson, moduleName, signatureField);

        try {
            //Reconstruir EXACTAMENTE el CBOR canónico firmado
            Object jsonObject = mapper.readValue(
                    mapper.writeValueAsBytes(yangJson),
                    Object.class
            );

            byte[] canonicalCbor = canonicalizeCbor(jsonObject);

            // Decodificar COSE_Sign1
            Sign1Message sign1 =
                    (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);

            sign1.SetContent(canonicalCbor);

            // Obtener KID y validar
            String kid = sign1
                    .findAttribute(HeaderKeys.KID, Attribute.PROTECTED)
                    .AsString();

            OneKey publicKey = publicKey(kid);

            return sign1.validate(publicKey);

        } catch (IOException e) {
            throw new COSESignatureException("Invalid YANG CBOR input", e);
        }
    }


}
