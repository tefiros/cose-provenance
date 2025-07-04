package com.telefonica.cose.provenance.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
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

		String xmlString = "<envelope xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-notification\">\n" +
				"    <event-time>2024-10-10T10:59:55.32Z</event-time>\n" +
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
		Document provenanceXML = enclose.enclosingMethod2(file, signature);


		XMLOutputter xmlOutputter = new XMLOutputter();
		System.out.println("Document was correctly saved in: " + xmlOutputter.outputString(provenanceXML));

		// Guarda el documento XML en un archivo
		try (FileOutputStream fos = new FileOutputStream("provenance_output.xml")) {
			xmlOutputter.output(provenanceXML, fos);
			System.out.println("Documento guardado en provenance_output.xml");
		} catch (IOException e) {
			System.err.println("Error al guardar el XML: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
