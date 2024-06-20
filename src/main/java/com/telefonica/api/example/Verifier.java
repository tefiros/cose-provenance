package com.telefonica.api.example;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import COSE.CoseException;

import com.telefonica.api.*;

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
		
		if (args.length != 1) {
			System.out.println("The number of arguments is not correct");
		}else {
			xmlFilePath = args[0];
		}

		// Instantiate the Verification class
		VerificationInterface ver = new Verification();

		// Separate signature from YANG data provenance for verification
		String content = ver.readYANGFile(xmlFilePath);
		byte[] signature = ver.readSignature(xmlFilePath);

		// Verify COSE signature and content
		try {
			if (ver.verify(content, signature)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			e.printStackTrace();
		}
	}
}
