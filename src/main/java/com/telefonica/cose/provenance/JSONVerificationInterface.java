package com.telefonica.cose.provenance;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * The JSONVerificationInterface defines operations for verifying JSON documents
 * and loading them from files. Implementations typically validate JSON structure/content
 *
 *
 * @author A. Mendez
 */

public interface JSONVerificationInterface {

    /**
     * Validates whether the given JSON node meets specific, integrity is preserved.
     *
     * @param jsonNode The JSON data to verify, represented as a JsonNode
     * @return true if the JSON is valid, false otherwise
     * @throws Exception If validation fails due to structural issues or I/O errors
     */
    boolean verify(JsonNode jsonNode) throws Exception;

    /**
     * Loads and parses a JSON document from the specified file path.
     *
     * @param jsonFilePath Path to the JSON file
     * @return JsonNode representing the parsed JSON content
     * @throws IOException If the file cannot be read
     * @throws JsonProcessingException If the file contains invalid JSON syntax
     */
    JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException;

}
