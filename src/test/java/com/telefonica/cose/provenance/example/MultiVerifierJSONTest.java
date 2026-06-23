package com.telefonica.cose.provenance.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.cose.provenance.JSONVerification;

public class MultiVerifierJSONTest {

    public static void main(String[] args) throws Exception {

        String jsonWithSignatures =
                "{\"ietf-interfaces:interfaces\":{\"interface\":[{\"name\":\"GigabitEthernet1\",\"type\":\"iana-if-type:ethernetCsmacd\",\"admin-status\":\"up\",\"oper-status\":\"up\",\"last-change\":\"2024-02-03T11:22:41.081+00:00\",\"if-index\":1,\"phys-address\":\"0c:00:00:37:d6:00\",\"speed\":1000000000}]},\"provenance-string\":\"0oRRowEmA2N4bWwEZ2VjMi5rZXmhB4KDTKIBJgRnZWMzLmtleaBYQCAipHLTBL/FfWzNEcjp0jvE/cqkJxWvwvw8oZQERlPJedLmdIHiMj5iSOVuULpNRu1AFZW5osVDwqaxvk9vV1aDTKIBJgRnZWM0LmtleaBYQLkItWjI9EXcvnBaWBKt8QebeXg4Wn2RmbimpmLww3h5+na80vvSpaTGdAlHuSninjC61tu7xwDwTcSaT0vyL1D2WECsA0kIO+EolMcghKt/vA+lsiUBSzz6H37iCMAoadmE+404aanap+r3aq/vZmGzJrR4QXoQ5U/aY3o8Mttc0OZX\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode doc = mapper.readTree(jsonWithSignatures);

        JSONVerification verifier = new JSONVerification();

        System.out.println("=== Verificando JSON Sign1 + countersigns ===");

        boolean valid = verifier.verifyJSONWithCountersigns(doc.deepCopy());

        if (valid) {
            System.out.println("✓ JSON TODO VÁLIDO");
        } else {
            System.out.println("✗ Alguna firma JSON INVÁLIDA");
        }
    }
}
