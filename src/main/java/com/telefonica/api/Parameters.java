package com.telefonica.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.gson.Gson;

public class Parameters {

	private static String readFile() {

		String fichero = "";

		try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/cose_parameters.json"))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				fichero += linea;
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		return fichero;

	}
	
	public String getProperty(String property) {
		
		String fichero = readFile();

		Gson gson = new Gson();

		// Obtener las propiedades
		Properties properties = gson.fromJson(fichero, Properties.class);
		// Ahora puedes acceder a las propiedades como:
		String prop = properties.getProperty(property);

		return prop;
		
	}

//	public String readpswd() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("Password");
//
//		return pswd;
//	}
//
//	public String readInstanceKeyStore() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("KeyStore Instance");
//
//		return pswd;
//	}
//
//	public String readSignerKeyStore() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("Signer KeyStore");
//
//		return pswd;
//	}
//
//	public String readReceiverKeyStore() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("Receiver KeyStore");
//
//		return pswd;
//	}
//
//	public String readKid() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("kid");
//
//		return pswd;
//	}
//
//	public String readAlg() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("alg");
//
//		return pswd;
//	}
//	
//	public String readContentType() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("Content Type");
//
//		return pswd;
//	}
//
//
//	public String readSignElement() {
//
//		String fichero = readFile();
//
//		Gson gson = new Gson();
//
//		// Obtener las propiedades
//		Properties properties = gson.fromJson(fichero, Properties.class);
//		// Ahora puedes acceder a las propiedades como:
//		String pswd = properties.getProperty("Signature Element");
//
//		return pswd;
//	}

}
