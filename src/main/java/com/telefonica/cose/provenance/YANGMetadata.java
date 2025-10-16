package com.telefonica.cose.provenance;

public class YANGMetadata {
    private final String namespace;
    private final String leafName;

    public YANGMetadata(String namespace, String leafName) {
        this.namespace = namespace;
        this.leafName = leafName;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getLeafName() {
        return leafName;
    }


}
