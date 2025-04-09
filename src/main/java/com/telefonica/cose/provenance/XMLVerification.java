/**
 * 
 */
package com.telefonica.cose.provenance;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;

import COSE.AlgorithmID;
import COSE.Attribute;
import COSE.CoseException;
import COSE.HeaderKeys;
import COSE.KeyKeys;
import COSE.MessageTag;
import COSE.OneKey;
import COSE.Sign1Message;

/**
 * This class implements the method for verifying a signature created with COSE.
 * 
 * @author S. Garcia
 * @author A. Mendez
 */

public class XMLVerification extends XMLFileManagement implements XMLVerificationInterface {

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
	 * This method extracts the signature from the YANG data provenance file and
	 * removes the related element
	 * 
	 * @param YANGFile JDOM document with YANG data and the signature enclosed
	 * @return signature as a bstr
	 */
	byte[] readSignature(Document YANGFile) throws COSESignatureException {

		Parameters param = new Parameters();

		byte[] signature = null;
		String signString = null;

		// Get the base64 signature from the xml document and decode it
		Element rootElement = YANGFile.getRootElement();
		Namespace namespace = rootElement.getNamespace();
		Namespace namespace2 = rootElement.getNamespace("ypmd");

		if (rootElement.getAttribute("provenance-string", namespace2) != null) {
			signString = rootElement.getAttributeValue("provenance-string", namespace2);
		} else if (rootElement.getChild(param.getProperty("Signature Element"), namespace) != null) {
			Element signElement = rootElement.getChild(param.getProperty("Signature Element"), namespace);
			signString = signElement.getText();
		} else if (rootElement.getChild(param.getProperty("Notification Element"), namespace) != null) {
			Element signElement = rootElement.getChild(param.getProperty("Notification Element"), namespace);
			signString = signElement.getText();
		} else {
			throw new COSESignatureException("No leaf or metadata related to a signature");
		}

		signature = Base64.getDecoder().decode(signString);

		return signature;

	}

	/**
	 * This method reads the updated xml assuming the signature element was
	 * previously removed for verification
	 * 
	 * @param YANGFile JDOM document with YANG data and the signature enclosed
	 * @return the ToBeVerified xml file as a String
	 */
	String readYANGFile(Document YANGFile) throws COSESignatureException {

		Parameters param = new Parameters();

		String content = null;

		Element rootElement = YANGFile.getRootElement();
		Namespace namespace = rootElement.getNamespace();
		Namespace namespace2 = rootElement.getNamespace("ypmd");

		if (rootElement.getAttribute("provenance-string", namespace2) != null) {
			rootElement.removeAttribute("provenance-string", namespace2);
		} else if (rootElement.getChild(param.getProperty("Signature Element"), namespace) != null) {
			rootElement.removeChild(param.getProperty("Signature Element"), namespace);
		} else if (rootElement.getChild(param.getProperty("Notification Element"), namespace) != null) {
			rootElement.removeChild(param.getProperty("Notification Element"), namespace);
		} else {
			throw new COSESignatureException("No leaf or metadata related to a signature");
		}

		StringWriter contentXML = new StringWriter();
		saveXMLDocument(YANGFile, contentXML);
		content = contentXML.toString();

		return content;

	}

	/**
	 * This method creates a COSE_Sign1 object that contains a COSE signature
	 * previously generated and verifies it with the public key related to the
	 * private key that was used for its creation.
	 * 
	 * @param YANGfile   JDOM document with YANG data and the signature enclosed
	 * @return true if the signature validates
	 * @throws CoseException indicates issues specific to COSE operations
	 * @throws COSESignatureException indicates issues specific to COSE operations
	 */
	@Override
	public boolean verify(Document YANGfile) throws CoseException, COSESignatureException {
		
		byte[] signature = readSignature(YANGfile);
		String message = readYANGFile(YANGfile);
		//String check = message
				//.replaceAll("[\\r\\n]+", "\n")     // Normalize line breaks
				//.replaceAll(">\\s+<", ">\r\n<");      // Remove extra spaces between tags
				//.trim();                           // Trim leading/trailing whitespace

		// Verify the signature
		Sign1Message verificator = (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);
		//JSON serializa como XML y restituye bytes aqui
		String content = canonicalizeXML(message);
		verificator.SetContent(content);

		// System.out.println(verificator.findAttribute(HeaderKeys.KID,
		// Attribute.PROTECTED).AsString());
		OneKey publicOnlyKey = publicKey(verificator.findAttribute(HeaderKeys.KID, Attribute.PROTECTED).AsString());
		// OneKey publicOnlyKey = publicKey("ec2.key");

		return verificator.validate(publicOnlyKey);

	}

}
