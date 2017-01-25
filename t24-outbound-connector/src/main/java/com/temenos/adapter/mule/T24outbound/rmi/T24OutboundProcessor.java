package com.temenos.adapter.mule.T24outbound.rmi;

import com.temenos.adapter.mule.T24outbound.rmi.T24RequestSpec;

//import com.temenos.adapter.common.runtime.T24RuntimeException;

//import com.temenos.adapter.oracle.outbound.request.exception.T24RequestProcessingException;

/**
 * Interface for classes that will be responsible for performing the outbound requests to T24
 */
public interface T24OutboundProcessor {

    /**
     * Performs the actual request to T24
     * @param request incoming request data
     * @param t24RequestSpec outbound operations properties container
     * @return wrapped response data
     * @throws T24RequestProcessingException
     */
    public ResponseRecord processRequest(String request, T24RequestSpec t24RequestSpec) throws RuntimeException;

}
