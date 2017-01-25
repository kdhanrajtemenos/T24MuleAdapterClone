package com.temenos.adapter.mule.T24outbound.rmi;

public class T24RequestProcessingException extends Exception {

	private static final long serialVersionUID = 5861759524064387675L;

	public T24RequestProcessingException() {
    }

    public T24RequestProcessingException(String message) {
        super(message);
    }

    public T24RequestProcessingException(Throwable throwable) {
        super(throwable);
    }

    public T24RequestProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
