package com.telefonica.cose.provenance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.*;

import java.util.LinkedHashMap;
import java.util.Map;


public class CBORFileManagement {


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
