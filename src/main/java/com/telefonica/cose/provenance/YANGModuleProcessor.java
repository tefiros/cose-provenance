package com.telefonica.cose.provenance;
import java.io.*;
import java.nio.file.*;
import java.util.regex.*;


public class YANGModuleProcessor {
    public static YANGMetadata extractSignatureMetadata(File yangFile) throws IOException {
        String content = Files.readString(yangFile.toPath());

        // Buscar el namespace del módulo
        Pattern nsPattern = Pattern.compile("namespace\\s+\"([^\"]+)\"\\s*;");
        Matcher nsMatcher = nsPattern.matcher(content);

        String namespace = null;
        if (nsMatcher.find()) {
            namespace = nsMatcher.group(1);
        }

        // Buscar el nombre del leaf que sea de tipo iyangprov:provenance-signature
        Pattern leafPattern = Pattern.compile(
                "leaf\\s+([a-zA-Z0-9\\-]+)\\s*\\{[^}]*type\\s+iyangprov:provenance-signature\\s*;",
                Pattern.DOTALL);
        Matcher leafMatcher = leafPattern.matcher(content);

        String leafName = null;
        if (leafMatcher.find()) {
            leafName = leafMatcher.group(1);
        }

        if (namespace == null || leafName == null) {
            throw new IllegalArgumentException("No se encontró namespace o leaf de tipo provenance-signature en el módulo YANG");
        }

        return new YANGMetadata(namespace, leafName);
    }
}
