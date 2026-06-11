package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.telefonica.cose.provenance.JSONSignature;

public class MultiSigningJSONTest {

    private static final String SIG_EL = "provenance-string";

    private static final String KID_A = "ec2.key";
    private static final String KID_B = "ec3.key";
    private static final String KID_C = "ec4.key";

    public static void main(String[] args) throws Exception {

        String jsonString =
                "{\n" +
                        "  \"ietf-interfaces:interfaces\": {\n" +
                        "    \"interface\": [\n" +
                        "      {\n" +
                        "        \"name\": \"GigabitEthernet1\",\n" +
                        "        \"type\": \"iana-if-type:ethernetCsmacd\",\n" +
                        "        \"admin-status\": \"up\",\n" +
                        "        \"oper-status\": \"up\",\n" +
                        "        \"last-change\": \"2024-02-03T11:22:41.081+00:00\",\n" +
                        "        \"if-index\": 1,\n" +
                        "        \"phys-address\": \"0c:00:00:37:d6:00\",\n" +
                        "        \"speed\": 1000000000\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}";

        JSONSignature signer = new JSONSignature();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode doc = mapper.readTree(jsonString);

        // Ronda 1 — firmante original
        String sig1 = signer.signing(jsonString, KID_A);
        doc = upsertSignature(doc, sig1, SIG_EL);
        System.out.println("[Round 1] Document firmado:\n" +
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));

        // Ronda 2 — countersign B
        String jsonWithSig1 = mapper.writeValueAsString(doc); // JSON compacto/estable
        String sig2 = signer.addCounterSign(jsonWithSig1, KID_B, SIG_EL);
        doc = upsertSignature(doc, sig2, SIG_EL);
        System.out.println("[Round 2] Document con countersign B:\n" +
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));

        // Ronda 3 — countersign C
        String jsonWithSig2 = mapper.writeValueAsString(doc); // JSON compacto/estable
        String sig3 = signer.addCounterSign(jsonWithSig2, KID_C, SIG_EL);
        doc = upsertSignature(doc, sig3, SIG_EL);
        System.out.println("[Round 3] Document con countersign C:\n" +
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));

        // JSON final para verifier
        String finalJson = mapper.writeValueAsString(doc);
        System.out.println("=== JSON PARA VERIFIER ===");
        System.out.println(finalJson);
    }

    private static JsonNode upsertSignature(JsonNode json, String signature, String signatureElement) {
        if (json == null || !json.isObject()) {
            throw new IllegalArgumentException("El JSON root debe ser un objeto");
        }

        ObjectNode root = ((ObjectNode) json).deepCopy();
        root.put(signatureElement, signature);
        return root;
    }
}