package com.telefonica.cose.provenance;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jdom2.Document;
import org.jdom2.JDOMException;

/**
 * Signer interface class
 * 
 * @author S. Garcia
 */

public interface SignatureInterface {

	/**
	 * interface method
	 */
	String signing(String document, String kid) throws Exception;

	JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException;

	/*
	*/
/**
	 * interface method
	 */

	void saveJSONnode(JsonNode jsonNode, String fileName) throws Exception;



/**
	 * interface method
	 *//*

	Document loadXMLDocument(String xmlFilePath)throws JDOMException, IOException ;
*/
}
