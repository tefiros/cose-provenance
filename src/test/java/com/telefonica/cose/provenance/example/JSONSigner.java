package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.JSONEnclosingMethods;
import com.telefonica.cose.provenance.JSONEnclMethodInterface;
import com.telefonica.cose.provenance.Parameters;
import com.telefonica.cose.provenance.JSONSignatureInterface;
import com.telefonica.cose.provenance.JSONSignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class JSONSigner {

    public static void main(String[] args) throws Exception {

        // Instanciamos los manejadores de firma y de enclavamiento
        JSONSignatureInterface sign = new JSONSignature();
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

        // Parseamos el JSON original
        ObjectMapper mapper = new ObjectMapper();
        JsonNode file = mapper.readTree(jsonString);

        // Definimos el módulo y el leaf según el YANG
//        String moduleName = "interfaces-provenance-augmented";
//        String leafName = "interfaces-provenance";
        File yangModule = new File("./ietf-yp-provenance@2025-05-09.yang");
        // Insertamos la firma usando el metodo paramétrico
//        JsonNode provenanceJSON = enclose.enclosingMethodParam(file, signature, moduleName, leafName);
        JsonNode provenanceJSON = enclose.enclosingMethodYANG(file,signature,yangModule);

        // Mostramos el resultado y guardamos
        String output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(provenanceJSON);
        System.out.println("Documento firmado con provenance:");
        System.out.println(output);

        try (FileOutputStream fos = new FileOutputStream("provenance_output.json")) {
            fos.write(output.getBytes());
            System.out.println("Documento guardado en provenance_output.json");
        } catch (IOException e) {
            System.err.println("Error al guardar el JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

