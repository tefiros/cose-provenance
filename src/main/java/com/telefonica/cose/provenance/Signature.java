package com.telefonica.cose.provenance;


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
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;

import COSE.*;
import COSE.Attribute;

/**
 * This class implements the method for signing a message with COSE signatures
 * using COSE_Sign1 structures with null payload
 * 
 * @author S. Garcia
 */

public class Signature extends XMLFileManagement implements SignatureInterface {

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
		// Set message to sign
		//document = document.replaceAll(">\\s+<", ">\r\n<");
		String content = canonicalizeXML(document);
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

}
