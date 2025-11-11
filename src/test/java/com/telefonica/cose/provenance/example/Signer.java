package com.telefonica.cose.provenance.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath;
	static String path;

	public static void main(String[] args) throws Exception {

		filepath = "./netconf-interfaces.xml";
		path = "./provenance_netconf.xml";

		// Instanciamos las clases de firma y de enclavamiento
		XMLSignatureInterface sign = new XMLSignature();
		XMLEnclosingMethodInterface enclose = new XMLEnclosingMethods();
		Parameters param = new Parameters();

		// XML de ejemplo
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
				"                    </interface>\n" +
				"                </interfaces-state>\n" +
				"            </datastore-contents>\n" +
				"        </push-update>\n" +
				"    </contents>\n" +
				"</envelope>";

		// Parseamos el XML
		SAXBuilder saxBuilder = new SAXBuilder();
		Document file = saxBuilder.build(new StringReader(xmlString));

		//  Generamos la firma
		String signature = sign.signing(xmlString, param.getProperty("kid"));

		// Módulo YANG del que extraeremos el namespace y el nombre del elemento
		File yangModule = new File("./ietf-yp-provenance@2025-05-09.yang");

		// Insertamos la firma automáticamente usando el módulo YANG
		Document provenanceXML = enclose.enclosingMethodYANG(file, signature, yangModule);

		//  Mostramos por consola
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		System.out.println("Documento firmado con provenance:");
		System.out.println(xmlOutputter.outputString(provenanceXML));

		// Guardamos en archivo
		try (FileOutputStream fos = new FileOutputStream("provenance_output.xml")) {
			xmlOutputter.output(provenanceXML, fos);
			System.out.println("Documento guardado en provenance_output.xml");
		} catch (IOException e) {
			System.err.println("Error al guardar el XML: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

