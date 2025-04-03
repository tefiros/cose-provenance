package com.telefonica.cose.provenance.example;

import java.io.StringReader;
import java.security.Security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;

import COSE.CoseException;
import org.jdom2.input.SAXBuilder;

public class Verifier {

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
	}

	static {
		org.apache.xml.security.Init.init();
	}

	static String xmlFilePath;

	public static void main(String[] args) throws Exception {
		
		xmlFilePath="./provenance_netconf.xml";
		// Instantiate the Verification class
		XMLVerificationInterface ver = new XMLVerification();
		//JSONVerificationInterface ver2 = new JSONVerification();
		
		// Document doc = ver.loadXMLDocument(xmlFilePath);
//		String jsonString = "{\n" +
//				"    \"name\": \"Alice\",\n" +
//				"    \"age\": 30,\n" +
//				"    \"city\": \"New York\",\n" +
//				"    \"hobbies\": [\"reading\", \"traveling\", \"coding\"],\n" +
//				"    \"nested\": {\n" +
//				"        \"key1\": \"value1\",\n" +
//				"        \"key2\": \"value2\",\n" +
//				"        \"provenance-string\": \"0oRRowNjeG1sBGdlYzIua2V5ASag9lhA2z4DnOVfMCs21Qm21+A6wZCvE9S7S7hsh1MzDKNw4/ch8pvLMxBXDNM2wdgyVnZqu0CVxnYVuDI2VZx1xmNi9w==\"\n" +
//				"    }\n" +
//				"}";
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode rootNode = objectMapper.readTree(jsonString);

		String xmlString = "<root><provenance-string>0oRRowNjeG1sBGdlYzIua2V5ASag9lhAV5VXJEclLyo8EIlN4oPiNW74MwJ3kYxPqTq3pPGnjSi2fOlRyUmXrORUeAE9pJPP/cgQrRY2HYOyjZt9dxeqMg==</provenance-string>\n" +
				"    <name>Alice</name>\n" +
				"    <age>30</age>\n" +
				"    <city>New York</city>\n" +
				"    <hobbies>\n" +
				"        <hobby>reading</hobby>\n" +
				"        <hobby>traveling</hobby>\n" +
				"        <hobby>coding</hobby>\n" +
				"    </hobbies>\n" +
				"</root>";

		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = saxBuilder.build(new StringReader(xmlString));

		// Verify COSE signature and content
		try {
			if (ver.verify(document)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			e.printStackTrace();
		}
	}
}
