package com.telefonica.cose.provenance.exception;

/**
 * Exception handler
 * 
 * @author S. Garcia
 */

@SuppressWarnings("serial")
public class COSESignatureException extends Exception {
	/**
	 * Exception Method
	 * 
	 * @param string exception string
	 */
	public COSESignatureException(String string) {
        super(string);
    }
	/**
	 * Exception Method
	 * 
	 * @param message message exception
	 * @param ex exception
	 */
    public COSESignatureException(String message, Exception ex) {
        super(message, ex);
    }

}
