package com.telefonica.cose.provenance.example;

import java.security.Security;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import com.telefonica.cose.provenance.*;

import COSE.CoseException;

public class Verifier {

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public static void main(String[] args) throws Exception {
		// Read XML content from standard input (similar to the Signer)
		InputStream inputStream = System.in;
		String xmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

		// Instantiate the Verification class
		VerificationInterface ver = new Verification();

		// Load XML Document from the input string
		Document doc = loadXMLDocumentFromString(xmlContent);

		// Verify COSE signature and content
		try {
			if (ver.verify(doc)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			e.printStackTrace();
		}
	}

	// Method to load XML document from a string using SAXBuilder
	private static Document loadXMLDocumentFromString(String xmlContent) throws Exception {
		SAXBuilder saxBuilder = new SAXBuilder();
		try (StringReader reader = new StringReader(xmlContent)) {
			return saxBuilder.build(reader);
		}
	}
}
