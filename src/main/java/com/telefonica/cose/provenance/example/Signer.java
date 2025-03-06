package com.telefonica.cose.provenance.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	public static void main(String[] args) {
		try {
			// Read the JSON content from standard input (stdin)
			InputStream inputStream = System.in;
			String JSONContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			// System.err.println("DEBUG: JSONContent = " + JSONContent);
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
			String signature = sign.signing(JSONContent, param.getProperty("kid"));

			// Load JSON document
			JsonNode doc = loadJsonNodeFromString(JSONContent);
			// System.err.println("DEBUG: JSONNode = " + doc);
			// Select appropriate enclosing method based on the query parameter
			JsonNode provenanceJSON;
			if ("notification".equalsIgnoreCase(methodType)) {
				provenanceJSON = enclose.enclosingMethod2(doc, signature);
			} else if ("instance".equalsIgnoreCase(methodType)) {
				provenanceJSON = enclose.enclosingMethod3(doc, signature);
			} else if ("annotation".equalsIgnoreCase(methodType)) {
				provenanceJSON = enclose.enclosingMethod4(doc, signature);
			} else {
				provenanceJSON = enclose.enclosingMethod(doc, signature); // Default method
			}

			// Convert signed JSON back to string
			ObjectMapper objectMapper = new ObjectMapper();
			String signedJsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(provenanceJSON);


			// Write the signed XML to standard output (stdout)
			OutputStream outputStream = System.out;
			outputStream.write(signedJsonContent.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: Unable to process the request.");
		}
	}


	//method to make string to JsonNode
	public static JsonNode loadJsonNodeFromString(String jsonContent) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsonContent);
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
