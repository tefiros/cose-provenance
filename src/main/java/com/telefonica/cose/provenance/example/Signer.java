package com.telefonica.cose.provenance.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

			// Retrieve and debug query parameters
			Map<String, String> queryParams = getQueryParamsFromEnvironment();
			// System.err.println("DEBUG: Raw Http_Query = " + System.getenv("Http_Query")); // Print raw query
			// System.err.println("DEBUG: Parsed query parameters = " + queryParams); // Print parsed parameters

			// Get the enclosure type from query parameters
			String methodType = queryParams.getOrDefault("enclosing", "leaf");
			// System.err.println("DEBUG: Selected enclosureType = " + methodType); // Debug selected method

			// Instantiate classes
			EnclosingMethods enclose = new EnclosingMethods();
			SignatureInterface sign = new Signature();
			Parameters param = new Parameters();

			// Generate signature
			String signature = sign.signing(xmlContent, param.getProperty("kid"));

			// Load XML document
			Document doc = loadXMLDocumentFromString(xmlContent);

			// Select appropriate enclosing method based on the query parameter
			Document provenanceXML;
			if ("notification".equalsIgnoreCase(methodType)) {
				provenanceXML = enclose.enclosingMethod2(doc, signature);
			} else if ("instance".equalsIgnoreCase(methodType)) {
				provenanceXML = enclose.enclosingMethod3(doc, signature);
			} else if ("annotation".equalsIgnoreCase(methodType)) {
				provenanceXML = enclose.enclosingMethod4(doc, signature);
			} else {
				provenanceXML = enclose.enclosingMethod(doc, signature); // Default method
			}

			// Convert signed XML back to string
			String signedXmlContent = new org.jdom2.output.XMLOutputter().outputString(provenanceXML);

			// Write the signed XML to standard output (stdout)
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
		return saxBuilder.build(new java.io.StringReader(xmlContent));
	}

	// Retrieves query parameters from OpenFaaS environment
	private static Map<String, String> getQueryParamsFromEnvironment() {
		String query = System.getenv("Http_Query"); // OpenFaaS query parameters
		if (query == null || query.isEmpty()) {
			//System.err.println("DEBUG: Http_Query is empty or null");
			return new HashMap<>();
		}
		return parseQueryParams(query);
	}

	// Helper method to parse query parameters (e.g., "enclosureType=enclosingMethod2")
	private static Map<String, String> parseQueryParams(String query) {
		Map<String, String> paramMap = new HashMap<>();
		String[] pairs = query.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2);
			if (keyValue.length == 2) {
				paramMap.put(keyValue[0], keyValue[1]);
			}
		}
		return paramMap;
	}
}
