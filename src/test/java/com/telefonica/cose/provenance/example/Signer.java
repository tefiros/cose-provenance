package com.telefonica.cose.provenance.example;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath;
	static String path;

	public static void main(String[] args) throws Exception {
		

		filepath= "./netconf-interfaces.xml";
		path= "./provenance_netconf.xml";
		// Instantiate the Signature and Parameter classes
		//SignatureInterface sign = new Signature();
		JSONSignatureInterface sign = new JSONSignature();
		//EnclosingMethodInterface enclose = new EnclosingMethods();
		JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		//String file = Files.readString(Path.of(filepath));
		String file = "{"
				+ "\"name\": \"Alice\","
				+ "\"age\": 30,"
				+ "\"city\": \"New York\","
				+ "\"hobbies\": [\"reading\", \"traveling\", \"coding\"],"
				+ "\"nested\": {\"key1\": \"value1\", \"key2\": \"value2\"}"
				+ "}";
		//Document doc = ver.loadXMLDocument(filepath);
		String signature = sign.signing(file, param.getProperty("kid"));

		// Enclose the previously generated signature into a YANG data provenance xml
		// Document doc = sign.loadXMLDocument(filepath);
		//Document provenanceXML = enclose.enclosingMethod2(doc, signature);
		//sign.saveXMLDocument(provenanceXML, path);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode doc = objectMapper.readTree(file);
		JsonNode provenanceJSON = enclose.enclosingMethodJSON(doc, signature);

		System.out.println("Document was correctly saved in: " + provenanceJSON.toString());
	}

}
