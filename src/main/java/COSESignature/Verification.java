/**
 * 
 */
package COSESignature;

import COSE.CoseException;
import COSE.MessageTag;
import COSE.OneKey;
import COSE.Sign1Message;

/**
 * This class implements the method for verifying a signature created with COSE.
 */
public class Verification {

	/**
	 * This method creates a COSE_Sign1 object that contains a COSE signature
	 * previously generated and verifies it with the public key related to the
	 * private key that was used for its creation.
	 * 
	 * @param message   message that was signed, needed for verification
	 * @param signature COSE signature to be verified
	 * @param publicOnlyKey public key to be used for the verification of the signature 
	 * @return true if the signature validates
	 * 
	 */
	public boolean verify(String message, byte[] signature, OneKey publicOnlyKey) throws CoseException {
		// Verify the signature
		Sign1Message verificator = (Sign1Message) Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1);
		verificator.SetContent(message);

		return verificator.validate(publicOnlyKey);
	}

}
