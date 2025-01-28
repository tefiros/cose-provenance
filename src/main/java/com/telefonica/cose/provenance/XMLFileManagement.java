package com.telefonica.cose.provenance;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.parser.XMLParserException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * XML files management
 * 
 * @author S. Garcia
 * 
 */

public class XMLFileManagement {

	/**
	 * This method canonicalized the xml
	 * 
	 * @param xmlYANG file path String of the xml to be canonicalized
	 * @return canonicalized xml File as a String
	 */
	public String canonicalizeXML(String xmlYANG) {

		byte[] content;
		String check_string; // preprocessing for several \r\n
		String canonicalizedFile = null;
		try {
			// check_string = xmlYANG.replaceAll(">\\s+<", ">\r\n<");
			content = xmlYANG.getBytes();
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS);
			canonicalizer.canonicalize(content, output, false);

			canonicalizedFile = new String(output.toByteArray(), "UTF-8");
		} catch (XMLParserException | IOException | CanonicalizationException | InvalidCanonicalizerException e) {
			e.getStackTrace();
		}

		return canonicalizedFile;
	}

	/**
	 * This method loads the xml as a JDOM
	 * 
	 * @param xmlFilePath file path String of the xml to be loaded
	 * @return JDOM for the xml file
	 * @throws JDOMException exception that occurs in the JDOM library, which is
	 *                       used for manipulating XML data in Java
	 * @throws IOException   exception that occurs during Input/Output (I/O)
	 *                       operations
	 */
	public Document loadXMLDocument(String xmlFilePath) throws JDOMException, IOException {

		// Use a SAXBuilder to charge the XML document
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(xmlFilePath);

	}

	/**
	 * This method saves the xml as a File
	 * 
	 * @param document JDOM to be stored as a StringWriter
	 * @param fileName String of the file path where the JDOM will be stored
	 */
	public void saveXMLDocument(Document document, String fileName) {

		try {
			// XMLOutputter is necessary to write xml format in files
			XMLOutputter xmlOutput = new XMLOutputter();
			// xmlOutput.setFormat(Format.getPrettyFormat());

			// Write the updated XML in a new file
			FileWriter writer = new FileWriter(fileName);
			xmlOutput.output(document, writer);
			writer.close();

		} catch (IOException e) {
			e.getStackTrace();
		}

	}

	/**
	 * This method saves the xml as a StringWriter
	 * 
	 * @param document JDOM to be stored as a StringWriter
	 * @param writer   StringWriter where the JDOM document will be stored
	 */
	public void saveXMLDocument(Document document, StringWriter writer) {
		try {
			// XMLOutputter is necessary to write xml format in files
			XMLOutputter xmlOutput = new XMLOutputter();
			// xmlOutput.setFormat(Format.getPrettyFormat());

			// Write the updated XML in a new String
			xmlOutput.output(document, writer);
			writer.close();

		} catch (IOException e) {
			e.getStackTrace();
		}
	}

}
