package com.telefonica.cose.provenance;

import java.io.File;
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
import java.util.Map;
import java.util.Map.Entry;

import org.jdom2.Attribute;

/**
 * Procedures to enclose the signature in the data structure
 *
 * @author A. Mendez
 *
 */

public class JSONEnclosingMethods extends JSONFileManagement implements JSONEnclMethodInterface{

    /**
     * Method related to the first enclosing method proposed
     *
     * @param rootNode json file where the signature is to be enclosed
     * @param signature      signature to include in the YANG data provenance
     * @return JsonNode of the YANG data provenance with the new signature element
     *         integrated
     */
    public JsonNode enclosingMethodJSON(JsonNode rootNode, String signature) {
        if (!rootNode.isObject()) {
            throw new IllegalArgumentException("The root of the JSON must be an object node");
        }

        ObjectNode rootObjectNode = (ObjectNode) rootNode;

        // Find the first inner object node inside root
        for (Iterator<Map.Entry<String, JsonNode>> it = rootObjectNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue().isObject()) {
                ((ObjectNode) entry.getValue()).put("provenance-string", signature);
                return rootNode;
            }
        }

        // If no inner object exists, throw an error
        throw new IllegalArgumentException("No inner object found to add the provenance-string");
    }

    /**
     * Inserta una firma en un documento JSON YANG, usando el formato module:leaf.
     *
     * @param yangProvenance Documento JSON original (como árbol Jackson)
     * @param signature Firma base64 a insertar
     * @param moduleName Nombre del módulo YANG (por ejemplo "interfaces-provenance-augmented")
     * @param leafName Nombre del leaf augmentado (por ejemplo "interfaces-provenance")
     * @return Documento JSON con la firma insertada
     */
    public JsonNode enclosingMethodParam(JsonNode yangProvenance, String signature, String moduleName, String leafName) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootCopy = yangProvenance.deepCopy();

        // Supongamos que el root tiene un único top-level como "ietf-interfaces:interfaces"
        String rootKey = rootCopy.fieldNames().next();
        JsonNode innerNode = rootCopy.get(rootKey);

        if (innerNode.isObject()) {
            ObjectNode innerObject = (ObjectNode) innerNode;

            // Creamos el nombre completo del campo para la firma
            String signatureField = moduleName + ":" + leafName;

            // Añadimos la firma al nivel superior del bloque principal
            innerObject.put(signatureField, signature);

            // Volvemos a insertar el objeto modificado
            rootCopy.set(rootKey, innerObject);
        } else {
            System.err.println("El documento JSON no tiene la estructura esperada (objeto raíz dentro de otro).");
        }

        return rootCopy;
    }

    public JsonNode enclosingMethodYANG(JsonNode YANGprovenance, String signature, File yangModule) throws IOException {
        YANGMetadata metadata = YANGModuleProcessor.extractSignatureMetadata(yangModule);
        String moduleName = YANGModuleProcessor.extractModuleName(yangModule);
        return enclosingMethodParam(YANGprovenance, signature, moduleName, metadata.getLeafName());
    }


    /**
     * Method related to the second enclosing method proposed
     *
     * @param rootNode json file where the signature is to be enclosed
     * @param signature      signature to include in the YANG data provenance
     * @return JsonNode of the YANG data provenance with the new signature element
     *         integrated in a notification envelope type
     */

    public JsonNode enclosingMethod2JSON(JsonNode rootNode, String signature) {

        // Define the key patterns that indicate Extensible YANG Model for YANG-Push Notifications (Envelopes)
        String[] notificationKeys = {"ietf-yp-notification:envelope"};

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
                        notificationNode.put("ietf-yp-provenance:provenance", signature);
                        return rootNode;
                    }
                }
            }
            throw new IllegalArgumentException("The JSON does not contain a valid notification envelope object node");
        } else {
            throw new IllegalArgumentException("The root of the JSON must be an object node");
        }

    }


    /**
     * Method related to the third enclosing method proposed
     *
     * @param rootNode json file where the signature is to be enclosed
     * @param signature      signature to include in the YANG data provenance
     * @return jsonnode of the YANG data provenance with the new signature element
     *         integrated
     */

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode enclosingMethod3JSON(JsonNode rootNode, String signature) {

//		if (rootNode.isObject()) {
//			ObjectNode rootObjectNode = (ObjectNode) rootNode;
//
//			// Create the metadata object
//			ObjectNode metadataNode = objectMapper.createObjectNode();
//			metadataNode.put("provenance-string", signature);
//
//			// Add the metadata object to the root node
//			rootObjectNode.set("@", metadataNode);
//		} else {
//			throw new IllegalArgumentException("The root of the JSON must be an object node");
//		}

        if (rootNode.isObject()) {
            ObjectNode rootObjectNode = (ObjectNode) rootNode;

            // Check if "ietf-yang-instance-data:instance-data-set" exists
            if (rootObjectNode.has("ietf-yang-instance-data:instance-data-set")) {
                // Access the "ietf-yang-instance-data:instance-data-set" object
                ObjectNode instanceDataSetNode = (ObjectNode) rootObjectNode.get("ietf-yang-instance-data:instance-data-set");

                // Add the "provenance-string" inside "ietf-yang-instance-data:instance-data-set"
                instanceDataSetNode.put("provenance-string", signature);
            } else {
                throw new IllegalArgumentException("The JSON must contain the 'ietf-yang-instance-data:instance-data-set' key");
            }
        } else {
            throw new IllegalArgumentException("The root of the JSON must be an object node");
        }

        return rootNode;

    }



    /**
     * Method related to the fourth enclosing method proposed
     *
     * @param rootNode xml file where the signature is to be enclosed
     * @param signature      signature to include in the YANG data provenance
     * @return JDOM of the YANG data provenance with the new signature element
     *         integrated
     */

    // private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode enclosingMethod4JSON(JsonNode rootNode, String signature) {
        // Ensure the root node is an object
        if (!rootNode.isObject()) {
            throw new IllegalArgumentException("The root of the JSON must be an object node.");
        }

        ObjectNode rootObjectNode = (ObjectNode) rootNode;

        // Iterate through all fields of the root node
        Iterator<Map.Entry<String, JsonNode>> fields = rootObjectNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();

            // If the field value is an object node, add the metadata
            if (value.isObject()) {
                ObjectNode targetObjectNode = (ObjectNode) value;
                targetObjectNode.put("@ypmd:provenance-string", signature);
            }
        }

        return rootNode;

    }


}