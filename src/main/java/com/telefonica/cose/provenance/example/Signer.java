package com.telefonica.cose.provenance.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.io.InputStream;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;


import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath;
	static String path = "provenance-interfaces.xml";

	public static void main(String[] args) throws Exception {
		
		if (args.length != 1) {
			System.out.println("The number of arguments is not correct.");
		}else {
			filepath = args[0];
		}

		// Check if the provided argument is a URL or a local file path
		String xmlContent;
		if (isValidURL(filepath)) {
			System.out.println("Downloading XML from URL: " + filepath);
			xmlContent = downloadXml(filepath);
		} else {
			System.out.println("Reading XML from file: " + filepath);
			xmlContent = Files.readString(Path.of(filepath));
		}

		// Instantiate the Signature and Parameter classes
		SignatureInterface sign = new Signature();
		EnclosingMethodInterface enclose = new EnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		// String xmlFile = Files.readString(Path.of(filepath));
		//Document doc = ver.loadXMLDocument(filepath);
		String signature = sign.signing(xmlContent, param.getProperty("kid"));

		// Enclose the previously generated signature into a YANG data provenance xml
		Document doc = loadXMLDocumentFromString(xmlContent);
		Document provenanceXML = enclose.enclosingMethod(doc, signature);
		sign.saveXMLDocument(provenanceXML, path);

		System.out.println("Document was correctly saved in: " + path);
	}

	// Method to download XML content from a given URL
	private static String downloadXml(String xmlUrl) throws Exception {
		URL url = new URL(xmlUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		try (InputStream inputStream = connection.getInputStream();
			 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	// Helper method to check if a string is a valid URL
	private static boolean isValidURL(String urlString) {
		try {
			new URL(urlString).toURI();
			return true;
		} catch (Exception e) {
			return false;
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
