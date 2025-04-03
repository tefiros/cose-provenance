package com.telefonica.cose.provenance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * The JSONSignatureInterface defines a set of methods for signing JSON documents,
 * loading JSON content from files, and saving JSON nodes to files.
 *
 * @author A.Mendez
 */

public interface JSONSignatureInterface {

	/**
	 * Signs the given JSON document using the specified key identifier (KID).
	 *
	 * @param document The JSON document to be signed, represented as a {@code String}.
	 * @param kid The key identifier used for signing the document.
	 * @return A String representing the signature.
	 * @throws Exception If an error occurs during the signing process.
	 */
	String signing(String document, String kid) throws Exception;


	/**
	 * Loads a JSON document from the specified file path.
	 *
	 * @param jsonFilePath The path to the JSON file to be loaded.
	 * @return A JsonNode representing the loaded JSON content.
	 * @throws IOException If an I/O error occurs while reading the file.
	 * @throws JsonProcessingException If an error occurs while parsing the JSON content.
	 */
	JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException;

	/**
	 * Saves the given JSON node to a file with the specified file name.
	 *
	 * @param jsonNode The JsonNode to be saved to a file.
	 * @param fileName The name of the file where the JSON node will be saved.
	 * @throws Exception If an error occurs while saving the JSON node.
	 */

	void saveJSONnode(JsonNode jsonNode, String fileName) throws Exception;


}
