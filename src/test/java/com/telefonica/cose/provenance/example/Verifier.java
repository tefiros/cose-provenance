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
		
		xmlFilePath="./provenance_output.xml";
		// Instantiate the Verification class
		XMLVerificationInterface ver = new XMLVerification();
		//JSONVerificationInterface ver2 = new JSONVerification();
		
		Document doc = ver.loadXMLDocument(xmlFilePath);
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


		String xmlString = "<envelope xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-notification\">\n" +
				"    <event-time>2024-10-10T10:59:55.32Z</event-time>\n" +
				"    <provenance xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-provenance\">" +
				"0oRRowNjeG1sBGdlYzIua2V5ASag9lhAYAx6zZMtPCRwJ9wBTR2d50ixOlqVMaqAIFA93SFAXmWj+jfaUq+BXXQ4Qx0pXjMUnhIesvB18xvuuUmanBuSFQ==" +
				"</provenance>\n" +
				"    <contents>\n" +
				"        <push-update xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yang-push\">\n" +
				"            <id>1011</id>\n" +
				"            <datastore-contents>\n" +
				"                <interfaces xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\">\n" +
				"                    <interface>\n" +
				"                        <name>eth0</name>\n" +
				"                        <oper-status>up</oper-status>\n" +
				"                    </interface>\n" +
				"                </interfaces>\n" +
				"            </datastore-contents>\n" +
				"        </push-update>\n" +
				"    </contents>\n" +
				"</envelope>";




		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = saxBuilder.build(new StringReader(xmlString));

		// Verify COSE signature and content
		try {
			if (ver.verify(doc)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			System.err.println("Signature verification failed: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
