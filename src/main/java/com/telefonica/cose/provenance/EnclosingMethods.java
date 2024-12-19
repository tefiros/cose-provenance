package com.telefonica.cose.provenance;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import java.util.Iterator;
import java.util.Map.Entry;

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

		// Add the leaf node (provenance-string)
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
	 * @param rootNode xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */

	public JsonNode enclosingMethod2(JsonNode rootNode, String signature) {

		// Define the key patterns that indicate NETCONF/RESTCONF notifications
		String[] notificationKeys = {"ietf-restconf:notification", "ietf-notification:notification"};

		// Traverse the JSON to find the notification node
		if (rootNode.isObject()) {
			ObjectNode rootObjectNode = (ObjectNode) rootNode;

			// Iterate through the entries to find the notification node
			Iterator<Entry<String, JsonNode>> fields = rootObjectNode.fields();
			while (fields.hasNext()) {
				Entry<String, JsonNode> field = fields.next();
				String fieldName = field.getKey();
				JsonNode valueNode = field.getValue();

				// Check if the current field is one of the notification keys
				for (String notificationKey : notificationKeys) {
					if (fieldName.equals(notificationKey) && valueNode.isObject()) {
						ObjectNode notificationNode = (ObjectNode) valueNode;
						// Add the provenance-string node at the same level as eventTime
						notificationNode.put("provenance-string", signature);
						return rootNode;
					}
				}
			}
			throw new IllegalArgumentException("The JSON does not contain a valid NETCONF/RESTCONF notification object node");
		} else {
			throw new IllegalArgumentException("The root of the JSON must be an object node");
		}

	}


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

		//ponerlo al nivel de content-name
	}

	*/
/**
 * This method in JSON would have been the same as method 1. In JSON there is not an specific translation for an annotation
	 * Method related to the fourth enclosing method proposed
	 * 
	 * @param rootNode xml file where the signature is to be enclosed
	 * @param signature      signature to include in the YANG data provenance
	 * @return JDOM of the YANG data provenance with the new signature element
	 *         integrated
	 */

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public JsonNode enclosingMethod3(JsonNode rootNode, String signature) {

		if (rootNode.isObject()) {
			ObjectNode rootObjectNode = (ObjectNode) rootNode;

			// Create the metadata object
			ObjectNode metadataNode = objectMapper.createObjectNode();
			metadataNode.put("ypmd:provenance-string", signature);

			// Add the metadata object to the root node
			rootObjectNode.set("@", metadataNode);
		} else {
			throw new IllegalArgumentException("The root of the JSON must be an object node");
		}

		return rootNode;

	}


}
