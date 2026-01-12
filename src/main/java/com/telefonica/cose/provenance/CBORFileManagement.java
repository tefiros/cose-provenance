package com.telefonica.cose.provenance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.*;

import java.util.LinkedHashMap;
import java.util.Map;


public class CBORFileManagement {
//    public static void main(String[] args) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//
//        String jsonBig = "{\n" +
//                "    \"zeta\": 26,\n" +
//                "    \"alpha\": 1,\n" +
//                "    \"gamma\": {\"x\":10,\"y\":20},\n" +
//                "    \"beta\": [3,2,1],\n" +
//                "    \"delta\": {\"m\":100,\"n\":200}\n" +
//                "}";
//
//        String jsonBig2 = "{\n" +
//                "    \"gamma\": {\"y\":20,\"x\":10},\n" +
//                "    \"beta\": [3,2,1],\n" +
//                "    \"delta\": {\"n\":200,\"m\":100},\n" +
//                "    \"alpha\": 1,\n" +
//                "    \"zeta\": 26\n" +
//                "}";
//
//        // Parsear JSON a objetos Java
//        Object data1 = mapper.readValue(jsonBig, Object.class);
//        Object data2 = mapper.readValue(jsonBig2, Object.class);
//
//        // Convertir a CBORObjects
//        CBORObject cbor1 = CBORObject.FromObject(data1);
//        CBORObject cbor2 = CBORObject.FromObject(data2);
//
//        // Imprimir representación legible antes de serializar
//        System.out.println("CBOR1 (legible): " + cbor1.toString());
//        System.out.println("CBOR2 (legible): " + cbor2.toString());
//
//        // Serializar en modo canonicalizado
//        byte[] canonical1 = cbor1.EncodeToBytes(CBOREncodeOptions.DefaultCtap2Canonical);
//        byte[] canonical2 = cbor2.EncodeToBytes(CBOREncodeOptions.DefaultCtap2Canonical);
//
//        System.out.println("\nCBOR1 bytes: " + canonical1.length);
//        System.out.println("CBOR2 bytes: " + canonical2.length);
//
//        // Verificar que los bytes son idénticos (canonicalización funciona)
//        System.out.println("¿Bytes iguales? " + java.util.Arrays.equals(canonical1, canonical2));
//    }

    public byte[] canonicalizeCbor(Object input) {
        CBORObject cbor = CBORObject.FromObject(input);
        return cbor.EncodeToBytes(CBOREncodeOptions.DefaultCtap2Canonical);
    }

    public static void main(String[] args) {
        // Creamos un mapa con claves de distintas longitudes y tipos
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("aa", 2);
        map.put("b", 3);
        map.put(10, Float.NaN);
        map.put(100, 5);

        // Convertir a CBORObject
        CBORObject cbor = CBORObject.FromObject(map);

        // Serializar con modo "Default" (no necesariamente determinístico)
        byte[] normal = cbor.EncodeToBytes(CBOREncodeOptions.Default);

        // Serializar con modo "DefaultCtap2Canonical"
        byte[] canonical = cbor.EncodeToBytes(CBOREncodeOptions.DefaultCtap2Canonical);

        System.out.println("Mapa legible (CBOR toString):");
        System.out.println(cbor.toString());

        System.out.println("\nBytes (Default):");
        printHex(normal);

        System.out.println("\nBytes (DefaultCtap2Canonical):");
        printHex(canonical);

        System.out.println("\n¿Son iguales los bytes? " + java.util.Arrays.equals(normal, canonical));
    }

    private static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }
}
