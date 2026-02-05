package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.*;

public class CBORVerifier {

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

        System.out.println("\nJSON firmado:");
        System.out.println(
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(signedJson)
        );

        boolean valid = verifier.verify(signedJson);

        System.out.println("\nVerification result:");
        System.out.println(valid ? "Valid signature" : "Invalid signature");
    }
}
