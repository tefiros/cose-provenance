package com.telefonica.cose.provenance;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jdom2.Document;
import org.jdom2.JDOMException;

/**
 * Verification interface class
 * 
 * @author S. Garcia
 */

public interface VerificationInterface {
	
	/**
	 * interface method
	 */
	boolean verify(JsonNode jsonNode) throws Exception;

	/**
	 * interface method
	 */
	JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException;

}
