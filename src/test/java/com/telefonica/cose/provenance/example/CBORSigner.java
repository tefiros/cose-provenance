package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.*;

public class CBORSigner {

    public static void main(String[] args) throws Exception {
        // Instanciamos los manejadores de firma y de enclosing
        CBORSignatureInterface sign = new CBORSignature();
        JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
        Parameters param = new Parameters();

        // JSON YANG de ejemplo (simula un fragmento de ietf-interfaces)
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

        // Generamos la firma base64 (simulada o real)
        String signature = sign.signing(jsonString, param.getProperty("kid"));

        System.out.println(signature);

        // Parseamos el JSON original
        ObjectMapper mapper = new ObjectMapper();
        JsonNode file = mapper.readTree(jsonString);

        JsonNode provenanceJson = enclose.enclosingMethodJSON(file, signature);

        String output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(provenanceJson);
        System.out.println("Documento firmado con provenance:");
        System.out.println(output);

    }

}
