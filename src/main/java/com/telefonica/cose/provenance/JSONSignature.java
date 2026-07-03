package com.telefonica.cose.provenance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.StringWriter;


import COSE.*;
import COSE.Attribute;

/**
 * This class implements the method for signing a message with COSE signatures
 * using COSE_Sign1 structures with null payload
 *
 * @author A.Mendez
 */

public class JSONSignature extends JSONFileManagement implements JSONSignatureInterface {

    static {
        // Register BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @param kid key ID of the key to use
     * @return COSE key structure
     * @throws KeyStoreException         indicates an issue with the keystore
     *                                   operations
     * @throws NoSuchAlgorithmException  occurs when a requested cryptographic
     *                                   algorithm is not available in the
     *                                   environment
     * @throws CertificateException      indicates a problem with a certificate
     * @throws IOException               exception that occurs during Input/Output
     *                                   (I/O) operations
     * @throws COSESignatureException    indicates issues specific to COSE signature
     *                                   operations
     * @throws CoseException             indicates issues specific to COSE
     *                                   operations
     * @throws UnrecoverableKeyException occurs when there’s an issue with
     *                                   retrieving a key from a keystore
     */
    private OneKey privateKey(String kid) {

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

            if (ks.containsAlias(kid)) {

                PrivateKey privateKey = (PrivateKey) ks.getKey(kid, pswd);

                keyPair = new OneKey(null, privateKey);

                // Specify key type
                if (privateKey.getAlgorithm().equals("EC")) {

                    keyPair.add(KeyKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR());

                } else if (privateKey.getAlgorithm().equals("RSA")) {

                    keyPair.add(KeyKeys.KeyType, KeyKeys.KeyType_RSA);
                    keyPair.add(KeyKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR());

                } else if (privateKey.getAlgorithm().equals("EdDSA")) {
                    // error al crear clave COSE con eddsa
                    keyPair.add(KeyKeys.Algorithm, AlgorithmID.EDDSA.AsCBOR());

                }

                keyPair.add(KeyKeys.KeyId, CBORObject.FromObject(kid));

            } else {
                throw new COSESignatureException("There is no key with this ID: " + kid);
            }

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                 | COSESignatureException | CoseException | UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return keyPair;

    }

    /**
     * This method creates a COSE_Sign1 object with nil payload and protected
     * algorithm tag attributes and signs the set message using the private key.
     *
     * @param document message to be signed, it does not appear in the structure
     * @param kid      key to be used to sign the message
     * @return the serialized signature
     * @throws COSESignatureException    indicates issues specific to COSE signature
     *                                   operations
     * @throws CoseException             indicates issues specific to COSE
     *                                   operations
     */
    @Override
    public String signing(String document, String kid) throws CoseException, COSESignatureException, JsonProcessingException {

        // Creates a COSE_Sign1 object with null payload
        Sign1Message sign1Message = new Sign1Message(true, false);
        // Set message to sign
        String content = canonicalizeJSON(document);
        sign1Message.SetContent(content);

        OneKey privateKey;
        Parameters param = new Parameters();

        privateKey = privateKey(kid);
        // Add protected attributes with algorithm tags
        if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
            sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
            sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.EDDSA)) {
            throw new COSESignatureException("EdDSA algorithm is not available for the cose library version  used");
            // sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.EDDSA.AsCBOR(),
            // Attribute.PROTECTED);
        } else
            throw new COSESignatureException("No valid algorithm found");

