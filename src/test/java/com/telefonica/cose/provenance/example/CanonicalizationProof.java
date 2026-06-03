package com.telefonica.cose.provenance.example;

import com.telefonica.cose.provenance.XMLFileManagement;

public class CanonicalizationProof {

    static {
        org.apache.xml.security.Init.init();
    }

    public static void main(String[] args) {

        XMLFileManagement xmlManager = new XMLFileManagement();

        // XML 1 (orden A)
        String xml1 =
                "<aaa>" +
                        "  <bbb>1</bbb>" +
                        "  <ccc>2</ccc>" +
                        "  <ddd>3</ddd>" +
                        "</aaa>";

        // XML 2 (orden diferente)
        String xml2 =
                "<aaa>" +
                        "  <ddd>3</ddd>" +
                        "  <ccc>2</ccc>" +
                        "  <bbb>1</bbb>" +
                        "</aaa>";

        // Canonicalización
        String canonical1 = xmlManager.canonicalizeXML(xml1);
        String canonical2 = xmlManager.canonicalizeXML(xml2);

        // Mostrar resultados
        System.out.println("===== XML 1 Canonical =====");
        System.out.println(canonical1);

        System.out.println("===== XML 2 Canonical =====");
        System.out.println(canonical2);

        // Comparación
        if (canonical1.equals(canonical2)) {
            System.out.println("Los XML canonicos son iguales");
        } else {
            System.out.println("Los XML canonicos son DIFERENTES");
        }
    }
}