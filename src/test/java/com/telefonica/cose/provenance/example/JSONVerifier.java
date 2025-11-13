package com.telefonica.cose.provenance.example;

import COSE.CoseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.cose.provenance.JSONVerification;
import com.telefonica.cose.provenance.JSONVerificationInterface;
import com.telefonica.cose.provenance.XMLVerification;
import com.telefonica.cose.provenance.XMLVerificationInterface;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;

import java.security.Security;

public class JSONVerifier {

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
	}

	static {
		org.apache.xml.security.Init.init();
	}

	static String xmlFilePath;

	public static void main(String[] args) throws Exception {
		
		xmlFilePath="./provenance_output.json";

		// Instantiate the Verification class
		JSONVerificationInterface ver2 = new JSONVerification();
		JsonNode doc =  ver2.loadJSONDocument(xmlFilePath);

		String signatureElement = "provenance";
		String signatureNS = "ietf-yp-provenance";
		// Verify COSE signature and content
		try {
			if (ver2.verifyYANG(doc, signatureNS, signatureElement)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			System.err.println("Signature verification failed: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
