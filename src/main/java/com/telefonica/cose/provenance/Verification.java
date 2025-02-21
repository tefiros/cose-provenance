/**
 *
 */
package com.telefonica.cose.provenance;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import com.fasterxml.jackson.databind.node.ObjectNode;

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
 * @author A.Mendez
 */

public class Verification extends JSONFileManagement implements VerificationInterface {

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

//	/**
//	 * This method extracts the signature from the YANG data provenance file and
//	 * removes the related element
//	 *
//	 * @param YANGFile JDOM document with YANG data and the signature enclosed
//	 * @return signature as a bstr
//	 */
//	byte[] readSignature(JsonNode YANGFile) throws COSESignatureException {
//
//		// CHECK INSIDE NETCONF
//		byte[] signature = null;
//
//		// Check for "provenance-string" annotation or metadata
//		if (YANGFile.has("@ypmd:provenance-string")) {
//			// Get the signature value
//			String signString = YANGFile.get("@ypmd:provenance-string").asText();
//
//			// Remove the "provenance-string" field
//			((ObjectNode) YANGFile).remove("@ypmd:provenance-string");
//
//			// Decode the Base64 signature
//			signature = Base64.getDecoder().decode(signString);
//		} else if (YANGFile.has("provenance-string")) {
//			// Handle case where "provenance-string" is a regular field
//			String signString = YANGFile.get("provenance-string").asText();
//
//			// Remove the "provenance-string" field
//			((ObjectNode) YANGFile).remove("provenance-string");
//
//			// Decode the Base64 signature
//			signature = Base64.getDecoder().decode(signString);
//		} else {
//			throw new COSESignatureException("No provenance-string found in the JSON document");
//		}
//
//		return signature;
//
//	}
//

	/**
	 * This method extracts the signature from a JSON document and removes the related element,
	 * whether it's at the root level or nested inside other nodes.
	 *
	 * @param node JsonNode with YANG data and the signature enclosed.
	 * @return signature as a byte array.
	 * @throws COSESignatureException if no signature is found.
	 */
	public byte[] readSignature(JsonNode node) throws COSESignatureException {
		// Wrapper to hold the signature (for recursive modification)
		final byte[][] signatureWrapper = { null };

		// Recursive helper method to find and remove the signature
		extractSignatureRecursively(node, signatureWrapper);

		// If no signature was found, throw an exception
		if (signatureWrapper[0] == null) {
			throw new COSESignatureException("No provenance-string found in the JSON document");
		}

		return signatureWrapper[0];
	}

