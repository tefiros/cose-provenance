package com.telefonica.cose.provenance.example;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath;
	static String path;

	public static void main(String[] args) throws Exception {
		

		filepath= "./netconf.json";
		path= "./EM2-netconf.json";
		// Instantiate the Signature and Parameter classes
		SignatureInterface sign = new Signature();
		EnclosingMethodInterface enclose = new EnclosingMethods();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		String jsonFile = Files.readString(Path.of(filepath));
		String signature = sign.signing(jsonFile, param.getProperty("kid"));

		// Enclose the previously generated signature into a YANG data provenance xml
		JsonNode doc = sign.loadJSONDocument(filepath);

		JsonNode JSONsigned = enclose.enclosingMethod2(doc,signature);

		sign.saveJSONnode(JSONsigned, path);
		System.out.println("Document was correctly saved in: " + path);
	}

}