        sign1Message.addAttribute(HeaderKeys.CONTENT_TYPE, CBORObject.FromObject(param.getProperty("Content Type")),
                Attribute.PROTECTED);
        // Add protected attributes with KID tag
        sign1Message.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);

        // Sign the message
        sign1Message.sign(privateKey);

        String signatureString = Base64.getEncoder().encodeToString(sign1Message.EncodeToBytes());

        return signatureString;
    }

    public String multiSigning(String document, List<String> kids) throws CoseException, COSESignatureException {

        // SignMessage instead of Sign1Message
        SignMessage signMessage = new SignMessage(true, false);

        String content = canonicalizeJSON(document);
        signMessage.SetContent(content);

        Parameters param = new Parameters();

        for (String kid : kids) {
            OneKey privateKey = privateKey(kid);

            Signer signer = new Signer();

            // Mismo patrón que tienes en signing()
            if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
                signer.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
            } else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
                signer.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
            } else if (privateKey.HasAlgorithmID(AlgorithmID.EDDSA)) {
                throw new COSESignatureException("EdDSA algorithm is not available");
            } else {
                throw new COSESignatureException("No valid algorithm found");
            }

            signer.addAttribute(HeaderKeys.CONTENT_TYPE,
                    CBORObject.FromObject(param.getProperty("Content Type")), Attribute.PROTECTED);
            signer.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);
            signer.setKey(privateKey);

            signMessage.AddSigner(signer);
        }

        // Firma todos los signers de una vez
        signMessage.sign();

        return Base64.getEncoder().encodeToString(signMessage.EncodeToBytes());
    }





    public String addCounterSign(String document, String kid, String signatureElement)
            throws CoseException, COSESignatureException, JsonProcessingException {

        // 1. Extraer la firma existente del documento JSON
        String existingSignature = extractSignatureFromJSONDocument(document, signatureElement);
        if (existingSignature == null) {
            throw new COSESignatureException("No existing signature found in element: " + signatureElement);
        }

        // 2. Decodificar el Sign1 existente
        byte[] signatureBytes = Base64.getDecoder().decode(existingSignature);
        Sign1Message sign1 = (Sign1Message) Message.DecodeFromBytes(signatureBytes);

        System.out.println(">>> counterSignList AFTER DECODE: " + sign1.getCountersignerList().size());

        // Recuperar countersigns existentes manualmente del header no protegido
        if (sign1.getCountersignerList().isEmpty()) {
            CBORObject existingCS = sign1.findAttribute(HeaderKeys.CounterSignature, Attribute.UNPROTECTED);
            if (existingCS != null) {
                if (existingCS.getType() == com.upokecenter.cbor.CBORType.Array &&
                        existingCS.size() > 0 &&
                        existingCS.get(0).getType() == com.upokecenter.cbor.CBORType.Array) {

                    for (CBORObject obj : existingCS.getValues()) {
                        sign1.addCountersignature(new CounterSign(obj));
                    }
                } else {
                    sign1.addCountersignature(new CounterSign(existingCS));
                }
            }
        }

        System.out.println(">>> counterSignList AFTER MANUAL LOAD: " + sign1.getCountersignerList().size());
        System.out.println(">>> CounterSignature attr (unprotected=2): "
                + sign1.findAttribute(HeaderKeys.CounterSignature, Attribute.UNPROTECTED));

        // 3. Marcar payload detached
        try {
            java.lang.reflect.Field f = COSE.Message.class.getDeclaredField("emitContent");
            f.setAccessible(true);
            f.set(sign1, false);
        } catch (Exception e) {
            throw new COSESignatureException("Failed to set detached payload: " + e.getMessage());
        }

        // 4. Extraer documento limpio (sin el campo firma) y reponer payload canonizado
        try {
            String cleanDocument = extractJSONDocumentContent(document, signatureElement);
            String canonical = canonicalizeJSON(cleanDocument);

            System.out.println(">>> canonicalized JSON content for countersign (" + kid + "): " + canonical);

            sign1.SetContent(canonical);
        } catch (Exception e) {
            throw new COSESignatureException("Failed to strip signature element: " + e.getMessage());
        }

        // 5. Construir countersign
        OneKey privateKey = privateKey(kid);
        CounterSign cs = new CounterSign();

        if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
            cs.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
            cs.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.EDDSA)) {
            throw new COSESignatureException("EdDSA algorithm is not available for the cose library version used");
        } else {
            throw new COSESignatureException("No valid algorithm found for kid: " + kid);
        }

        cs.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);
        cs.setKey(privateKey);

        // 6. Firmar countersign con rgbProtected crudo del Sign1
        try {
            java.lang.reflect.Field f = COSE.Attribute.class.getDeclaredField("rgbProtected");
            f.setAccessible(true);
            byte[] rgbProt = (byte[]) f.get(sign1);

            System.out.println(">>> SIGNING JSON countersign with rgbProtected: "
                    + java.util.HexFormat.of().formatHex(rgbProt));

            java.lang.reflect.Method m = COSE.Signer.class
                    .getDeclaredMethod("sign", byte[].class, byte[].class);
            m.setAccessible(true);

            m.invoke(cs, rgbProt, sign1.GetContent());
        } catch (Exception e) {
            throw new COSESignatureException(
                    "Failed to sign countersign with raw rgbProtected: " + e.getMessage());
        }

        sign1.addCountersignature(cs);

        // 7. Forzar serialización del array de countersigns en el header no protegido
        try {
            List<CounterSign> allCS = sign1.getCountersignerList();

            java.lang.reflect.Method encodeMethod = COSE.Signer.class.getDeclaredMethod("EncodeToCBORObject");
            encodeMethod.setAccessible(true);

            CBORObject csArray = CBORObject.NewArray();
            for (CounterSign c : allCS) {
                csArray.Add((CBORObject) encodeMethod.invoke(c));
            }

            sign1.addAttribute(HeaderKeys.CounterSignature, csArray, Attribute.UNPROTECTED);

        } catch (Exception e) {
            throw new COSESignatureException("Failed to process counter signatures: " + e.getMessage());
        }

        // Debug
        CBORObject mainKid = sign1.findAttribute(HeaderKeys.KID);
        System.out.println("Sign1 JSON firmante principal KID: " +
                (mainKid != null ? mainKid.toString() : "unknown"));

        System.out.println("Número de JSON countersignatures: " + sign1.getCountersignerList().size());
        for (int i = 0; i < sign1.getCountersignerList().size(); i++) {
            CounterSign c = sign1.getCountersignerList().get(i);
            CBORObject csKid = c.findAttribute(HeaderKeys.KID);
            System.out.println("  CounterSign[" + i + "] KID: " +
                    (csKid != null ? csKid.toString() : "unknown"));
        }

        return Base64.getEncoder().encodeToString(sign1.EncodeToBytes());
    }

    private String extractSignatureFromJSONDocument(String document, String signatureElement) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(document);

            JsonNode sigNode = findSignatureNode(root, signatureElement);
            if (sigNode != null && sigNode.isTextual()) {
                String text = sigNode.asText().trim();
                return text.isBlank() ? null : text;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String extractJSONDocumentContent(String jsonDocument, String signatureElement)
            throws COSESignatureException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonDocument);

            if (root == null || !root.isObject()) {
                throw new COSESignatureException("JSON root is not an object");
            }

            ObjectNode rootObj = (ObjectNode) root.deepCopy();

            boolean removed = removeSignatureNode(rootObj, signatureElement);

            if (!removed) {
                throw new COSESignatureException("No signature element found: " + signatureElement);
            }

            return mapper.writeValueAsString(rootObj);

        } catch (Exception e) {
            throw new COSESignatureException("Failed to remove JSON signature element: " + e.getMessage());
        }
    }


    private JsonNode findSignatureNode(JsonNode node, String signatureElement) {
        if (node == null) {
            return null;
        }

        // Si el nodo actual es objeto, primero miramos si tiene directamente el campo
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;

            if (obj.has(signatureElement)) {
                return obj.get(signatureElement);
            }

            // Si no, buscamos recursivamente en los hijos
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode found = findSignatureNode(entry.getValue(), signatureElement);
                if (found != null) {
                    return found;
                }
            }
        }

        // Si es array, buscamos en cada elemento
        if (node.isArray()) {
            for (JsonNode item : node) {
                JsonNode found = findSignatureNode(item, signatureElement);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    private boolean removeSignatureNode(JsonNode node, String signatureElement) {
        if (node == null) {
            return false;
        }

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;

            // Si está directamente aquí, lo quitamos
            if (obj.has(signatureElement)) {
                obj.remove(signatureElement);
                return true;
            }

            // Si no, buscamos recursivamente en hijos
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (removeSignatureNode(entry.getValue(), signatureElement)) {
                    return true;
                }
            }
        }

        if (node.isArray()) {
            for (JsonNode item : node) {
                if (removeSignatureNode(item, signatureElement)) {
                    return true;
                }
            }
        }

        return false;
    }



}