	/**
	 * Helper method to recursively extract the signature from a JSON node.
	 *
	 * @param currentNode the current node being traversed.
	 * @param signatureWrapper a wrapper to store the extracted signature.
	 */
	private void extractSignatureRecursively(JsonNode currentNode, byte[][] signatureWrapper) {
		if (currentNode.isObject()) {
			ObjectNode objectNode = (ObjectNode) currentNode;

			// Check for "@ypmd:provenance-string" or "provenance-string" in this object
			if (objectNode.has("@ypmd:provenance-string")) {
				String signString = objectNode.get("@ypmd:provenance-string").asText();
				signatureWrapper[0] = Base64.getDecoder().decode(signString);
				objectNode.remove("@ypmd:provenance-string");
				return; // Found, stop further recursion
			} else if (objectNode.has("provenance-string")) {
				String signString = objectNode.get("provenance-string").asText();
				signatureWrapper[0] = Base64.getDecoder().decode(signString);
				objectNode.remove("provenance-string");
				return; // Found, stop further recursion
			}

			// Recursively check all fields of the object
			for (Iterator<String> it = objectNode.fieldNames(); it.hasNext();) {
				String fieldName = it.next();
				JsonNode childNode = objectNode.get(fieldName);
				extractSignatureRecursively(childNode, signatureWrapper);
				if (signatureWrapper[0] != null) {
					return; // Stop recursion if signature is already found
				}
			}
		} else if (currentNode.isArray()) {
			// If the node is an array, iterate over its elements
			ArrayNode arrayNode = (ArrayNode) currentNode;
			for (int i = 0; i < arrayNode.size(); i++) {
				JsonNode arrayElement = arrayNode.get(i);
				extractSignatureRecursively(arrayElement, signatureWrapper);
				if (signatureWrapper[0] != null) {
					return; // Stop recursion if signature is already found
				}
			}
		}
	}



//	/**
//	 * This method reads the updated xml assuming the signature element was
//	 * previously removed for verification
//	 *
//	 * @param YANGFile JDOM document with YANG data and the signature enclosed
//	 * @return the ToBeVerified xml file as a String
//	 */
//	String readYANGFile(Document YANGFile) throws COSESignatureException {
//
//		Parameters param = new Parameters();
//
//		String content = null;
//
//		Element rootElement = YANGFile.getRootElement();
//		Namespace namespace = rootElement.getNamespace();
//		Namespace namespace2 = rootElement.getNamespace("ypmd");
//
//		if (rootElement.getAttribute("provenance-string", namespace2) != null) {
//			rootElement.removeAttribute("provenance-string", namespace2);
//		} else if (rootElement.getChild(param.getProperty("Signature Element"), namespace) != null) {
//			rootElement.removeChild(param.getProperty("Signature Element"), namespace);
//		} else {
//			throw new COSESignatureException("No leaf or metadata related to a signature");
//		}
//
//		StringWriter contentXML = new StringWriter();
//		//saveXMLDocument(YANGFile, contentXML);
//		content = contentXML.toString();
//
//		return content;
//
//	}


	/**
	 * This method reads the JSON document, removes the "provenance-string" field if present,
	 * and returns the updated JSON content as a string.
	 *
	 * @param yangFile The JsonNode representing the JSON document.
	 * @return The content of the JSON document as a string after removing the "provenance-string".
	 * @throws COSESignatureException If no "provenance-string" is found in the JSON document.
	 */
	String readYANGFile(JsonNode yangFile) throws COSESignatureException {
		ObjectMapper objectMapper = new ObjectMapper();

//		// Check for "@ypmd:provenance-string" or "provenance-string"
//		if (yangFile.has("@ypmd:provenance-string")) {
//			// Remove the "@ypmd:provenance-string" field
//			((ObjectNode) yangFile).remove("@ypmd:provenance-string");
//		} else if (yangFile.has("provenance-string")) {
//			// Remove the "provenance-string" field
//			((ObjectNode) yangFile).remove("provenance-string");
//		} else {
//			throw new COSESignatureException("No provenance-string found in the JSON document");
//		}

		try {
			// Convert the updated JSON document back to a string
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(yangFile);
		} catch (JsonProcessingException e) {
			throw new COSESignatureException("Failed to serialize the JSON document.", e);
		}
	}

	/**
	 * This method creates a COSE_Sign1 object that contains a COSE signature
	 * previously generated and verifies it with the public key related to the
	 * private key that was used for its creation.
	 *
	 * @param YANGfile   JDOM document with YANG data and the signature enclosed
	 * @return true if the signature validates
	 * @throws CoseException indicates issues specific to COSE operations
	 * @throws COSESignatureException
	 */
	@Override
	public boolean verify(JsonNode YANGfile) throws CoseException, COSESignatureException, JsonProcessingException {

		byte[] signature = readSignature(YANGfile);

		String message = readYANGFile(YANGfile);


		// Verify the signature
		Sign1Message verificator = (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);
		//JSON serializa como XML y restituye bytes aqui
		String content = canonicalizeJSON(message);
		verificator.SetContent(content);

		// System.out.println(verificator.findAttribute(HeaderKeys.KID,
		// Attribute.PROTECTED).AsString());
		OneKey publicOnlyKey = publicKey(verificator.findAttribute(HeaderKeys.KID, Attribute.PROTECTED).AsString());
		// OneKey publicOnlyKey = publicKey("ec2.key");

		return verificator.validate(publicOnlyKey);
	}

}