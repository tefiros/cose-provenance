/**
 * 
 */
package com.telefonica.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

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
 * @author idb0095
 */
public class Verification extends XMLFileManagement implements VerificationInterface	{

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
	}

	private OneKey publicKey(String kid) {

		OneKey keyPair = null;
		Parameters param = new Parameters();

		char pswd[] = param.getProperty("Password").toCharArray();

		KeyStore ks;
		try {
			ks = KeyStore.getInstance(param.getProperty("KeyStore Instance"));

			ks.load(new FileInputStream(param.getProperty("Signer KeyStore")), pswd);

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
	 * This method extracts the signature from the YANG data provenance file and removes the related element
	 * 
	 * @param signatureFile xml where the signature is enclosed
	 * @return signature as a bstr
	 * @throws JDOMExcpetion
	 * @throws IOException
	 */
	@Override
	public byte[] readSignature(String signatureFile) {

		Parameters param = new Parameters();

		Document document;
		byte[] signature = null;

		try {
			document = loadXMLDocument(signatureFile);

			// Get the base64 signature from the xml document and decode it
			Element rootElement = document.getRootElement();
			Namespace namespace = rootElement.getNamespace();
			Element signElement = rootElement.getChild(param.getProperty("Signature Element"), namespace);
			String signString = signElement.getText();
			signature = Base64.getDecoder().decode(signString);
			
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return signature;

	}

	/**
	 * This method reads the updated xml assuming the signature element was previously removed for verification
	 * 
	 * @param signatureFile YANG data provenance ToBeVerified
	 * @return the ToBeVerified xml file as a String
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Override
	public String readYANGFile(String signatureFile) {
		
		Parameters param = new Parameters();

		Document document;
		String content = null;

		try {
			document = loadXMLDocument(signatureFile);
			
			Element rootElement = document.getRootElement();
			Namespace namespace = rootElement.getNamespace();
			rootElement.removeChild(param.getProperty("Signature Element"), namespace);
			
			StringWriter contentXML = new StringWriter();
			saveXMLDocument(document, contentXML);
			content = contentXML.toString();
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;

	}
	
	/**
	 * This method creates a COSE_Sign1 object that contains a COSE signature
	 * previously generated and verifies it with the public key related to the
	 * private key that was used for its creation.
	 * 
	 * @param message       message that was signed, needed for verification
	 * @param signature     COSE signature to be verified
	 * @param publicOnlyKey public key to be used for the verification of the
	 *                      signature
	 * @return true if the signature validates
	 * @throws COSESignatureExceptions
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 * 
	 */
	@Override
	public boolean verify(String message, byte[] signature) throws CoseException {

		// Verify the signature
		Sign1Message verificator = (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);
		String content = canonicalizeXML(message);
		verificator.SetContent(content);

		// System.out.println(verificator.findAttribute(HeaderKeys.KID,
		// Attribute.PROTECTED).AsString());
		OneKey publicOnlyKey = publicKey(verificator.findAttribute(HeaderKeys.KID, Attribute.PROTECTED).AsString());
		//OneKey publicOnlyKey = publicKey("ec2.key");

		return verificator.validate(publicOnlyKey);

	}

}
