package com.telefonica.cose.provenance;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import org.jdom2.Attribute;

import java.io.File;
import java.io.IOException;

/**
 * Procedures to enclose the signature in the XML data structure
 * 
 * @author A. Mendez
 * 
 */

public class XMLEnclosingMethods extends XMLFileManagement implements XMLEnclosingMethodInterface{

	/**
	 * Method related to the first enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
	public Document enclosingMethod(Document YANGprovenance, String signature) {

		Parameters param = new Parameters();
		
		Element rootElementDocument = YANGprovenance.getRootElement();
		Namespace namespace = rootElementDocument.getNamespace();

		// Get bage64 provenance signature so we can store it inside the YANG structure
		Element signatureElement = new Element(param.getProperty("Signature Element"), namespace);
		signatureElement.setText(signature);

		// Add the new provenance-string element to the root element
		rootElementDocument.addContent(0, signatureElement);

		return YANGprovenance;

	}

	public Document enclosingMethodParam(Document YANGprovenance, String signature, String signatureElement, String signatureNS) {
		Element rootElementDocument = YANGprovenance.getRootElement();
		Namespace signatureNamespace = Namespace.getNamespace(signatureNS);

		Element sigElement = new Element(signatureElement, signatureNamespace);
		sigElement.setText(signature);
		rootElementDocument.addContent(0, sigElement);

		return YANGprovenance;
	}



	public Document enclosingMethodYANG(Document YANGprovenance, String signature, File yangModule) throws IOException {
		YANGMetadata metadata = YANGModuleProcessor.extractSignatureMetadata(yangModule);
		return enclosingMethodParam(YANGprovenance, signature, metadata.getLeafName(), metadata.getNamespace());
	}



	/**
	 * Method related to the second enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated into a notification envelope doc
	 */
	public Document enclosingMethod2(Document YANGprovenance, String signature) {

		Parameters param = new Parameters();

		Element rootElement = YANGprovenance.getRootElement();
		Namespace namespace = Namespace.getNamespace("urn:ietf:params:xml:ns:yang:ietf-yp-provenance");

		Element notificationElement = rootElement.getChild("event-time", namespace);

		Element provenanceElement = new Element(param.getProperty("Notification Element"), namespace);
		provenanceElement.setText(signature);

		rootElement.addContent(rootElement.indexOf(notificationElement) + 1, provenanceElement);

		return YANGprovenance;
	}

	/**
	 * Method related to the third enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
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

	/**
	 * Method related to the fourth enclosing method proposed
	 * 
	 * @param YANGprovenance xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */
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

}
