package com.telefonica.cose.provenance;

import com.fasterxml.jackson.databind.JsonNode;
import org.jdom2.Document;

public interface EnclosingMethodInterface {

	/**
	 * interface method
	 */
	JsonNode enclosingMethod(JsonNode YANGprovenance, String signature);

/*	*//**
	 * interface method
	 */
	JsonNode enclosingMethod2(JsonNode YANGprovenance, String signature);

	/**
	 * interface method
	 *//*
	Document enclosingMethod3(Document YANGprovenance, String signature);

	*//**
	 * interface method
	 *//*
	Document enclosingMethod4(Document YANGprovenance, String signature);*/
}
