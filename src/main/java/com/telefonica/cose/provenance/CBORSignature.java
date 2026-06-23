package com.telefonica.cose.provenance;

import COSE.*;
import COSE.Signer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

public class CBORSignature extends CBORFileManagement implements CBORSignatureInterface {

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

        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | COSESignatureException | CoseException |
                 UnrecoverableKeyException | CertificateException e) {
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
    public String signing(String document, String kid) throws CoseException, COSESignatureException {

        // Creates a COSE_Sign1 object with null payload
        Sign1Message sign1Message = new Sign1Message(true, false);

        //create cbor object

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parsear JSON a objeto Java
            Object jsonObject = mapper.readValue(document, Object.class);

            // Canonicalizar a CBOR (RFC 8949)
            byte[] canonicalCbor = canonicalizeCbor(jsonObject);

            // MUY IMPORTANTE: usar byte[]
            sign1Message.SetContent(canonicalCbor);

        } catch (IOException e) {
            throw new COSESignatureException("Invalid JSON input", e);
        }

        // Set message to sign

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

    /**
     * Signs a generic CBOR object.
     *
     * @param cbor CBORObject to sign
     * @param kid  key ID to use for signing
     * @return Base64-encoded COSE_Sign1 signature
     * @throws COSESignatureException on COSE signing errors
     * @throws CoseException          on COSE library errors
     */
    public byte[] signingCBOR(CBORObject cbor, String kid) throws COSESignatureException, CoseException {
        // Crea un mensaje COSE_Sign1 con payload nulo
        Sign1Message sign1Message = new Sign1Message(true, false);

        // Canonicaliza CBOR usando tu metodo existente
        byte[] canonicalCbor = canonicalizeCbor(cbor);

        // Asigna el contenido
        sign1Message.SetContent(canonicalCbor);

        // Obtiene la clave privada
        OneKey privateKey = privateKey(kid);

        // Añade atributos protegidos de algoritmo
        if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
            sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
            sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
        } else if (privateKey.HasAlgorithmID(AlgorithmID.EDDSA)) {
            throw new COSESignatureException("EdDSA algorithm not supported in this library version");
        } else {
            throw new COSESignatureException("No valid algorithm found");
        }

        // Añade content type y KID
        Parameters param = new Parameters();
        sign1Message.addAttribute(HeaderKeys.CONTENT_TYPE, CBORObject.FromObject(param.getProperty("Content Type")), Attribute.PROTECTED);
        sign1Message.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);

        // Firma el mensaje
        sign1Message.sign(privateKey);

        // Devuelve bytes
        return sign1Message.EncodeToBytes();
    }

    public byte[] multiSigning(CBORObject cbor, List<String> kids) throws CoseException, COSESignatureException {

        // SignMessage instead of Sign1Message
        SignMessage signMessage = new SignMessage(true, false);

        byte[] content = canonicalizeCbor(cbor);

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

        return signMessage.EncodeToBytes();
    }

    public byte[] addCounterSign(CBORObject cbor, byte[] existingSignature, String kid)
            throws CoseException, COSESignatureException {

        if (existingSignature == null || existingSignature.length == 0) {
            throw new COSESignatureException("No existing signature provided");
        }

        // 1. Decodificar el Sign1 existente
        Sign1Message sign1 = (Sign1Message) Message.DecodeFromBytes(existingSignature);

        System.out.println(">>> counterSignList AFTER DECODE: " + sign1.getCountersignerList().size());

        // 2. Recuperar countersigns existentes manualmente del header no protegido
        if (sign1.getCountersignerList().isEmpty()) {
            CBORObject existingCS = sign1.findAttribute(HeaderKeys.CounterSignature, Attribute.UNPROTECTED);
            if (existingCS != null) {
                if (existingCS.getType() == com.upokecenter.cbor.CBORType.Array
                        && existingCS.size() > 0
                        && existingCS.get(0).getType() == com.upokecenter.cbor.CBORType.Array) {

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

        // 3. Forzar payload detached
        try {
            java.lang.reflect.Field f = COSE.Message.class.getDeclaredField("emitContent");
            f.setAccessible(true);
            f.set(sign1, false);
        } catch (Exception e) {
            throw new COSESignatureException("Failed to set detached payload: " + e.getMessage());
        }

        // 4. Reponer payload canonicalizado (deterministic CBOR)
        byte[] canonicalCbor = canonicalizeCbor(cbor);
        sign1.SetContent(canonicalCbor);

        System.out.println(">>> canonicalized CBOR payload length: " + canonicalCbor.length);

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

            System.out.println(">>> SIGNING CBOR countersign with rgbProtected: "
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
        System.out.println("Sign1 CBOR firmante principal KID: " +
                (mainKid != null ? mainKid.toString() : "unknown"));

        System.out.println("Número de CBOR countersignatures: " + sign1.getCountersignerList().size());
        for (int i = 0; i < sign1.getCountersignerList().size(); i++) {
            CounterSign c = sign1.getCountersignerList().get(i);
            CBORObject csKid = c.findAttribute(HeaderKeys.KID);
            System.out.println("  CounterSign[" + i + "] KID: " +
                    (csKid != null ? csKid.toString() : "unknown"));
        }

        return sign1.EncodeToBytes();
    }

}
