package com.telefonica.cose.provenance;

import java.io.IOException;

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
	 * Signs the given document using the specified key identifier (KID).
	 *
	 * @param document The document to be signed, represented as a String.
	 * @param kid The key identifier used for signing the document.
	 * @return A String representing the signature.
	 * @throws Exception If an error occurs during the signing process.
	 */
	String signing(String document, String kid) throws Exception;

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

}

