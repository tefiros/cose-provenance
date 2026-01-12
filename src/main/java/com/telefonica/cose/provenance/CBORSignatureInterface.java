package com.telefonica.cose.provenance;

import org.jdom2.Document;
import org.jdom2.JDOMException;

import java.io.IOException;


/**
 * The interface defines a contract for operations related to
 * signing and handling XML documents. It provides methods for signing a document,
 * saving it to a file, and loading an XML document from a file.
 *
 * @author A. Méndez
 */


public interface CBORSignatureInterface {



    /**
     * Signs the given document using the specified key identifier (KID).
     *
     * @param document The document to be signed, represented as a String.
     * @param kid The key identifier used for signing the document.
     * @return A String representing the signature.
     * @throws Exception If an error occurs during the signing process.
     */
    String signing(String document, String kid) throws Exception;



}
