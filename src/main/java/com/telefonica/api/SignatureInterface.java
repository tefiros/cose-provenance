package com.telefonica.api;

import org.jdom2.Document;

public interface SignatureInterface {

	String signing(String document, String kid) throws Exception;

	Document enclosingMethod(String YANGdata, String signature) throws Exception;

	Document enclosingMethod2(String notificationYANG, String signature) throws Exception;
	
	void saveXMLDocument(Document document, String fileName) throws Exception;

}
