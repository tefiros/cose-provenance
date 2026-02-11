package com.telefonica.cose.provenance.example;

import COSE.CoseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.CBORVerification;
import com.telefonica.cose.provenance.CBORVerificationInterface;
import com.telefonica.cose.provenance.exception.COSESignatureException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.nio.file.Files;
import java.security.Security;

public class CBORVerifier {

    static {
        // Register BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {

        // 📂 Archivo CBOR firmado previamente
        String filePath = "./provenance_output_cbor.json";

        // Instanciar verificador CBOR
        CBORVerificationInterface verifier = new CBORVerification();

        // Leer archivo CBOR como bytes
        byte[] cborBytes = Files.readAllBytes(new File(filePath).toPath());

        // Convertir CBOR → JsonNode (para poder usar la misma lógica de extracción)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode doc = mapper.readTree(cborBytes);

        // 🔎 Parámetros YANG
        String moduleName = "ietf-yp-provenance";
        String leafName = "provenance";

        try {

            if (verifier.verifyYANG(doc, moduleName, leafName)) {
                System.out.println("\033[1mSignature verified successfully (CBOR + YANG)\033[0m");
            } else {
                System.err.println("\033[1mInvalid signature.\033[0m");
            }

        } catch (CoseException | COSESignatureException e) {
            System.err.println("Signature verification failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

