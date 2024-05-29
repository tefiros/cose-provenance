package com.provenance.cose.cose_provenance;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import COSE.CoseException;
import processes.Verification;

public class Verifier {

	static {
		// Register BouncyCastle provider
		Security.addProvider(new BouncyCastleProvider());
	}

	static {
		org.apache.xml.security.Init.init();
	}

	static String xmlFilePath = "C:/Users/idb0095/Documents/COSEdocs/ietf-interfaces-signature.xml";

	public static void main(String[] args) {

		// Instantiate the Verification class
		Verification ver = new Verification();

		// Separate signature from YANG data provenance for verification
		String content = ver.readYANGFile(xmlFilePath);
		byte[] signature = ver.readSignature(xmlFilePath);

		// Verify COSE signature and content
		try {
			if (ver.verify(ver.canonicalizeXML(content), signature)) {
				System.out.println("\033[1m" + "Signature verified");
			} else {
				System.err.println("\033[1m" + "Invalid signature.");
			}
		} catch (CoseException e) {
			e.printStackTrace();
		}
	}

}
