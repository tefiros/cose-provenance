package com.telefonica.cose.provenance;

import java.io.*;
import java.util.Properties;

import com.google.gson.Gson;

/**
* Class to handle parameters to be configured
* 
* @author A.Méndez
* 
*/

public class Parameters {

	/**
	 * @return configuration file
	 */
	private static String readFile() {

		StringBuilder fichero = new StringBuilder();

		try (InputStream inputStream = Parameters.class.getClassLoader().getResourceAsStream("cose_parameters.json");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

			String linea;
			while ((linea = br.readLine()) != null) {
				fichero.append(linea);
			}

		} catch (IOException | NullPointerException ex) {
			System.out.println("Error reading resource: " + ex.getMessage());
		}

		return fichero.toString();

	}
	
	/**
	 * Method to access parameters within the configuration file
	 * 
	 * @param parameter name of the parameter to use
	 * @return specified parameter
	 */
	public String getProperty(String parameter) {
		
		String fichero = readFile();

		Gson gson = new Gson();

		// Obtener las propiedades
		Properties properties = gson.fromJson(fichero, Properties.class);
		// Ahora puedes acceder a las propiedades como:
		String prop = properties.getProperty(parameter);

		return prop;
		
	}

}
