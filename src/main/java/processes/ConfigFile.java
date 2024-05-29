package processes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import com.google.gson.Gson;

public class ConfigFile {

	public static void main(String[] args) {

		Gson gson = new Gson();

		// Crear un objeto Properties
		Properties persona = new Properties();
		persona.setProperty("Password", "18marzo01");
		persona.setProperty("KeyStore Instance", "PKCS12");
		persona.setProperty("Signer KeyStore", "C:/Users/idb0095/Documents/COSEdocs/sender_keystore.p12");
		persona.setProperty("Receiver KeyStore", "C:/Users/idb0095/Documents/COSEdocs/receiver_keystore.p12");
		persona.setProperty("kid", "telefonica.com");
		persona.setProperty("alg", "");
		persona.setProperty("Content Type", "xml");
		persona.setProperty("Signature Element", "provenance-signature");

		// Convertir a JSON
		String json = gson.toJson(persona);

		// Guardar en un archivo
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("cose_parameters.json"))) {
			bw.write(json);
			System.out.println("File updated");
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
