package com.telefonica.cose.provenance.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	public static void main(String[] args) {
		try {
			// Read the XML content from standard input (stdin)
			InputStream inputStream = System.in;
			String xmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

			// Instantiate the Signature and Parameter classes
			SignatureInterface sign = new Signature();
			EnclosingMethodInterface enclose = new EnclosingMethods();
			Parameters param = new Parameters();

			// Generate provenance signature as a Base64 string
			String signature = sign.signing(xmlContent, param.getProperty("kid"));

			// Enclose the previously generated signature into a YANG data provenance xml
			Document doc = loadXMLDocumentFromString(xmlContent);
			Document provenanceXML = enclose.enclosingMethod(doc, signature);

			// Convert the signed XML document back to a string
			String signedXmlContent = new org.jdom2.output.XMLOutputter().outputString(provenanceXML);

			// Write the signed XML content to standard output (stdout)
			OutputStream outputStream = System.out;
			outputStream.write(signedXmlContent.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: Unable to process the request.");
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

