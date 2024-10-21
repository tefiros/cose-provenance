package com.telefonica.cose.provenance.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import com.telefonica.cose.provenance.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String path = "provenance-interfaces.xml";

	public static void main(String[] args) throws Exception {
		// Start the HTTP server on port 8000
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/sign", new SignHandler());
		server.setExecutor(null); // creates a default executor
		System.out.println("Server is listening on port 8000...");
		server.start();
	}

	// Handler for processing incoming XML via POST request
	static class SignHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("POST".equals(exchange.getRequestMethod())) {
				// Read the XML content from the request body
				String xmlContent = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
						.lines()
						.collect(Collectors.joining("\n"));

				// Print received XML content for debugging
				System.out.println("Received XML content for signing: " + xmlContent);

				// Process the XML data
				try {
					String signedXmlPath = processXmlData(xmlContent);
					String response = "Document signed successfully! Saved at: " + signedXmlPath;
					exchange.sendResponseHeaders(200, response.length());
					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} catch (Exception e) {
					String errorResponse = "Error processing XML: " + e.getMessage();
					exchange.sendResponseHeaders(500, errorResponse.length());
					OutputStream os = exchange.getResponseBody();
					os.write(errorResponse.getBytes());
					os.close();
				}
			} else {
				// Handle only POST requests
				String response = "Only POST requests are allowed.";
				exchange.sendResponseHeaders(405, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	// Method to process the incoming XML data
	private static String processXmlData(String xmlContent) throws Exception {
		// Instantiate the Signature and Parameter classes
		SignatureInterface sign = new Signature();
		EnclosingMethodInterface enclose = new EnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		String signature = sign.signing(xmlContent, param.getProperty("kid"));

		// Load the XML Document
		Document doc = loadXMLDocumentFromString(xmlContent);
		Document provenanceXML = enclose.enclosingMethod(doc, signature);
		sign.saveXMLDocument(provenanceXML, path);

		return path;
	}

	// Method to load XML document from a string using SAXBuilder
	private static Document loadXMLDocumentFromString(String xmlContent) throws Exception {
		SAXBuilder saxBuilder = new SAXBuilder();
		try (StringReader reader = new StringReader(xmlContent)) {
			return saxBuilder.build(reader);
		}
	}
}
