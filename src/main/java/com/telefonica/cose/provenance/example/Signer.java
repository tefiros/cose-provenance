package com.telefonica.cose.provenance.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String path = "provenance-interfaces.xml";

	public static void main(String[] args) throws Exception {
		// Create an HTTP server that listens on port 8000
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/sign", new SignHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Server is listening on port 8000...");
	}

	// HTTP handler for the /sign endpoint
	static class SignHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) {
			try {
				if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					// Read the XML content from the request body
					InputStream inputStream = exchange.getRequestBody();
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

					// Set response headers and write the response
					exchange.getResponseHeaders().set("Content-Type", "application/xml");
					exchange.sendResponseHeaders(200, signedXmlContent.getBytes().length);
					OutputStream outputStream = exchange.getResponseBody();
					outputStream.write(signedXmlContent.getBytes());
					outputStream.close();
				} else {
					// If it's not a POST request, return a 405 Method Not Allowed
					exchange.sendResponseHeaders(405, -1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					exchange.sendResponseHeaders(500, -1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
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
