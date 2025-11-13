package com.telefonica.cose.provenance.example;

import java.io.File;
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

		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<envelope xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-notification\">\n" +
				"    <event-time>2024-02-03T11:37:25.94Z</event-time>\n" +
				"    <contents>\n" +
				"        <push-update xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yang-push\">\n" +
				"            <subscription-id>2147483648</subscription-id>\n" +
				"            <datastore-contents>\n" +
				"                <interfaces-state xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\">\n" +
				"                    <interface>\n" +
				"                        <name>GigabitEthernet1</name>\n" +
				"                        <type xmlns:ianaift=\"urn:ietf:params:xml:ns:yang:iana-if-type\">\n" +
				"                            ianaift:ethernetCsmacd\n" +
				"                        </type>\n" +
				"                        <admin-status>up</admin-status>\n" +
				"                        <oper-status>up</oper-status>\n" +
				"                        <last-change>2024-02-03T11:22:41.081+00:00</last-change>\n" +
				"                        <if-index>1</if-index>\n" +
				"                        <phys-address>0c:00:00:37:d6:00</phys-address>\n" +
				"                        <speed>1000000000</speed>\n" +
				"                        <statistics>\n" +
				"                            <discontinuity-time>2024-02-03T11:20:38+00:00</discontinuity-time>\n" +
				"                            <in-octets>8157</in-octets>\n" +
				"                            <in-unicast-pkts>94</in-unicast-pkts>\n" +
				"                            <in-broadcast-pkts>0</in-broadcast-pkts>\n" +
				"                            <in-multicast-pkts>0</in-multicast-pkts>\n" +
				"                            <in-discards>0</in-discards>\n" +
				"                            <in-errors>0</in-errors>\n" +
				"                            <in-unknown-protos>0</in-unknown-protos>\n" +
				"                            <out-octets>89363</out-octets>\n" +
				"                            <out-unicast-pkts>209</out-unicast-pkts>\n" +
				"                            <out-broadcast-pkts>0</out-broadcast-pkts>\n" +
				"                            <out-multicast-pkts>0</out-multicast-pkts>\n" +
				"                            <out-discards>0</out-discards>\n" +
				"                            <out-errors>0</out-errors>\n" +
				"                        </statistics>\n" +
				"                    </interface>\n" +
				"                </interfaces-state>\n" +
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
		File yangModule = new File("./interfaces-provenance-augmented.yang");
		//JsonNode doc = objectMapper.readTree(file);
		//JsonNode provenanceJSON = enclose.enclosingMethodJSON(doc, signature);
		Document provenanceXML = enclose.enclosingMethodYANG(file, signature, yangModule);


		XMLOutputter xmlOutputter = new XMLOutputter();
		System.out.println("Document was correctly saved in: " + xmlOutputter.outputString(provenanceXML));

		// Guarda el documento XML en un archivo
		try (FileOutputStream fos = new FileOutputStream("provenance_output2.xml")) {
			xmlOutputter.output(provenanceXML, fos);
			System.out.println("Documento guardado en provenance_output.xml");
		} catch (IOException e) {
			System.err.println("Error al guardar el XML: " + e.getMessage());
			e.printStackTrace();
		}

	}

}