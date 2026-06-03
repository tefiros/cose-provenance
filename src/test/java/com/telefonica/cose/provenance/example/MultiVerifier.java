package com.telefonica.cose.provenance.example;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.StringReader;
import com.telefonica.cose.provenance.*;
import COSE.CoseException;

public class MultiVerifier {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    static {
        org.apache.xml.security.Init.init();
    }

    private static final String SIG_NS = "urn:ietf:params:xml:ns:yang:ietf-yp-provenance";
    private static final String SIG_EL = "provenance-string";

    public static void main(String[] args) throws Exception {

        // Simular el XML final con countersignatures que viene del test
        // En producción esto vendría de un fichero o red
        String xmlWithSignatures =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<envelope xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-notification\">" +
                        "<provenance-string xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-provenance\">" +
                        "0oRRowEmA2N4bWwEZ2VjMi5rZXmhB4KDTKIBJgRnZWMzLmtleaBYQAvRk7UszCxQaqhYSoA5mxKSipPXI2jjSieJ9QoAYU4rYOpOPAOiRd/sS002+HEkApJjcPzzVTsrrQrBVEBM0ZCDTKIBJgRnZWM0LmtleaBYQFrVrbqGLKaDDtFZ6/5IENf/In75POl0ZahlVQKZgBCmOS0y5duWDEwXa25M6vo0aG9f0qrDU7/snJ87yY3EzKT2WEBdBSN9C839ofGzVD78UQdbQ5jO9b3tkhAsTSUot5RISItec7d4Kv1MEGNE0w++MTOsjFQEJT4C5Zr2zNWZ9sZq" +
                        "</provenance-string>\n" +
                        "    <event-time>2024-02-03T11:37:25.94Z</event-time>\n" +
                        "    <contents>\n" +
                        "        <push-update xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yang-push\">\n" +
                        "            <subscription-id>2147483648</subscription-id>\n" +
                        "            <datastore-contents>\n" +
                        "                <interfaces-state xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\">\n" +
                        "                    <interface>\n" +
                        "                        <name>GigabitEthernet1</name>\n" +
                        "                        <admin-status>up</admin-status>\n" +
                        "                        <oper-status>up</oper-status>\n" +
                        "                    </interface>\n" +
                        "                </interfaces-state>\n" +
                        "            </datastore-contents>\n" +
                        "        </push-update>\n" +
                        "    </contents>\n" +
                        "</envelope>";

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlWithSignatures));

        XMLVerification verifier = new XMLVerification();

        // ── Verificar Sign1 original ──────────────────────────────────────
        System.out.println("=== Verificando Sign1 original ===");
        try {
            // verifyYANG muta el documento (elimina el elemento firma)
            // así que trabajamos con una copia
            Document docCopy = builder.build(new StringReader(xmlWithSignatures));
            if (verifier.verifyYANG(docCopy, SIG_EL, SIG_NS)) {
                System.out.println("✓ Sign1 original VÁLIDO");
            } else {
                System.out.println("✗ Sign1 original INVÁLIDO");
            }
        } catch (CoseException e) {
            System.err.println("Error verificando Sign1: " + e.getMessage());
        }

        // ── Verificar Sign1 + todas las countersigns ──────────────────────
        System.out.println("=== Verificando Sign1 + countersigns ===");
        try {
            Document docCopy = builder.build(new StringReader(xmlWithSignatures));
            if (verifier.verifyYANGWithCountersigns(docCopy, SIG_EL, SIG_NS)) {
                System.out.println("Todo alido");
            } else {
                System.out.println("Alguna firma INVALIDA");
            }
        } catch (CoseException e) {
            System.err.println("Error verificando: " + e.getMessage());
            e.printStackTrace();
        }

        // ── Verificar countersign de ec3.key ─────────────────────────────
//        System.out.println("\n=== Verificando countersign ec3.key ===");
//        try {
//            Document docCopy = builder.build(new StringReader(xmlWithSignatures));
//            if (verifier.verifyCounterSign(docCopy, SIG_EL, SIG_NS, "ec3.key")) {
//                System.out.println("✓ CounterSign ec3.key VÁLIDO");
//            } else {
//                System.out.println("✗ CounterSign ec3.key INVÁLIDO");
//            }
//        } catch (CoseException e) {
//            System.err.println("Error verificando countersign: " + e.getMessage());
//        }
//
//        // ── Verificar countersign de ec4.key ─────────────────────────────
//        System.out.println("\n=== Verificando countersign ec4.key ===");
//        try {
//            Document docCopy = builder.build(new StringReader(xmlWithSignatures));
//            if (verifier.verifyCounterSign(docCopy, SIG_EL, SIG_NS, "ec4.key")) {
//                System.out.println("✓ CounterSign ec4.key VÁLIDO");
//            } else {
//                System.out.println("✗ CounterSign ec4.key INVÁLIDO");
//            }
//        } catch (CoseException e) {
//            System.err.println("Error verificando countersign: " + e.getMessage());
//        }
    }
}