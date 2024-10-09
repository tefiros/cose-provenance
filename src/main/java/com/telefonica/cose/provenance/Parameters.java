package com.telefonica.cose.provenance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.gson.Gson;

/**
* Class to handle parameters to be configured
* 
* @author S. Garcia
* 
*/

public class Parameters {

	/**
	 * @return configuration file
	 */
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
