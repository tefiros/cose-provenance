package com.telefonica.cose.provenance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface JSONSignatureInterface {
	/**
	 * interface method
	 */
	String signing(String document, String kid) throws Exception;


	/**
	 * interface method
	 */
	JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException;

	/**
	 * interface method
	 */

	void saveJSONnode(JsonNode jsonNode, String fileName) throws Exception;


}
