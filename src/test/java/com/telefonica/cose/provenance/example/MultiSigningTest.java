package com.telefonica.cose.provenance.example;

import com.telefonica.cose.provenance.*;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.StringReader;

/**
 * End-to-end runnable test for progressive COSE multi-signing.
 * Flow:
 *   Round 1 — signer A (ec2.key)
 *       sign(null, xmlString, KID_A)              → new COSE_Sign with 1 signature
 *       upsertSignature(doc, sig1, element, ns)   → inserts element (first time)
 *   Round 2 — signer B (ec3.key)
 *       sign(sig1, xmlString, KID_B)              → COSE_Sign with 2 signatures
 *       upsertSignature(doc, sig2, element, ns)   → replaces element (no duplicate)
 */
public class MultiSigningTest {

    static {
        org.apache.xml.security.Init.init();
    }

    private static final String YANG_MODULE_PATH = "./interfaces-provenance-augmented.yang";
    private static final String SIG_NS  = "urn:ietf:params:xml:ns:yang:ietf-yp-provenance";
    private static final String SIG_EL  = "provenance-string";

    private static final String KID_A = "ec2.key";
    private static final String KID_B = "ec3.key";
    private static final String KID_C = "ec4.key";

    public static void main(String[] args) throws Exception {

        String xmlString =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<envelope xmlns=\"urn:ietf:params:xml:ns:yang:ietf-yp-notification\">\n" +
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

        XMLSignature        signer   = new XMLSignature();
        XMLEnclosingMethods encloser = new XMLEnclosingMethods();
        File                yang     = new File(YANG_MODULE_PATH);
        SAXBuilder          builder  = new SAXBuilder();
        XMLOutputter        out      = new XMLOutputter(Format.getPrettyFormat());

        Document doc = builder.build(new StringReader(xmlString));
        // Al final del MultiSigningTest, en lugar de getPrettyFormat:
        XMLOutputter rawOut = new XMLOutputter(Format.getRawFormat());

        // Ronda 1 — firmante original
        String sig1 = signer.signing(xmlString, KID_A);
        doc = encloser.enclosingMethodParam(doc, sig1, SIG_EL, SIG_NS);
        System.out.println("[Firmado] Document:\n" + out.outputString(doc));
// Ronda 2 — componente B recibe el XML con sig1 y añade countersign
        String xmlWithSig1 = rawOut.outputString(doc);
        String sig2 = signer.addCounterSign(xmlWithSig1, KID_B, SIG_EL);
        System.out.println("sig2 length: " + sig2.length());
        System.out.println("Calling upsert with sig2...");
        doc = encloser.upsertSignature(doc, sig2, SIG_EL, SIG_NS);
        System.out.println("[Round 2] Document:\n" + out.outputString(doc));

// Ronda 3 — componente C añade otro countersign
        String xmlWithSig2 = rawOut.outputString(doc);
        String sig3 = signer.addCounterSign(xmlWithSig2, KID_C, SIG_EL);
        System.out.println("sig3 length: " + sig3.length());
        System.out.println("Calling upsert with sig3...");
        doc = encloser.upsertSignature(doc, sig3, SIG_EL, SIG_NS);
        System.out.println("[Round 3] Document:\n" + out.outputString(doc));
        System.out.println("=== XML PARA VERIFIER ===");
        System.out.println(rawOut.outputString(doc));
    }


}