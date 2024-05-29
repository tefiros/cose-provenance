package com.provenance.cose.cose_provenance;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;

import processes.Parameters;
import processes.Signature;

public class Signer {

	static {
		org.apache.xml.security.Init.init();
	}

	static String filepath = "C:/Users/idb0095/Documents/COSEdocs/ietf-interfaces.xml";
	static String path = "C:/Users/idb0095/Documents/COSEdocs/ietf-interfaces-signature.xml";

	public static void main(String[] args) throws Exception {

		// Instantiate the Signature and Parameter classes
		Signature sign = new Signature();
		Parameters param = new Parameters();

		// Generate provenance signature as a Base64 string
		String xmlFile = Files.readString(Path.of(filepath));
		String signature = sign.signing(sign.canonicalizeXML(xmlFile), param.readKid());

		// Enclose the previously generated signature into a YANG data provenance xml
		Document provenanceXML = sign.enclosingMethod(filepath, signature);
		sign.saveXMLDocument(provenanceXML, path);

		System.out.println("Document was correctly saved in: " + path);
	}

}
