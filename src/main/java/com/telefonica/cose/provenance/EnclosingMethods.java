package com.telefonica.cose.provenance;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import org.jdom2.Attribute;

/**
 * Procedures to enclose the signature in the data structure
 * 
 * @author S. Garcia
 * 
 */

public class EnclosingMethods extends JSONFileManagement implements EnclosingMethodInterface{

	/**
	 * Method related to the first enclosing method proposed
	 * 
	 * @param rootNode xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
	public JsonNode enclosingMethod(JsonNode rootNode, String signature) {

//		Parameters param = new Parameters();
//
//		Element rootElementDocument = YANGprovenance.getRootElement();
//		Namespace namespace = rootElementDocument.getNamespace();
//
//		// Get bage64 provenance signature so we can store it inside the YANG structure
//		Element signatureElement = new Element(param.getProperty("Signature Element"), namespace);
//		signatureElement.setText(signature);
//
//		// Add the new provenance-string element to the root element
//		rootElementDocument.addContent(0, signatureElement);

		// Step 2: Add the leaf node (provenance-string)
		if (rootNode.isObject()) {
			ObjectNode rootObjectNode = (ObjectNode) rootNode;
			rootObjectNode.put("provenance-string", signature);
		} else {
			throw new IllegalArgumentException("The root of the JSON must be an object node");
		}


		return rootNode;

	}

/*
	*/
/**
	 * Method related to the second enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 *//*

	public Document enclosingMethod2(Document YANGprovenance, String signature) {

		Parameters param = new Parameters();

		Element rootElement = YANGprovenance.getRootElement();
		Namespace namespace = rootElement.getNamespace();

		Element notificationElement = rootElement.getChild("eventTime", namespace);

		Element provenanceElement = new Element(param.getProperty("Signature Element"), namespace);
		provenanceElement.setText(signature);

		rootElement.addContent(rootElement.indexOf(notificationElement) + 1, provenanceElement);

		return YANGprovenance;

	}

	*/
/**
	 * Method related to the third enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 *//*

	public Document enclosingMethod3(Document YANGprovenance, String signature) {

		Parameters param = new Parameters();

		Element rootElement = YANGprovenance.getRootElement();
		Namespace namespace = rootElement.getNamespace();

		Element provenanceElement = new Element(param.getProperty("Signature Element"), namespace);
		provenanceElement.setText(signature);

		Element content = rootElement.getChild("content-data", namespace);
		int index = rootElement.indexOf(content);
		rootElement.addContent(index, provenanceElement);

		return YANGprovenance;
	}

	*/
/**
	 * Method related to the fourth enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 *//*

	public Document enclosingMethod4(Document YANGprovenance, String signature) {

		Parameters param = new Parameters();

		Element rootElement = YANGprovenance.getRootElement();

		Element elementNamespace = new Element("element", "ypmd", "http://telefonica.com/temporary-ns-yangpmd");
		Namespace namespace = elementNamespace.getNamespace("ypmd");

		// RFC 7952, Section 5.1
		Attribute annotation = new Attribute(param.getProperty("Signature Element"), signature,
				Attribute.CDATA_TYPE, namespace);
		rootElement.setAttribute(annotation);

		return YANGprovenance;

	}
*/

}
