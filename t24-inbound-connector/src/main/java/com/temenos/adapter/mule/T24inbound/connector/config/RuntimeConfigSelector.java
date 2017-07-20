package com.temenos.adapter.mule.T24inbound.connector.config;

/**
 * Enumerator for the design-time runtime selector drop-down list 
 */
public enum RuntimeConfigSelector {
	TAFJ; // TAFJ, TAFC;
	
	public static String getRunTimeSelector(RuntimeConfigSelector type){

		if(type.equals(TAFJ)){
			return "TAFJ";
		}
		/*
		else if(type.equals(TAFC)){
			return "TAFC";
		}
		*/
		return "Undefined";
	}
}
