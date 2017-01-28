package com.temenos.adapter.mule.T24outbound.rmi;

import com.temenos.adapter.common.metadata.T24ServiceXmlMetadata;
import com.temenos.adapter.common.runtime.outbound.RequestType;

/**
 * Outbound operations properties container
 */
public class T24RequestSpec {

    /**
     * Type of outbound request
     */
    private RequestType t24RequestType;

    /**
     * ServiceXML specific request/response configuration data
     */
    private T24ServiceXmlMetadata t24ServiceMetadata;

    /**
     * Set outbound request type
     * @param t24RequestType
     */
    public void setT24RequestType(RequestType t24RequestType) {
        this.t24RequestType = t24RequestType;
    }

    /**
     * @return outbound request type
     */
    public RequestType getT24RequestType() {
        return t24RequestType;
    }

    /**
     * @return ServiceXML request/response configuration data
     */
    public T24ServiceXmlMetadata getT24ServiceMetadata() {
        return t24ServiceMetadata;
    }

    /**
     * Sets ServiceXML specific request/response configuration data
     * @param t24ServiceMetadata ServiceXML request/response configuration data
     */
    public void setT24ServiceMetadata(T24ServiceXmlMetadata t24ServiceMetadata) {
        this.t24ServiceMetadata = t24ServiceMetadata;
    }
}
