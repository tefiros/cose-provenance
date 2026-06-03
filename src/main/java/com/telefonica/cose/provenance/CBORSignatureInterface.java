package com.telefonica.cose.provenance;

import COSE.CoseException;
import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.List;


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

    /**
     * Signs a generic CBOR object.
     *
     * @param cbor CBORObject to sign
     * @param kid  key ID to use for signing
     * @return Base64-encoded COSE_Sign1 signature
     * @throws COSESignatureException on COSE signing errors
     * @throws CoseException          on COSE library errors
     */

    byte[] signingCBOR(CBORObject cbor, String kid) throws COSESignatureException, CoseException;

    /**
     * Multi-Signs the given document using the specified key identifiers (KIDs).
     *
     * @param cbor The document to be signed, represented as a String.
     * @param kids The key identifier used for signing the document.
     * @return A String representing the signature.
     * @throws Exception If an error occurs during the signing process.
     */
    public byte[] multiSigning(CBORObject cbor, List<String> kids) throws CoseException, COSESignatureException;


}
