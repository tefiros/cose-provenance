package com.telefonica.cose.provenance.example;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;

import com.telefonica.cose.provenance.*;

import COSE.CoseException;

public class Verifier {

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
	}

	static {
		org.apache.xml.security.Init.init();
	}

	static String xmlFilePath;

	public static void main(String[] args) throws Exception {
		
		xmlFilePath="./provenance_netconf.xml";
		// Instantiate the Verification class
		VerificationInterface ver = new Verification();
		
		Document doc = ver.loadXMLDocument(xmlFilePath);

		// Verify COSE signature and content
		try {
			if (ver.verify(doc)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			e.printStackTrace();
		}
	}
}
