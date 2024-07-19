package com.telefonica.api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import org.jdom2.Attribute;
import org.jdom2.Content;

public class EnclosingMethods extends XMLFileManagement{
	
	/**
	 * This is the method related to the first enclosing method proposed
	 * 
	 * @param YANGFile  xml file where the signature is to be enclosed
	 * @param signature signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Document enclosingMethod(String YANGprovenance, String signature) {

		Parameters param = new Parameters();
		Document document = null;

		// Charge the existing XML file
		try {
			document = loadXMLDocument(YANGprovenance);

			Element rootElementDocument = document.getRootElement();
			Namespace namespace = rootElementDocument.getNamespace();

			// Get bage64 provenance signature so we can store it inside the YANG structure
			Element signatureElement = new Element(param.getProperty("Signature Element"), namespace);
			signatureElement.setText(signature);

			// Add the new provenance-string element to the root element
			rootElementDocument.addContent(0, signatureElement);

			// Save the updated XML document that includes the content and the signature
			// saveXMLDocument(document, signatureFile);

		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;

	}

	public Document enclosingMethod2(String YANGprovenance, String signature) {

		Parameters param = new Parameters();
		Document document = null;

		try {
			document = loadXMLDocument(YANGprovenance);

			Element rootElement = document.getRootElement();
			Namespace namespace = rootElement.getNamespace();

			Element notificationElement = rootElement.getChild("eventTime", namespace);

			Element provenanceElement = new Element(param.getProperty("Signature Element"), namespace);
			provenanceElement.setText(signature);

			rootElement.addContent(rootElement.indexOf(notificationElement) + 1, provenanceElement);


		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return document;

	}
	
	public Document enclosingMethod4(String YANGprovenance, String signature) {

		Parameters param = new Parameters();
		Document document = null;

		try {
			document = loadXMLDocument(YANGprovenance);

			Element rootElement = document.getRootElement();
			
			Element elementNamespace = new Element("element", "ypmd", "http://telefonica.com/temporary-ns-yangpmd");
			Namespace namespace = elementNamespace.getNamespace("ypmd");
			
			//RFC 7952, Section 5.1
			Attribute annotation = new Attribute(param.getProperty("Signature Element"), signature, Attribute.CDATA_TYPE, namespace);
			rootElement.setAttribute(annotation);
			
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return document;

	}

}
