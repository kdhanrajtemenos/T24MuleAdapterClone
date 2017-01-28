package com.temenos.adapter.mule.T24outbound.rmi;

/**
 * This is an enumeration of the T24 request types supported by the 
 * T24 Outbound Adapter.
 */
public enum T24RequestType {
    OFS,
    OFSML,
    BATCH_OFS,
    BATCH_OFSML,
    SERVICE_XML;
    
    public static T24RequestType fromString(String s){
        s = s.trim().toUpperCase().replaceAll("\\s+", "_");
        for(T24RequestType type : values()) {
            if (type.name().equalsIgnoreCase(s)) return type;
        }
        return null;
    }
}
