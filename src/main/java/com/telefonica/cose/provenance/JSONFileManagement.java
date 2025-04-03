package com.telefonica.cose.provenance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.erdtman.jcs.JsonCanonicalizer;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


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
     * @param jsonString file to be canonicalized
     * @return canonicalized json File as a String
     */
    public String canonicalizeJSON(String jsonString) {
        try {
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
     * @throws IOException   exception that occurs during Input/Output (I/O)
     *                       operations
     */
    public JsonNode loadJSONDocument(String jsonFilePath) throws IOException {
        // Use an ObjectMapper to load the JSON document
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(new File(jsonFilePath));
    }

    /**
     * This method saves the JSON as a File
     *
     * @param fileName JsonNode to be stored as a String
     * @param jsonNode String of the file path where the JDOM will be stored
     */
    public void saveJSONnode(JsonNode jsonNode, String fileName) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convert the JsonNode to a JSON string
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

            // Write the JSON string to the specified file
            FileWriter writer = new FileWriter(fileName);
            writer.write(jsonString);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*
    // run example
    public static void main(String[] args) throws IOException {
        JSONFileManagement loader = new JSONFileManagement();
        // JsonNode jsonNode = loader.loadJSONDocument("sample1.json");

        String jsonString = "{"
                + "\"name\": \"Alice\","
                + "\"age\": 30,"
                + "\"city\": \"New York\","
                + "\"hobbies\": [\"reading\", \"traveling\", \"coding\"],"
                + "\"nested\": {\"key1\": \"value1\", \"key2\": \"value2\"}"
                + "}";


        String canonicalizedJSON = loader.canonicalizeJSON(jsonString);

        // Output the result
        if (canonicalizedJSON != null) {
            System.out.println("Canonicalized JSON:");
            System.out.println(canonicalizedJSON);
        } else {
            System.out.println("Error during canonicalization.");
        }
    }

 */
}