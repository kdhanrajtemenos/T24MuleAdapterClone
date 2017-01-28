package com.temenos.adapter.mule.T24outbound.rmi;

/**
 * Wrapper for response data from the outbound operations
 */
public class ResponseRecord {
    private String response;
    private String returnCode;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnCode() {
        return returnCode;
    }
}
