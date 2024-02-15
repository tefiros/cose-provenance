package cose_implementation_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.Base64;

import COSE.CoseException;
import COSESignature.Verification;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;

public class COSE_verifying {

	static {
		EdDSASecurityProvider provider = new EdDSASecurityProvider();
		Security.addProvider(provider);
	}

	public static void main(String[] args) throws ClassNotFoundException, CoseException {
		// TODO Auto-generated method stub

		Verification ver = new Verification();
		Data data = new Data();

		Path SignatureBase64 = Path.of("C:/Users/idb0095/Documents/COSEdocs/signature.cose");

		try {
			// Read the whole file as a String since the signature is encoded to Base64
			String base64Content = Files.readString(SignatureBase64);

			// Print decoded content (binary)
			System.out.println("Signature with content: " + base64Content + "\n");

			if (base64Content.contains("//cont//")) {

				// Split signature from content
				String[] parts = base64Content.split("//cont//");

				String signature64 = parts[0];
				System.out.println("SIGNATURE BASE64: " + signature64);
				String contentToVer = parts[1];
				System.out.println("CONTENT: " + contentToVer + "\n");

				// Decode signature back to bytes
				byte[] signatureRead = Base64.getDecoder().decode(signature64);

				// This is just for printing usage
				String decodedText = new String(signatureRead, "UTF-8");
				System.out.println("Decoded signature (binary): " + decodedText + "\n");

				if (ver.verify(contentToVer, signatureRead, data.Publickey())) {
					System.out.println("Signature verified");
				} else {
					System.out.println("Invalid signature");
				}

			} else {
				System.out.println("Elements missing for verification");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
