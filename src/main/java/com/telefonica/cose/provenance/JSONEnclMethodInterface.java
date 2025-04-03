package com.telefonica.cose.provenance;

import com.fasterxml.jackson.databind.JsonNode;

public interface JSONEnclMethodInterface {

    /**
     * interface method
     */
    JsonNode enclosingMethodJSON(JsonNode YANGprovenance, String signature);

    /*	*//**
     * interface method
     */
    JsonNode enclosingMethod2JSON(JsonNode YANGprovenance, String signature);

    /**
     * interface method
     */
    JsonNode enclosingMethod3JSON(JsonNode YANGprovenance, String signature);

    /**
     * interface method
     */
    JsonNode enclosingMethod4JSON(JsonNode YANGprovenance, String signature);
}