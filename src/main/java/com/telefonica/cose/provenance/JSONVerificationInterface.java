package com.telefonica.cose.provenance;

import java.io.IOException;

import COSE.CoseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.cose.provenance.exception.COSESignatureException;


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


    /**
     * Verifies a YANG-based JSON document by extracting a signature field
     * using the given module and field names.
     *
     * This method removes the signature field, canonicalizes the remaining
     * JSON content, and validates the COSE_Sign1 signature.
     *
     * @param yangJson The JSON document containing YANG data and provenance signature
     * @param moduleName Name of the YANG module
     * @param signatureField Name of the signature leaf field
     * @return true if the signature is valid
     * @throws CoseException If COSE processing fails
     * @throws COSESignatureException If extraction or validation fails
     */
    boolean verifyYANG(JsonNode yangJson, String moduleName, String signatureField) throws CoseException, COSESignatureException;

    /**
     * Verifies both the main COSE_Sign1 signature and all associated countersignatures.
     *
     * This method:
     * - validates the primary signer (origin authenticity)
     * - iterates over all countersignatures
     * - validates each countersignature independently
     *
     * Countersignatures represent additional attestations or endorsements over
     * the original signature and payload.
     *
     * @param YANGfile JSON document containing the signed payload and countersignatures
     * @return true if the main signature and all countersignatures are valid
     * @throws CoseException If COSE processing fails
     * @throws COSESignatureException If validation fails
     * @throws JsonProcessingException If JSON parsing fails
     */
    public boolean verifyJSONWithCountersigns(
            JsonNode YANGfile,
            String moduleName,
            String signatureField)
            throws CoseException, COSESignatureException, JsonProcessingException;

    /**
     * Verifies a single countersignature identified by its key identifier (kid).
     * The method:
     * - extracts and canonicalizes the JSON payload
     * - locates the countersignature with the given kid
     * - validates only that specific countersignature
     *
     * @param YANGfile JSON document containing the signed payload
     * @param targetKid The key identifier (kid) of the countersignature to verify
     * @return true if the specified countersignature is valid
     *         false if it is invalid or not found
     * @throws CoseException If COSE processing fails
     * @throws COSESignatureException If extraction or validation fails
     * @throws JsonProcessingException If JSON parsing fails
     */
    public boolean verifyJSONCounterSignByKid(
            JsonNode YANGfile,
            String moduleName,
            String signatureField,
            String targetKid)
            throws CoseException, COSESignatureException, JsonProcessingException;
}



