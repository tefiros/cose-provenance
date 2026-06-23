package com.telefonica.cose.provenance.example;

import com.telefonica.cose.provenance.CBORVerification;
import com.upokecenter.cbor.CBORObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultiVerifierCBORTest {

    public static void main(String[] args) throws Exception {

        // =========================================================
        // 1. Cargar documento CBOR ya firmado desde fichero
        // =========================================================
        byte[] fileBytes = Files.readAllBytes(Paths.get("provenance_output_multisign.cbor"));
        CBORObject signedDoc = CBORObject.DecodeFromBytes(fileBytes);

        System.out.println("=== DOCUMENTO CBOR CARGADO ===");
        System.out.println(signedDoc);

        // =========================================================
        // 2. Instanciar verifier
        // =========================================================
        CBORVerification verifier = new CBORVerification();
        File yangModule = new File("./ietf-yp-provenance@2025-05-09.yang");

        // =========================================================
        // 3. Verificar firma principal
        // =========================================================
        System.out.println("\n=== VERIFICANDO SOLO FIRMA PRINCIPAL ===");
        boolean validMain = verifier.verifyYANG(
                CBORObject.DecodeFromBytes(fileBytes),   // copia limpia
                yangModule
        );

        if (validMain) {
            System.out.println("Firma principal CBOR VALIDA");
        } else {
            System.out.println("Firma principal CBOR INVALIDA");
        }

        // =========================================================
        // 4. Verificar firma principal + countersigns
        // =========================================================
        System.out.println("\n=== VERIFICANDO FIRMA PRINCIPAL + COUNTERSIGNS ===");
        boolean validAll = verifier.verifyYANGWithCountersigns(
                CBORObject.DecodeFromBytes(fileBytes),   // otra copia limpia
                yangModule
        );

        if (validAll) {
            System.out.println("TODO VALIDO (firma principal + countersigns)");
        } else {
            System.out.println(" Alguna firma CBOR es INVALIDA");
        }
    }
}
