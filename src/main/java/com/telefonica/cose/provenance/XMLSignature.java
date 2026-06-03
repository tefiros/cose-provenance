package com.telefonica.cose.provenance;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;

import COSE.*;
import COSE.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * This class implements the method for signing a message with COSE signatures
 * using COSE_Sign1 structures with null payload
 * 
 * @author A. Mendez
 */

public class XMLSignature extends XMLFileManagement implements XMLSignatureInterface {

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

	/**
	 * COSE_Sign1 — single signer, compact format.
	 */
	@Override
	public String signing(String document, String kid) throws CoseException, COSESignatureException {

		// Creates a COSE_Sign1 object with null payload
		Sign1Message sign1Message = new Sign1Message(true, false);
		// Set message to sign
		//document = document.replaceAll(">\\s+<", ">\r\n<");
		String content = canonicalizeXML(document);
		System.out.println(">>> content al FIRMAR:\n" + content);
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

	// Multisign  with all kids given at once

	public String multiSigning(String document, List<String> kids) throws CoseException, COSESignatureException {

		// SignMessage instead of Sign1Message
		SignMessage signMessage = new SignMessage(true, false);

		String content = canonicalizeXML(document);
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

	/**
	 * Builds a Signer object for a given kid — shared logic for all multi-sign methods.
	 */
	private Signer buildSigner(String kid) throws CoseException, COSESignatureException {

		OneKey privateKey = privateKey(kid);
		Parameters param = new Parameters();
		Signer signer = new Signer();

		if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
			signer.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
		} else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
			signer.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
		} else if (privateKey.HasAlgorithmID(AlgorithmID.EDDSA)) {
			throw new COSESignatureException("EdDSA algorithm is not available");
		} else {
			throw new COSESignatureException("No valid algorithm found for kid: " + kid);
		}

		signer.addAttribute(HeaderKeys.CONTENT_TYPE,
				CBORObject.FromObject(param.getProperty("Content Type")), Attribute.PROTECTED);
		signer.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);
		signer.setKey(privateKey);

