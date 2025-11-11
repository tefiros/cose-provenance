package com.telefonica.cose.provenance;

import org.jdom2.Document;

import java.io.File;
import java.io.IOException;

/**
 * This interface defines a contract for processing XML documents
 * using the JDOM2 library. It provides methods that accept an XML document and a signature
 * string to perform specific operations.
 *
 * <p>Classes implementing this interface must provide concrete implementations for all
 * declared methods.</p>
 *
 * <p>This interface is designed to facilitate operations on XML documents in scenarios
 * where signatures are involved, such as provenance tracking or validation.</p>
 *
 * @author A. Méndez
 *
 */

public interface XMLEnclosingMethodInterface {

	/**
	 * Processes the given XML document with the specified signature.
	 *
	 * @param YANGprovenance The XML document to be processed.
	 * @param signature The signature used in the operation.
	 * @return A JDOM representing the result of the operation.
	 */
	Document enclosingMethod(Document YANGprovenance, String signature);

	/**
	 * Processes the given XML document with the specified signature.
	 * For yang modules
	 *
	 * @param YANGprovenance The XML document to be processed.
	 * @param signature The signature used in the operation.
	 * @return A JDOM representing the result of the operation.
	 */
	Document enclosingMethodParam(Document YANGprovenance, String signature, String signatureElement, String signatureNS);

	/**
	 * Processes the given XML document with the specified signature.
	 * For yang modules files
	 *
	 * @param YANGprovenance The XML document to be processed.
	 * @param signature The signature used in the operation.
	 * @return A JDOM representing the result of the operation.
	 */
	Document enclosingMethodYANG(Document YANGprovenance, String signature, File yangModule) throws IOException;
	/**
	 * Method related to the second enclosing method proposed
	 *
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *
	 *         integrated
	 */
	Document enclosingMethod2(Document YANGprovenance, String signature);

	/**
	 * Method related to the third enclosing method proposed
	 *
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
	Document enclosingMethod3(Document YANGprovenance, String signature);

	/**
	 * Method related to the fourth enclosing method proposed
	 *
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
	Document enclosingMethod4(Document YANGprovenance, String signature);
}
