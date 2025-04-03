package com.telefonica.cose.provenance;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;

/**
 * Verification interface class
 * 
 * @author A.Mendez
 */

public interface XMLVerificationInterface {

	/**
	 * Verifies the given XML document.
	 *
	 * @param documento The document to be verified.
	 * @return A boolean indicating whether the document passed verification.
	 * @throws Exception If an error occurs during the verification process.
	 */
	boolean verify(Document documento) throws Exception;

	/**
	 * Loads an XML document from the specified file path.
	 *
	 * @param xmlFilePath The path to the XML file to be loaded.
	 * @return A Document representing the loaded XML content.
	 * @throws JDOMException If an error occurs while parsing the XML file.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	Document loadXMLDocument(String xmlFilePath)throws JDOMException, IOException ;

}
