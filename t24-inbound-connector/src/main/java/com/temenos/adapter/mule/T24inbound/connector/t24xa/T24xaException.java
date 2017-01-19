package com.temenos.adapter.mule.T24inbound.connector.t24xa;

public class T24xaException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	public T24xaException() {
        super();
    }

    public T24xaException(String s) {
        super(s);
    }

    public T24xaException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public T24xaException(Throwable throwable) {
        super(throwable);
    }
}