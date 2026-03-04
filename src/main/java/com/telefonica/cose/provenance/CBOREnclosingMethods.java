package com.telefonica.cose.provenance;


import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;

import java.io.File;
import java.io.IOException;

/**
 * Procedures to enclose the signature in a CBOR data structure
 *
 * @author A. Mendez
 *
 */

public class CBOREnclosingMethods extends CBORFileManagement implements  CBOREnclosingMethodsInterface {

    /**
     * Method related to the first enclosing method proposed / testing phase method
     *
     * @param rootNode json file where the signature is to be enclosed
     * @param signature      signature to include in the YANG data provenance
     * @return CBORObject of the YANG data provenance with the new signature element
     *         integrated
     */


    public CBORObject enclosingMethodCBOR(CBORObject rootNode, byte[] signature) {

        if (!rootNode.getType().equals(CBORType.Map)) {
            throw new IllegalArgumentException("The root of the CBOR must be a map");
        }

        // Iterar sobre las entradas del mapa raíz
        for (CBORObject key : rootNode.getKeys()) {

            CBORObject value = rootNode.get(key);

            // Buscar el primer mapa interno
            if (value.getType().equals(CBORType.Map)) {

                // Insertar la firma como ByteString
                value.Add("provenance-signature", CBORObject.FromObject(signature));

                return rootNode;
            }
        }

        throw new IllegalArgumentException("No inner map found to add the provenance-signature");
    }


    /**
     * Inserts a COSE signature into a YANG CBOR document using the
     * "moduleName:leafName" format as defined by the YANG augment.
     *
     *
     * @param yangProvenance the original YANG document encoded as a CBORObject.
     *                       Must be of type CBORType.Map.
     * @param signature      the COSE_Sign1 signature as raw bytes.
     *                       This will be inserted as a CBOR ByteString.
     * @param moduleName     the YANG module name defining the augment.
     * @param leafName       the leaf name where the signature must be stored.
     *
     * @return a new CBORObject containing the inserted signature field.
     *
     * @throws IllegalArgumentException if the CBOR structure does not match the
     *                                  expected YANG top-level map structure.
     */


    public CBORObject enclosingMethodParamCBOR(
            CBORObject yangProvenance,
            byte[] signature,
            String moduleName,
            String leafName) {

        if (!yangProvenance.getType().equals(CBORType.Map)) {
            throw new IllegalArgumentException("Root CBOR must be a map");
        }

        // Copia profunda para no modificar el original
        CBORObject rootCopy = CBORObject.DecodeFromBytes(yangProvenance.EncodeToBytes());

        // Suponemos que hay un único top-level (ej: "ietf-interfaces:interfaces")
        CBORObject rootKey = rootCopy.getKeys().iterator().next();
        CBORObject innerNode = rootCopy.get(rootKey);

        if (!innerNode.getType().equals(CBORType.Map)) {
            throw new IllegalArgumentException(
                    "Unexpected CBOR structure: inner node is not a map");
        }

        // module:leaf
        String signatureField = moduleName + ":" + leafName;

        // Insertar firma como ByteString (MUY importante)
        innerNode.Add(signatureField, CBORObject.FromObject(signature));

        // Reinsertar (aunque técnicamente ya está modificado)
        rootCopy.set(rootKey, innerNode);

        return rootCopy;
    }

    /**
     * Inserts a COSE signature into a YANG CBOR document by automatically
     * extracting the module name and signature leaf metadata from a YANG module file.
     *
     *
     * @param yangProvenance the original YANG CBOR document.
     * @param signature      the COSE_Sign1 signature as raw bytes.
     * @param yangModule     the YANG module file used to extract metadata
     *                       (module name and leaf definition).
     *
     * @return a CBORObject containing the signature inserted according
     *         to the YANG module definition.
     *
     * @throws IOException if the YANG module file cannot be read or parsed.
     * @throws IllegalArgumentException if the CBOR structure does not match
     *                                  the expected YANG encoding.
     */


    public CBORObject enclosingMethodYANGCBOR(
            CBORObject yangProvenance,
            byte[] signature,
            File yangModule) throws IOException {

        YANGMetadata metadata = YANGModuleProcessor.extractSignatureMetadata(yangModule);
        String moduleName = YANGModuleProcessor.extractModuleName(yangModule);

        return enclosingMethodParamCBOR(
                yangProvenance,
                signature,
                moduleName,
                metadata.getLeafName()
        );
    }

}
