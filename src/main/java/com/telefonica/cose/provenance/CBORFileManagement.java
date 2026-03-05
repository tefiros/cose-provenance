package com.telefonica.cose.provenance;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


public class CBORFileManagement {


    public byte[] canonicalizeCbor(Object input) {
        CBORObject cbor = CBORObject.FromObject(input);
        return cbor.EncodeToBytes(new CBOREncodeOptions("deterministic=true"));
    }

//    public static void main(String[] args) {
//        // Creamos un mapa con claves de distintas longitudes y tipos
//        Map<Object, Object> map = new LinkedHashMap<>();
//        map.put("a", 1);
//        map.put("aa", 2);
//        map.put("b", 3);
//        map.put(10, Float.NaN);
//        map.put(100, 5);
//
//        // Convertir a CBORObject
//        CBORObject cbor = CBORObject.FromObject(map);
//
//        // Serializar con modo "Default" (no necesariamente determinístico)
//        byte[] normal = cbor.EncodeToBytes(CBOREncodeOptions.Default);
//
//        // Serializar con modo "DefaultCtap2Canonical"
//        byte[] canonical = cbor.EncodeToBytes(CBOREncodeOptions.DefaultCtap2Canonical);
//
//        System.out.println("Mapa legible (CBOR toString):");
//        System.out.println(cbor.toString());
//
//        System.out.println("\nBytes (Default):");
//        printHex(normal);
//
//        System.out.println("\nBytes (DefaultCtap2Canonical):");
//        printHex(canonical);
//
//        System.out.println("\n¿Son iguales los bytes? " + java.util.Arrays.equals(normal, canonical));
//    }

    private static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

//    public static void main(String[] args) throws Exception {
//        CBORObject cbor = CBORObject.NewMap()
//                .Add("name", "GigabitEthernet1")
//                .Add("speed", 1000000000)
//                .Add("status", "up");
//
//        byte[] bytes = cbor.EncodeToBytes();
//        Files.write(Paths.get("example.cbor"), bytes);
//        System.out.println("Archivo CBOR generado: example.cbor");
//    }

//    public static void main(String[] args) throws Exception {
//
//        String json1 =
//                "{\n" +
//                        "  \"insa-test:insa-container\": {\n" +
//                        "    \"computer\": \"a\",\n" +
//                        "    \"router\": 2,\n" +
//                        "    \"time\": \"12:04:34\"\n" +
//                        "  }\n" +
//                        "}";
//
//        String json2 =
//                "{\n" +
//                        "  \"insa-test:insa-container\": {\n" +
//                        "    \"time\": \"12:04:34\",\n" +
//                        "    \"router\": 2,\n" +
//                        "    \"computer\": \"a\"\n" +
//                        "  }\n" +
//                        "}";
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        JsonNode node1 = mapper.readTree(json1);
//        JsonNode node2 = mapper.readTree(json2);
//
//        byte[] bytes1 = canonicalizeCbor(node1);
//        byte[] bytes2 = canonicalizeCbor(node2);
//
//        System.out.println("Length 1: " + bytes1.length);
//        System.out.println("Length 2: " + bytes2.length);
//        System.out.println("Are equal? -> " + Arrays.equals(bytes1, bytes2));
//    }
}
