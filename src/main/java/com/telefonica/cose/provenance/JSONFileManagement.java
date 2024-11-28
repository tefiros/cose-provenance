package com.telefonica.cose.provenance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.erdtman.jcs.JsonCanonicalizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * JSON files management
 *
 * @author A. Méndez
 *
 */

public class JSONFileManagement {

    /**
     * This method canonicalizes the JSON with the official JCS scheme [RFC 8785]
     *
     * @param jsonNode file to be canonicalized
     * @return canonicalized json File as a String
     */
    public String canonicalizeJSON(JsonNode jsonNode) {
        try {
            String jsonString = jsonNode.toString();
            JsonCanonicalizer canonicalizer = new JsonCanonicalizer(jsonString);
            return canonicalizer.getEncodedString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method loads the json as a JsonNode
     *
     * @param jsonFilePath file path String of the json to be loaded
     * @return JsonNode
     * @throws JsonProcessingException exception that occurs in the Jackson library, which is
     *                       used for manipulating JSON data in Java
     * @throws IOException   exception that occurs during Input/Output (I/O)
     *                       operations
     */
    public JsonNode loadJSONDocument(String jsonFilePath) throws IOException, JsonProcessingException {
        // Use an ObjectMapper to load the JSON document
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(new File(jsonFilePath));
    }


    public static void main(String[] args) throws IOException {
        JSONFileManagement loader = new JSONFileManagement();
        JsonNode jsonNode = loader.loadJSONDocument("sample1.json");

        String canonicalizedJSON = loader.canonicalizeJSON(jsonNode);

        // Output the result
        if (canonicalizedJSON != null) {
            System.out.println("Canonicalized JSON:");
            System.out.println(canonicalizedJSON);
        } else {
            System.out.println("Error during canonicalization.");
        }
    }
}
