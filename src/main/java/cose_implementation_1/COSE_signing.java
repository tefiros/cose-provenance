package cose_implementation_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import COSE.CoseException;
import COSE.OneKey;
import COSESignature.Signature;

public class COSE_signing {

	public static void main(String[] args) throws IOException, CoseException {
		// TODO Auto-generated method stub

		Signature sign = new Signature();
		Data data = new Data();

		// --------------------------------Signature--------------------------------------------
		OneKey keypair = data.Keypair();

		System.out.println("Content: " + data.Document());
		byte[] signature = sign.signing(data.Document(), keypair);
		System.out.println("Signature without content: " + Base64.getEncoder().encodeToString(signature));

		Path signatureFilePath = Path.of("C:/Users/idb0095/Documents/COSEdocs/binarysignature.cose");
		Files.write(signatureFilePath, signature);

		// Store the signature in a file
		try (BufferedWriter signatureWriter = new BufferedWriter(
				new FileWriter("C:/Users/idb0095/Documents/COSEdocs/signature.cose"))) {
			// Encode the signature to base64 and store it with the content
			// signed
			signatureWriter.write(Base64.getEncoder().encodeToString(signature));
		} catch (IOException e) {
			e.printStackTrace(); // Exceptions management in case of writing errors
		}

		// Store the public key in a different file for verification usage
		Path keyOut = Path.of("C:/Users/idb0095/Documents/COSEdocs/publickey.cose");
		Files.write(keyOut, keypair.PublicKey().AsCBOR().EncodeToBytes());

		
	}

}
