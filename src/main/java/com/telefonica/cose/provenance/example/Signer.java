package com.telefonica.cose.provenance.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath;
	static String path;

//	public static void main(String[] args) throws Exception {
//
//
//		filepath= "./JSON_draft.json";
//		path= "./EM1-root.json";
//		// Instantiate the Signature and Parameter classes
//		SignatureInterface sign = new Signature();
//		EnclosingMethodInterface enclose = new EnclosingMethods();
//		Parameters param = new Parameters();
//
//		// Generate provenance signature as a Base64 string
//		String jsonFile = Files.readString(Path.of(filepath));
//		String signature = sign.signing(jsonFile, param.getProperty("kid"));
//
//		// Enclose the previously generated signature into a YANG data provenance xml
//		JsonNode doc = sign.loadJSONDocument(filepath);
//
//		JsonNode JSONsigned = enclose.enclosingMethod(doc,signature);
//
//		sign.saveJSONnode(JSONsigned, path);
//		System.out.println("Document was correctly saved in: " + path);
//	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		// Interactive input for file paths
		//System.out.print("Enter the input file path (e.g., ./ietf-interfaces.xml): ");
		String filepath = "./netconf.json";

		//System.out.print("Enter the output file path (e.g., ./provenance_canonic.xml): ");
		String path = "./EM3-instance.json";

		// Display enclosing method options
		System.out.println("Select an enclosing method:");
		System.out.println("1. enclosingMethod");
		System.out.println("2. enclosingMethod2");
		System.out.println("3. enclosingMethod3");
		System.out.println("4. enclosingMethod4");
		System.out.print("Enter your choice (1-4): ");
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		// Instantiate the Signature and Parameter classes
		SignatureInterface sign = new Signature();
		EnclosingMethodInterface enclose = new EnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		String xmlFile = Files.readString(Path.of(filepath));
		String signature = sign.signing(xmlFile, param.getProperty("kid"));

		// Enclose the signature using the selected method
		JsonNode doc = sign.loadJSONDocument(filepath);
		JsonNode provenanceXML;

		switch (choice) {
			case 1:
				provenanceXML = enclose.enclosingMethod(doc, signature);
				break;
			case 2:
				provenanceXML = enclose.enclosingMethod2(doc, signature);
				break;
			case 3:
				provenanceXML = enclose.enclosingMethod3(doc, signature);
				break;
			case 4:
				provenanceXML = enclose.enclosingMethod4(doc, signature);
				break;
			default:
				System.out.println("Invalid choice. Exiting...");
				scanner.close();
				return;
		}

		// Save the resulting XML document
		sign.saveJSONnode(provenanceXML, path);
		System.out.println("Document was correctly saved in: " + path);

		// Close the scanner
		scanner.close();
	}

}
