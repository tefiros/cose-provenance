package com.telefonica.cose.provenance.example;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32;

public class YangFingerprint {

    // =======================
    // Node estructural
    // =======================
    static class Node {
        String tag;
        String value;
        List<Node> children;

        Node(String tag, String value, List<Node> children) {
            this.tag = tag;
            this.value = value != null ? value : "";
            this.children = children != null ? children : new ArrayList<>();
        }
    }

    // =======================
    // CRC32 (igual que Python)
    // =======================
    static String crc32(String s) {
        CRC32 crc = new CRC32();
        crc.update(s.getBytes(StandardCharsets.UTF_8));
        return Long.toString(crc.getValue() & 0xFFFFFFFFL);
    }

    static String fingerprintValue(Object value) {
        return crc32(String.valueOf(value));
    }

    // =======================
    // FINGERPRINT UNORDERED
    // =======================
    static String fingerprint(Object... args) {

        if (args.length == 1) {
            Object v = args[0];

            if (v instanceof Node) {
                Node n = (Node) v;
                return fingerprint(n.tag, n.value, n.children);
            }
            return fingerprintValue(v);
        }

        if (args.length == 2) {
            String tag = String.valueOf(args[0]);
            Object value = args[1];

            return fingerprint("!TAG" + tag + ":" + fingerprint(value));
        }

        if (args.length == 3) {
            String tag = String.valueOf(args[0]);
            Object value = args[1];
            @SuppressWarnings("unchecked")
            List<Node> children = (List<Node>) args[2];

            List<String> childFPs = new ArrayList<>();
            for (Node child : children) {
                childFPs.add(fingerprint(child));
            }

            Collections.sort(childFPs); // 🔥 aquí ignora orden

            StringBuilder payload = new StringBuilder();
            payload.append("!TAG").append(tag)
                    .append(":").append(fingerprint(value));

            for (String fp : childFPs) {
                payload.append(":").append(fp);
            }

            return fingerprint(payload.toString());
        }

        throw new IllegalArgumentException("Invalid args");
    }

    // =======================
    // FINGERPRINT ORDERED
    // =======================
    static String fingerprintOrdered(Object... args) {

        if (args.length == 1) {
            Object v = args[0];

            if (v instanceof Node) {
                Node n = (Node) v;
                return fingerprintOrdered(n.tag, n.value, n.children);
            }
            return fingerprintValue(v);
        }

        if (args.length == 2) {
            String tag = String.valueOf(args[0]);
            Object value = args[1];

            return fingerprintOrdered("!TAG" + tag + ":" + fingerprintOrdered(value));
        }

        if (args.length == 3) {
            String tag = String.valueOf(args[0]);
            Object value = args[1];
            @SuppressWarnings("unchecked")
            List<Node> children = (List<Node>) args[2];

            List<String> childFPs = new ArrayList<>();
            for (Node child : children) {
                childFPs.add(fingerprintOrdered(child));
            }

            StringBuilder payload = new StringBuilder();
            payload.append("!TAG").append(tag)
                    .append(":").append(fingerprintOrdered(value));

            for (String fp : childFPs) {
                payload.append(":").append(fp);
            }

            return fingerprintOrdered(payload.toString());
        }

        throw new IllegalArgumentException("Invalid args");
    }

    // =======================
    // XML → Node
    // =======================
    static Node xmlToNode(Element elem) {

        String value = "";
        NodeList nodeList = elem.getChildNodes();
        List<Node> children = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node domNode = nodeList.item(i);

            if (domNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                children.add(xmlToNode((Element) domNode));
            } else if (domNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                String text = domNode.getTextContent();
                if (text != null && !text.trim().isEmpty()) {
                    value += text.trim();
                }
            }
        }

        return new Node(elem.getTagName(), value, children);
    }

    static Node parseXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        return xmlToNode(doc.getDocumentElement());
    }

    // =======================
    // MAIN DE PRUEBA
    // =======================
    public static void main(String[] args) throws Exception {

        String xml1 =
                "<top>" +
                        "  <list>" +
                        "    <item>" +
                        "      <val>42.5</val>" +
                        "      <foo>true</foo>" +
                        "      <bar>Chocolate</bar>" +
                        "    </item>" +
                        "    <item>" +
                        "      <val>44.5</val>" +
                        "      <foo>true</foo>" +
                        "      <bar>Banana</bar>" +
                        "    </item>" +
                        "  </list>" +
                        "</top>";

        String xml2 =
                "<top>" +
                        "  <list>" +
                        "    <item>" +
                        "      <val>44.5</val>" +
                        "      <foo>true</foo>" +
                        "      <bar>Banana</bar>" +
                        "    </item>" +
                        "    <item>" +
                        "      <val>42.5</val>" +
                        "      <foo>true</foo>" +
                        "      <bar>Chocolate</bar>" +
                        "    </item>" +
                        "  </list>" +
                        "</top>";

        Node n1 = parseXML(xml1);
        Node n2 = parseXML(xml2);

        String fp1 = fingerprint(n1);
        String fp2 = fingerprint(n2);

        String fp1Ordered = fingerprintOrdered(n1);
        String fp2Ordered = fingerprintOrdered(n2);

        System.out.println("=== UNORDERED ===");
        System.out.println(fp1);
        System.out.println(fp2);
        System.out.println("Match: " + fp1.equals(fp2));

        System.out.println("\n=== ORDERED ===");
        System.out.println(fp1Ordered);
        System.out.println(fp2Ordered);
        System.out.println("Match: " + fp1Ordered.equals(fp2Ordered));
    }
}