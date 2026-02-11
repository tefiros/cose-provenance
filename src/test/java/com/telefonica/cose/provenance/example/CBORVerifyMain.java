package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.telefonica.cose.provenance.*;

public class CBORVerifyMain {

    public static void main(String[] args) throws Exception {


        CBORSignatureInterface signer = new CBORSignature();
        CBORVerificationInterface verifier = new CBORVerification();
        JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
        Parameters param = new Parameters();

        ObjectMapper mapper = new ObjectMapper();


        String jsonString = "{\n" +
                "  \"ietf-interfaces:interfaces\": {\n" +
                "    \"interface\": [\n" +
                "      {\n" +
                "        \"name\": \"GigabitEthernet1\",\n" +
                "        \"type\": \"ianaift:ethernetCsmacd\",\n" +
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

        System.out.println("JSON original:");
        System.out.println(jsonString);


        String signature = signer.signing(jsonString, param.getProperty("kid"));

        System.out.println("\nFirma COSE (Base64):");
        System.out.println(signature);


        JsonNode originalJson = mapper.readTree(jsonString);
        JsonNode signedJson = enclose.enclosingMethodJSON(originalJson, signature);

        // Verificación original
        JsonNode signedCopy1 = signedJson.deepCopy();
        boolean valid = verifier.verify(signedCopy1);
        System.out.println("Verification result: " + (valid ? "Valid signature" : "Invalid"));


        JsonNode signedCopy2 = signedJson.deepCopy();
        ObjectNode root = (ObjectNode) signedCopy2.get("ietf-interfaces:interfaces");
        ObjectNode iface = (ObjectNode) root.get("interface").get(0);


        iface.put("speed", iface.get("speed").asInt() + 1);

        System.out.println("JSON modificado:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(signedCopy2));


        boolean validAfterChange = verifier.verify(signedCopy2);
        System.out.println("Verification after modification: " + (validAfterChange ? "Valid" : "Invalid"));


    }
}
