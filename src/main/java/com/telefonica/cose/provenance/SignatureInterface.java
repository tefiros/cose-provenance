package com.telefonica.cose.provenance;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

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
	
	/**
	 * interface method
	 */
	void saveXMLDocument(Document document, String fileName) throws Exception;
	
	/**
	 * interface method
	 */
	Document loadXMLDocument(String xmlFilePath)throws JDOMException, IOException ;

}

