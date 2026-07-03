package com.telefonica.cose.provenance;

import java.io.IOException;
import java.util.List;

import COSE.CoseException;
import com.telefonica.cose.provenance.exception.COSESignatureException;
import com.upokecenter.cbor.CBORObject;
import org.jdom2.Document;
import org.jdom2.JDOMException;

/**
 * The interface defines a contract for operations related to
 * signing and handling XML documents. It provides methods for signing a document,
 * saving it to a file, and loading an XML document from a file.
 *
 * @author A. Méndez
 */

public interface XMLSignatureInterface {


	/**
	 * Signs a document using a single key identifier (KID).
	 *
	 * @param document The XML document to be signed, represented as a String.
	 * @param kid The key identifier used for signing.
	 * @return A signed representation of the document.
	 * @throws CoseException If an error occurs during COSE processing.
	 * @throws COSESignatureException If the signature process fails.
	 */
	String signing(String document, String kid)
			throws CoseException, COSESignatureException;

	/**
	 * Saves the given XML document to a file with the specified file name.
	 *
	 * @param document The XML document to be saved, represented as a JDOM document.
	 * @param fileName The name of the file where the document will be saved.
	 * @throws Exception If an error occurs while saving the document.
	 */
	void saveXMLDocument(Document document, String fileName) throws Exception;

	/**
	 * Loads an XML document from the specified file path.
	 *
	 * @param xmlFilePath The path to the XML file to be loaded.
	 * @return A JDOM document representing the loaded XML content.
	 * @throws JDOMException If an error occurs while parsing the XML file.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	Document loadXMLDocument(String xmlFilePath)throws JDOMException, IOException ;

	/**
	 * Multi-Signs the given document using the specified key identifiers (KIDs).
	 *
	 * @param document The document to be signed, represented as a String.
	 * @param kids The key identifier used for signing the document.
	 * @return A String representing the signature.
	 */
	public String multiSigning(String document, List<String> kids) throws CoseException, COSESignatureException;



	/**
	 * Adds a countersignature to an already signed XML document.
	 *
	 * @param document The original XML document.
	 * @param kid The key identifier used to generate the countersignature.
	 * @param signatureElement The XML element containing the original signature.
	 * @return The updated document including the countersignature.
	 * @throws CoseException If an error occurs during COSE processing.
	 * @throws COSESignatureException If the countersignature process fails.
	 */
	String addCounterSign(String document, String kid, String signatureElement)
			throws CoseException, COSESignatureException;



}

