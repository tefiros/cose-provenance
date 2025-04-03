package com.telefonica.cose.provenance.example;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

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
		XMLSignatureInterface sign = new XMLSignature();
		//JSONSignatureInterface sign = new JSONSignature();
		XMLEnclosingMethodInterface enclose = new XMLEnclosingMethods();
		//JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		//String file = Files.readString(Path.of(filepath));
//		String file = "{"
//				+ "\"name\": \"Alice\","
//				+ "\"age\": 30,"
//				+ "\"city\": \"New York\","
//				+ "\"hobbies\": [\"reading\", \"traveling\", \"coding\"],"
//				+ "\"nested\": {\"key1\": \"value1\", \"key2\": \"value2\"}"
//				+ "}";

		String xmlString = "<root>\n" +
				"    <name>Alice</name>\n" +
				"    <age>30</age>\n" +
				"    <city>New York</city>\n" +
				"    <hobbies>\n" +
				"        <hobby>reading</hobby>\n" +
				"        <hobby>traveling</hobby>\n" +
				"        <hobby>coding</hobby>\n" +
				"    </hobbies>\n" +
				"</root>";

		// Create a SAXBuilder instance
		SAXBuilder saxBuilder = new SAXBuilder();
		Document file = saxBuilder.build(new StringReader(xmlString));
		//Document doc = ver.loadXMLDocument(filepath);
		String signature = sign.signing(xmlString, param.getProperty("kid"));

		// Enclose the previously generated signature into a YANG data provenance xml
		// Document doc = sign.loadXMLDocument(filepath);
		//Document provenanceXML = enclose.enclosingMethod2(doc, signature);
		//sign.saveXMLDocument(provenanceXML, path);

		ObjectMapper objectMapper = new ObjectMapper();
		//JsonNode doc = objectMapper.readTree(file);
		//JsonNode provenanceJSON = enclose.enclosingMethodJSON(doc, signature);
		Document provenanceXML = enclose.enclosingMethod(file, signature);


		XMLOutputter xmlOutputter = new XMLOutputter();
		System.out.println("Document was correctly saved in: " + xmlOutputter.outputString(provenanceXML));
	}

}
