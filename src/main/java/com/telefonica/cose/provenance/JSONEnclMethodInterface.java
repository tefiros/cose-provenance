package com.telefonica.cose.provenance;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;

/**
 * This interface defines a contract for processing JSON documents
 * using the Jackson library. It provides methods that accept an JSON document and a signature
 * string to perform specific operations.
 *
 * <p>Classes implementing this interface must provide concrete implementations for all
 * declared methods.</p>
 *
 *
 * @author A. Méndez
 *
 */

public interface JSONEnclMethodInterface {

    /**
     * Processes the given JSON document with the specified signature.
     *
     * @param YANGprovenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethodJSON(JsonNode YANGprovenance, String signature);

    /**
     * Processes the given JSON document with the specified signature.
     * For yang modules
     *
     * @param yangProvenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethodParam(JsonNode yangProvenance, String signature, String moduleName, String leafName);

    /**
     * Processes the given JSON document with the specified signature.
     * For yang modules files
     *
     * @param YANGprovenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethodYANG(JsonNode YANGprovenance, String signature, File yangModule) throws IOException;

    /**
     * Processes the given JSON document with the specified signature.
     *
     * @param YANGprovenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethod2JSON(JsonNode YANGprovenance, String signature);

    /**
     * Processes the given JSON document with the specified signature.
     *
     * @param YANGprovenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethod3JSON(JsonNode YANGprovenance, String signature);

    /**
     * Processes the given JSON document with the specified signature.
     *
     * @param YANGprovenance The JSON document to be processed.
     * @param signature The signature used in the operation.
     * @return A JsonNode representing the result of the operation.
     */
    JsonNode enclosingMethod4JSON(JsonNode YANGprovenance, String signature);
}