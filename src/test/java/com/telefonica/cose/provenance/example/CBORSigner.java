package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.*;
import com.upokecenter.cbor.CBORObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CBORSigner {

    public static void main(String[] args) throws Exception {
//        // Instanciamos los manejadores de firma y de enclosing
//        CBORSignatureInterface sign = new CBORSignature();
//        JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
//        Parameters param = new Parameters();
//
//        // JSON YANG de ejemplo (simula un fragmento de ietf-interfaces)
//        String jsonString = "{\n" +
//                "  \"ietf-interfaces:interfaces\": {\n" +
//                "    \"interface\": [\n" +
//                "      {\n" +
//                "        \"name\": \"GigabitEthernet1\",\n" +
//                "        \"type\": \"ianaift:ethernetCsmacd\",\n" +
//                "        \"admin-status\": \"up\",\n" +
//                "        \"oper-status\": \"up\",\n" +
//                "        \"last-change\": \"2024-02-03T11:22:41.081+00:00\",\n" +
//                "        \"if-index\": 1,\n" +
//                "        \"phys-address\": \"0c:00:00:37:d6:00\",\n" +
//                "        \"speed\": 1000000000\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  }\n" +
//                "}";
//
//        // Generamos la firma base64 (simulada o real)
//        String signature = sign.signing(jsonString, param.getProperty("kid"));
//
//        System.out.println(signature);
//
//        // Parseamos el JSON original
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode file = mapper.readTree(jsonString);
//
//        JsonNode provenanceJson = enclose.enclosingMethodJSON(file, signature);
//
//        String output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(provenanceJson);
//        System.out.println("Documento firmado con provenance:");
//        System.out.println(output);

        // Probando archivos .cbor
//        // Ruta al archivo .cbor
//        String cborFilePath = "example.cbor";
//
//        // Leemos los bytes del archivo
//        byte[] fileBytes = Files.readAllBytes(Paths.get(cborFilePath));
//
//        // Convertimos a CBORObject
//        CBORObject cbor = CBORObject.DecodeFromBytes(fileBytes);
//
//        // Creamos instancia del signer
//        CBORSignatureInterface signer = new CBORSignature();
//        Parameters param = new Parameters();
//
//        // Firmamos el CBORObject
//        String kid = param.getProperty("kid"); // tu Key ID
//        String signature = ((CBORSignature) signer).signingCBOR(cbor, kid);
//
//        System.out.println("Firma COSE (Base64) del archivo CBOR:");
//        System.out.println(signature);
//
//        // Opcional: mostrar tamaño del CBOR y contenido (hex)
//        System.out.println("\nCBOR original tamaño (bytes): " + fileBytes.length);
//        System.out.println("CBOR en hex: " + bytesToHex(fileBytes));
//    }
//
//    //
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02X", b));
//        }
//        return sb.toString();

        // 1. Instanciamos el signer CBOR y el enclosing JSON
        CBORSignatureInterface sign = new CBORSignature();
        JSONEnclMethodInterface enclose = new JSONEnclosingMethods();
        Parameters param = new Parameters();

        // 2. JSON YANG de ejemplo (legible)
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

        // 3. Firmamos usando CBOR canonicalization (RFC 8949)
        String signature = sign.signing(jsonString, param.getProperty("kid"));

        System.out.println("Firma COSE (Base64):");
        System.out.println(signature);

        // 4. Parseamos el JSON original
        ObjectMapper mapper = new ObjectMapper();
        JsonNode file = mapper.readTree(jsonString);

        // 5. Enclosing usando YANG metadata
        File yangModule = new File("./ietf-yp-provenance@2025-05-09.yang");
        JsonNode provenanceJSON = enclose.enclosingMethodYANG(file, signature, yangModule);

        // 6. Resultado final (JSON legible + firma CBOR)
        String output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(provenanceJSON);
        System.out.println("\nDocumento firmado con CBOR provenance:");
        System.out.println(output);

        // 7. Guardamos a fichero
        try (FileOutputStream fos = new FileOutputStream("provenance_output_cbor.json")) {
            fos.write(output.getBytes());
            System.out.println("\nDocumento guardado en provenance_output_cbor.json");
        } catch (IOException e) {
            System.err.println("Error al guardar el JSON: " + e.getMessage());
        }


    }



}
