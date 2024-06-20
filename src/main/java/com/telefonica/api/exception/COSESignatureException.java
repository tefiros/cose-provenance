package com.telefonica.api.exception;

/**
 * 
 */

@SuppressWarnings("serial")
public class COSESignatureException extends Exception {
	public COSESignatureException(String string) {
        super(string);
    }
    public COSESignatureException(String message, Exception ex) {
        super(message, ex);
    }

}
