package com.telefonica.cose.provenance;


import com.upokecenter.cbor.CBORObject;

import java.io.File;
import java.io.IOException;


public interface CBOREnclosingMethodsInterface {

    CBORObject enclosingMethodCBOR(CBORObject rootNode, byte[] signature);

    CBORObject enclosingMethodParamCBOR(
            CBORObject yangProvenance,
            byte[] signature,
            String moduleName,
            String leafName
    );

    CBORObject enclosingMethodYANGCBOR(
            CBORObject yangProvenance,
            byte[] signature,
            File yangModule) throws IOException;

}
