package com.telefonica.api.example;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;

import com.telefonica.api.*;

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

		// Instantiate the Signature and Parameter classes
		SignatureInterface sign = new Signature();
		EnclosingMethods enclose = new EnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		String xmlFile = Files.readString(Path.of(filepath));
		String signature = sign.signing(xmlFile, param.getProperty("kid"));

		// Enclose the previously generated signature into a YANG data provenance xml
		Document provenanceXML = enclose.enclosingMethod4(filepath, signature);
		sign.saveXMLDocument(provenanceXML, path);

		System.out.println("Document was correctly saved in: " + path);
	}

}