		return signer;
	}

	// -------------------------------------------------------------------------
	// Unified progressive multi-sign (COSE_Sign)
	// -------------------------------------------------------------------------

	/** ROTO
	 * Unified progressive signing method for COSE_Sign (multi-signer).
	 * The payload is always treated as <em>detached</em> (null payload in the
	 * COSE structure); {@code document} is only used for the cryptographic
	 * computation and is never embedded.
	 *
	 *
	 *                          / empty string when starting a new signature chain
	 * @param document          original document content to sign
	 * @param kid               key ID of the signer being added
	 * @return Base64-encoded COSE_Sign with the new signature appended
	 */
	public String sign(String document, String kid, String signatureElement)
			throws CoseException, COSESignatureException {

		SignMessage signMessage;
		String content;

		String existingSignature = extractSignatureFromDocument(document, signatureElement);

		if (existingSignature == null) {
			content = canonicalizeXML(document);
			signMessage = new SignMessage(true, false);
			signMessage.SetContent(content);
			signMessage.AddSigner(buildSigner(kid));
			signMessage.sign();
		} else {
			// Decodificar COSE_Sign existente
			byte[] signatureBytes = Base64.getDecoder().decode(existingSignature);
			signMessage = (SignMessage) Message.DecodeFromBytes(signatureBytes);

			// Obtener contenido sin el elemento firma
			try {
				org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
				org.jdom2.Document jdomDoc = saxBuilder.build(new java.io.StringReader(document));
				content = canonicalizeXML(extractDocumentContent(jdomDoc, signatureElement));
			} catch (Exception e) {
				throw new COSESignatureException("Failed to strip signature element: " + e.getMessage());
			}

			signMessage.SetContent(content);

			// Apartar signers ya firmados, firmar solo el nuevo, reincorporar
			List<Signer> existingSigners = new ArrayList<>(signMessage.getSignerList());
			signMessage.getSignerList().clear();
			signMessage.AddSigner(buildSigner(kid));
			signMessage.sign();
			signMessage.getSignerList().addAll(0, existingSigners);
		}

		return Base64.getEncoder().encodeToString(signMessage.EncodeToBytes());
	}



	public String addCounterSign(String document, String kid, String signatureElement)
			throws CoseException, COSESignatureException {

		// 1. Extraer la firma existente del documento
		String existingSignature = extractSignatureFromDocument(document, signatureElement);
		if (existingSignature == null) {
			throw new COSESignatureException("No existing signature found in element: " + signatureElement);
		}

		// 2. Decodificar el Sign1 existente

		byte[] signatureBytes = Base64.getDecoder().decode(existingSignature);
		Sign1Message sign1 = (Sign1Message) Message.DecodeFromBytes(signatureBytes);
		System.out.println(">>> counterSignList AFTER DECODE: " + sign1.getCountersignerList().size());

		// Recuperar countersigns existentes manualmente del header no protegido
		if (sign1.getCountersignerList().isEmpty()) {
			CBORObject existingCS = sign1.findAttribute(HeaderKeys.CounterSignature, 2);
			if (existingCS != null) {
				if (existingCS.getType() == com.upokecenter.cbor.CBORType.Array &&
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
		System.out.println(">>> CounterSignature attr (unprotected=2): " + sign1.findAttribute(HeaderKeys.CounterSignature, 2));
		System.out.println(">>> CounterSignature attr (protected=1): " + sign1.findAttribute(HeaderKeys.CounterSignature, 1));
		System.out.println(">>> CounterSignature attr (any=3): " + sign1.findAttribute(HeaderKeys.CounterSignature, 3));

		try {
			java.lang.reflect.Field f = COSE.Message.class.getDeclaredField("emitContent");
			f.setAccessible(true);
			f.set(sign1, false);
		} catch (Exception e) {
			throw new COSESignatureException("Failed to set detached payload: " + e.getMessage());
		}


		// 3. Extraer documento limpio (sin el elemento firma) y reponer payload
		try {
			org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
			org.jdom2.Document jdomDoc = saxBuilder.build(new java.io.StringReader(document));
			String cleanDocument = extractDocumentContent(jdomDoc, signatureElement);
			sign1.SetContent(canonicalizeXML(cleanDocument));
		} catch (Exception e) {
			throw new COSESignatureException("Failed to strip signature element: " + e.getMessage());
		}

		// 4. Construir y aplicar el countersign
		OneKey privateKey = privateKey(kid);
		CounterSign cs = new CounterSign();

		if (privateKey.HasAlgorithmID(AlgorithmID.ECDSA_256)) {
			cs.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
		} else if (privateKey.HasAlgorithmID(AlgorithmID.RSA_PSS_512)) {
			cs.addAttribute(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_512.AsCBOR(), Attribute.PROTECTED);
		} else {
			throw new COSESignatureException("No valid algorithm found for kid: " + kid);
		}

		cs.addAttribute(HeaderKeys.KID, privateKey.get(KeyKeys.KeyId), Attribute.PROTECTED);
		cs.setKey(privateKey);


		try {
			java.lang.reflect.Field f = COSE.Attribute.class.getDeclaredField("rgbProtected");
			f.setAccessible(true);
			byte[] rgbProt = (byte[]) f.get(sign1);

			System.out.println(">>> SIGNING countersign with rgbProtected: "
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

// Forzar serialización
		try {
			// Construir array CBOR con TODAS las countersigns via reflexión
			List<CounterSign> allCS = sign1.getCountersignerList();

			java.lang.reflect.Method encodeMethod = COSE.Signer.class.getDeclaredMethod("EncodeToCBORObject");
			encodeMethod.setAccessible(true);

			CBORObject csArray = CBORObject.NewArray();
			for (CounterSign c : allCS) {
				csArray.Add((CBORObject) encodeMethod.invoke(c));
			}

			// Meter directamente en el header no protegido
			sign1.addAttribute(HeaderKeys.CounterSignature, csArray, 2);

		} catch (Exception e) {
			throw new COSESignatureException("Failed to process counter signatures: " + e.getMessage());
		}

		// Debug — firmante principal y countersignatures
		CBORObject mainKid = sign1.findAttribute(HeaderKeys.KID);
		System.out.println("Sign1 firmante principal KID: " +
				(mainKid != null ? mainKid.toString() : "unknown"));

		System.out.println("Número de countersignatures: " + sign1.getCountersignerList().size());
		for (int i = 0; i < sign1.getCountersignerList().size(); i++) {
			CounterSign c = sign1.getCountersignerList().get(i);
			CBORObject csKid = c.findAttribute(HeaderKeys.KID);
			System.out.println("  CounterSign[" + i + "] KID: " +
					(csKid != null ? csKid.toString() : "unknown"));
		}


		return Base64.getEncoder().encodeToString(sign1.EncodeToBytes());
	}

	/**
	 * Looks for an element named {@code signatureElement} anywhere under the
	 * document root and returns its text content, or {@code null} if absent.
	 * The search is namespace-agnostic so it works regardless of which
	 * namespace the leaf belongs to.
	 */
	private String extractSignatureFromDocument(String document, String signatureElement) {
		try {
			org.jdom2.input.SAXBuilder builder = new org.jdom2.input.SAXBuilder();
			org.jdom2.Document doc = builder.build(new java.io.StringReader(document));
			org.jdom2.Element root = doc.getRootElement();

			// Buscar en todos los hijos con cualquier namespace
			for (org.jdom2.Element child : root.getChildren()) {
				if (child.getName().equals(signatureElement)) {
					String text = child.getText().trim();
					return text.isBlank() ? null : text;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	String extractDocumentContent(Document YANGFile, String signatureElement)
			throws COSESignatureException {

		Element root = YANGFile.getRootElement();

		// Buscar el elemento sin namespace (igual que extractSignatureFromDocument)
		Element sigElement = null;
		for (Element child : root.getChildren()) {
			if (child.getName().equals(signatureElement)) {
				sigElement = child;
				break;
			}
		}

		if (sigElement == null) {
			throw new COSESignatureException("No signature element found: " + signatureElement);
		}

		root.removeChild(sigElement.getName(), sigElement.getNamespace());

		StringWriter contentXML = new StringWriter();
		saveXMLDocument(YANGFile, contentXML);
		return contentXML.toString();
	}


}
