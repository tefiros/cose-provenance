package com.telefonica.api;

import java.io.StringWriter;

import org.jdom2.Document;

public interface VerificationInterface {
	
	boolean verify(String message, byte[] signature) throws Exception;
	
	String readYANGFile(String signatureFile) throws Exception;
	
	byte[] readSignature(String signatureFile) throws Exception;
	
	void saveXMLDocument(Document document, StringWriter writer) throws Exception;

}
