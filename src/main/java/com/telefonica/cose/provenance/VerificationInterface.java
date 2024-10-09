package com.telefonica.cose.provenance;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;

/**
 * Verification interface class
 * 
 * @author S. Garcia
 */

public interface VerificationInterface {
	
	/**
	 * interface method
	 */
	boolean verify(Document documento) throws Exception;

	/**
	 * interface method
	 */
	Document loadXMLDocument(String xmlFilePath)throws JDOMException, IOException ;

}
