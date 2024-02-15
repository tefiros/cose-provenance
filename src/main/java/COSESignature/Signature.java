/**
 * 
 */
package COSESignature;

import COSE.AlgorithmID;
import COSE.Attribute;
import COSE.CoseException;
import COSE.HeaderKeys;
import COSE.OneKey;
import COSE.Sign1Message;

/**
 * This class implements the method for signing a message with COSE signatures
 * using COSE_Sign1 structure with null payload
 */
public class Signature {

	/**
	 * This method creates a COSE_Sign1 object with nil payload and protected
	 * algorithm tag attributes and signs the set message using the private key.
	 * 
	 * @param document   message to be signed, it does not appear in the structure
	 * @param privateKey key to be used to sign the message
	 * @return the serialized signature 
	 */

	public byte[] signing(String document, OneKey privateKey) throws CoseException {
		// Creates a COSE_Sign1 object with null payload
		Sign1Message sign1Message = new Sign1Message(true, false);
		// Set message to sign
		sign1Message.SetContent(document);

		// Adds protected attributes with algorithm tags
		sign1Message.addAttribute(HeaderKeys.Algorithm, AlgorithmID.EDDSA.AsCBOR(), Attribute.PROTECTED);

		// Sign the message
		sign1Message.sign(privateKey);

		// Return it serialized as bytes
		return sign1Message.EncodeToBytes();
	}
}
