/**
 * 
 */
package cose_implementation_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;

import com.upokecenter.cbor.CBORObject;

import COSE.AlgorithmID;
import COSE.CoseException;
import COSE.OneKey;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;

/**
 * This class contains the data to be used for the creation and verification of the signatures
 */
public class Data {
	
	static {
		EdDSASecurityProvider provider = new EdDSASecurityProvider();
		Security.addProvider(provider);
	}

	static Path filepath = Path.of("C:/Users/idb0095/Documents/Holamundo.txt");

	public String Document() throws IOException {
		byte[] doc = Files.readAllBytes(filepath);
		String documento = new String(doc, "UTF-8");
		return documento;
	}

	public OneKey Keypair() throws CoseException {
		OneKey keypair = OneKey.generateKey(AlgorithmID.EDDSA);
		return keypair;
	}

	public OneKey Publickey() throws CoseException, IOException, ClassNotFoundException {
		
		Path keyIn = Path.of("C:/Users/idb0095/Documents/COSEdocs/publickey.cose");
		byte[] key = Files.readAllBytes(keyIn);
		CBORObject key1 = CBORObject.DecodeFromBytes(key);
		OneKey publickey = new OneKey(key1);
		return publickey;
		
 	}

}
