package com.telefonica.cose.provenance.example;

import com.telefonica.cose.provenance.CBOREnclosingMethods;
import com.telefonica.cose.provenance.CBORSignature;
import com.telefonica.cose.provenance.YANGMetadata;
import com.telefonica.cose.provenance.YANGModuleProcessor;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;

import java.io.File;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultiSigningCBORObjectTest {

    private static final String KID_A = "ec2.key";
    private static final String KID_B = "ec3.key";
    private static final String KID_C = "ec4.key";

    public static void main(String[] args) throws Exception {

        CBORSignature signer = new CBORSignature();
        CBOREnclosingMethods encloser = new CBOREnclosingMethods();

        File yangModule = new File("./ietf-yp-provenance@2025-05-09.yang");

        // =========================================================
        // 1. Extraer nombre real del campo de firma desde YANG
        // =========================================================
        YANGMetadata metadata = YANGModuleProcessor.extractSignatureMetadata(yangModule);
        String moduleName = YANGModuleProcessor.extractModuleName(yangModule);
        String signatureField = moduleName + ":" + metadata.getLeafName();

        System.out.println("Campo de firma CBOR según YANG: " + signatureField);

        // =========================================================
        // 2. Crear payload CBOR
        // =========================================================
        CBORObject cbor = buildSampleCBOR();

        System.out.println("\n=== CBOR ORIGINAL ===");
        System.out.println(cbor);

        // =========================================================
        // 3. ROUND 1 — Firma principal
        // =========================================================
        byte[] sig1 = signer.signingCBOR(cbor, KID_A);

        CBORObject doc1 = encloser.enclosingMethodYANGCBOR(cbor, sig1, yangModule);

        System.out.println("\n=== ROUND 1 - DOCUMENTO CBOR FIRMADO ===");
        System.out.println(doc1);
        System.out.println("Firma Base64: " + Base64.getEncoder().encodeToString(sig1));

        printEmbeddedSignatureInfo(doc1, signatureField, "Round 1");

        // =========================================================
        // 4. ROUND 2 — Countersign B
        // =========================================================
        byte[] embeddedSig1 = extractEmbeddedSignature(doc1, signatureField);
        CBORObject cleanDoc1 = removeEmbeddedSignature(doc1, signatureField);

        byte[] sig2 = signer.addCounterSign(cleanDoc1, embeddedSig1, KID_B);

        CBORObject doc2 = upsertEmbeddedSignature(doc1, sig2, signatureField);

        System.out.println("\n=== ROUND 2 - DOCUMENTO CBOR + COUNTERSIGN B ===");
        System.out.println(doc2);
        System.out.println("Firma Base64: " + Base64.getEncoder().encodeToString(sig2));

        printEmbeddedSignatureInfo(doc2, signatureField, "Round 2");

        // =========================================================
        // 5. ROUND 3 — Countersign C
        // =========================================================
        byte[] embeddedSig2 = extractEmbeddedSignature(doc2, signatureField);
        CBORObject cleanDoc2 = removeEmbeddedSignature(doc2, signatureField);

        byte[] sig3 = signer.addCounterSign(cleanDoc2, embeddedSig2, KID_C);

        CBORObject doc3 = upsertEmbeddedSignature(doc2, sig3, signatureField);

        System.out.println("\n=== ROUND 3 - DOCUMENTO CBOR + COUNTERSIGN C ===");
        System.out.println(doc3);
        System.out.println("Firma Base64: " + Base64.getEncoder().encodeToString(sig3));

        printEmbeddedSignatureInfo(doc3, signatureField, "Round 3");

        // =========================================================
        // 6. RESULTADO FINAL
        // =========================================================
        System.out.println("\n=== DOCUMENTO CBOR FINAL ===");
        System.out.println(doc3);

        System.out.println("\n=== FIRMA FINAL BASE64 ===");
        System.out.println(Base64.getEncoder().encodeToString(sig3));

        System.out.println("\n=== ESTRUCTURA COSE FINAL ===");
        printCoseStructure(sig3);

        // =========================================================
        // 7. Guardar documento CBOR final a fichero (.cbor)
        // =========================================================
        byte[] finalDocBytes = doc3.EncodeToBytes();

        Files.write(Paths.get("provenance_output_multisign.cbor"), finalDocBytes);

        System.out.println("\nDocumento CBOR final guardado en provenance_output_multisign.cbor");


    }

    /**
     * Construye un ejemplo de CBORObject YANG-like.
     */
    private static CBORObject buildSampleCBOR() {
        CBORObject root = CBORObject.NewMap();

        CBORObject iface = CBORObject.NewMap();
        iface.Add("name", "GigabitEthernet1");
        iface.Add("type", "ianaift:ethernetCsmacd");
        iface.Add("admin-status", "up");
        iface.Add("oper-status", "up");
        iface.Add("last-change", "2024-02-03T11:22:41.081+00:00");
        iface.Add("if-index", 1);
        iface.Add("phys-address", "0c:00:00:37:d6:00");
        iface.Add("speed", 1000000000);

        CBORObject ifaceArray = CBORObject.NewArray();
        ifaceArray.Add(iface);

        CBORObject interfaces = CBORObject.NewMap();
        interfaces.Add("interface", ifaceArray);

        root.Add("ietf-interfaces:interfaces", interfaces);

        return root;
    }

    /**
     * Extrae la firma embebida desde el documento CBOR.
     */
    private static byte[] extractEmbeddedSignature(CBORObject rootNode, String signatureField) {
        CBORObject sigKey = CBORObject.FromObject(signatureField);

        if (rootNode.getType() != CBORType.Map) {
            throw new IllegalArgumentException("Root CBOR must be a map");
        }

        for (CBORObject key : rootNode.getKeys()) {
            CBORObject value = rootNode.get(key);

            if (value.getType() == CBORType.Map && value.ContainsKey(sigKey)) {
                return value.get(sigKey).GetByteString();
            }
        }

        throw new IllegalArgumentException("No embedded signature found: " + signatureField);
    }

    /**
     * Devuelve una copia del documento CBOR sin la firma embebida.
     */
    private static CBORObject removeEmbeddedSignature(CBORObject rootNode, String signatureField) {
        CBORObject copy = CBORObject.DecodeFromBytes(rootNode.EncodeToBytes());
        CBORObject sigKey = CBORObject.FromObject(signatureField);

        if (copy.getType() != CBORType.Map) {
            throw new IllegalArgumentException("Root CBOR must be a map");
        }

        for (CBORObject key : copy.getKeys()) {
            CBORObject value = copy.get(key);

            if (value.getType() == CBORType.Map && value.ContainsKey(sigKey)) {
                value.Remove(sigKey);
                return copy;
            }
        }

        throw new IllegalArgumentException("No embedded signature found to remove: " + signatureField);
    }

    /**
     * Inserta o sustituye la firma embebida en el mismo sitio.
     */
    private static CBORObject upsertEmbeddedSignature(CBORObject rootNode, byte[] signature, String signatureField) {
        CBORObject copy = CBORObject.DecodeFromBytes(rootNode.EncodeToBytes());
        CBORObject sigKey = CBORObject.FromObject(signatureField);

        if (copy.getType() != CBORType.Map) {
            throw new IllegalArgumentException("Root CBOR must be a map");
        }

        for (CBORObject key : copy.getKeys()) {
            CBORObject value = copy.get(key);

            if (value.getType() == CBORType.Map) {
                if (value.ContainsKey(sigKey)) {
                    value.Remove(sigKey);
                }
                value.Add(sigKey, CBORObject.FromObject(signature));
                return copy;
            }
        }

        throw new IllegalArgumentException("No inner map found to upsert signature");
    }

    /**
     * Debug rápido del campo embebido.
     */
    private static void printEmbeddedSignatureInfo(CBORObject doc, String signatureField, String label) {
        byte[] embedded = extractEmbeddedSignature(doc, signatureField);

        System.out.println("\n--- " + label + " / Firma embebida ---");
        System.out.println("Campo     : " + signatureField);
        System.out.println("Tamaño    : " + embedded.length + " bytes");
        System.out.println("Base64    : " + Base64.getEncoder().encodeToString(embedded));
    }

    /**
     * Imprime la estructura COSE por dentro (útil para depuración).
     */
    private static void printCoseStructure(byte[] signatureBytes) {
        CBORObject cose = CBORObject.DecodeFromBytes(signatureBytes);

        System.out.println("COSE raw diagnostic:");
        System.out.println(cose);

        // Si viene taggeado, obtener el array interno
        CBORObject coseArray = cose;
        while (coseArray.isTagged()) {
            coseArray = coseArray.UntagOne();
        }

        if (coseArray.getType() != CBORType.Array || coseArray.size() != 4) {
            System.out.println("No parece un COSE_Sign1 estándar");
            return;
        }

        // Protected header
        CBORObject protectedBytes = coseArray.get(0);
        CBORObject protectedDecoded = CBORObject.DecodeFromBytes(protectedBytes.GetByteString());

        System.out.println("\nProtected header:");
        System.out.println(protectedDecoded);

        // Unprotected header
        CBORObject unprotected = coseArray.get(1);
        System.out.println("\nUnprotected header:");
        System.out.println(unprotected);

        // Payload
        System.out.println("\nPayload field:");
        System.out.println(coseArray.get(2)); // normalmente null porque es detached

        // Main signature
        System.out.println("\nMain signature:");
        System.out.println(coseArray.get(3));

        // Countersigns si existen
        CBORObject csKey = CBORObject.FromObject(7); // CounterSignature
        if (unprotected.ContainsKey(csKey)) {
            CBORObject csArray = unprotected.get(csKey);

            System.out.println("\nCountersignatures:");
            for (int i = 0; i < csArray.size(); i++) {
                CBORObject cs = csArray.get(i);

                System.out.println("\n--- CounterSign[" + i + "] raw ---");
                System.out.println(cs);

                CBORObject csProtBytes = cs.get(0);
                CBORObject csProt = CBORObject.DecodeFromBytes(csProtBytes.GetByteString());

                System.out.println("Protected header:");
                System.out.println(csProt);

                System.out.println("Signature:");
                System.out.println(cs.get(2));
            }
        } else {
            System.out.println("\nNo countersignatures found");
        }
    }
}