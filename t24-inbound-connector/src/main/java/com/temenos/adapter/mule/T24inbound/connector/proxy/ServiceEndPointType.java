package com.temenos.adapter.mule.T24inbound.connector.proxy;



public enum ServiceEndPointType {
	HTTPSOAP11, HTTPSOAP12, HTTPENDPOINT;
	
	public static String getRunTimeSelector(ServiceEndPointType type){

		if(type.equals(HTTPSOAP11)){
			return "HTTPSOAP11";
		}else if(type.equals(HTTPSOAP12)){
			return "HTTPSOAP12";
		}
		else if(type.equals(HTTPENDPOINT)){
			return "HTTPENDPOINT";
		}
		return "Undefined";
	}
}
