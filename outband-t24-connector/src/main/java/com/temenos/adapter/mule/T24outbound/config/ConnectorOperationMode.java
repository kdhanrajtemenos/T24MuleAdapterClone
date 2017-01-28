package com.temenos.adapter.mule.T24outbound.config;

/**
 * Enumerator to mark connector mode
 */
public enum ConnectorOperationMode {
	
	
	DESIGN_TIME("DESIGN_TIME"), RUN_TIME("RUN_TIME");
	
	private String mode;
	
	ConnectorOperationMode(String mode){
		this.mode =mode;
	}
	
	public String asString() {
	    return this.mode;
	}
	
	public static ConnectorOperationMode fromString(String mode){
		ConnectorOperationMode[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; i++) {
        	ConnectorOperationMode type = arr[i];
            if(type.mode.equals(mode)) {
                return type;
            }
        }

        throw new RuntimeException("Invalid connector operation mode encountered [" + mode + "]");
	}
	
	public static boolean isValidOperationModeString(String mode) {
		ConnectorOperationMode[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; i++) {
        	ConnectorOperationMode type = arr[i];
            if(type.mode.equals(mode)) {
                return true;
            }
        }

        return false;
    }

}
