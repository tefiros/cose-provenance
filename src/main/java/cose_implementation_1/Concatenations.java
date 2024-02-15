/**
 * 
 */
package cose_implementation_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 */
public class Concatenations {

	public static void main(String[] args) {
		Data data = new Data();

		try (BufferedWriter signatureWriter = new BufferedWriter(
				new FileWriter("C:/Users/idb0095/Documents/COSEdocs/signature.cose", true))) {
			// Store the message in the same file as the signature for verification purpose
			signatureWriter.write("//cont//".concat(data.Document()));
		} catch (IOException e) {
			e.printStackTrace(); // Exceptions management in case of writing errors
		}

	}
}
