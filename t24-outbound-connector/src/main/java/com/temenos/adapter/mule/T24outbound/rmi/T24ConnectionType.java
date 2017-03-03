package com.temenos.adapter.mule.T24outbound.rmi;

public enum T24ConnectionType {
    TAFC("Agent"), TAFJ("Webservice");
    private String label;
    
    private T24ConnectionType(String _label) {
        label = _label;
    }
    
    public String getValue() {
        return label;
    }
    
    public static T24ConnectionType getDefault() {
        return TAFC;
    }
    
    public static T24ConnectionType resolve(String _label) {
        for (T24ConnectionType type : values()) {
        	if (type.label.equalsIgnoreCase(_label) || type.toString().equalsIgnoreCase(_label)) {
                return type;
            }
        }
        return getDefault();
    }
          
    public static T24ConnectionType fromString(String s){
        s = s.trim().toUpperCase().replaceAll("\\s+", "_");
        for(T24ConnectionType type : values()) {
            if (type.name().equalsIgnoreCase(s)) return type;
        }
        return null;
    }
}
