package com.temenos.adapter.mule.T24outbound.proxy;

import org.mule.modules.outbandt24.definition.IntegrationLandscapeServiceWS;
import org.mule.modules.outbandt24.definition.IntegrationLandscapeServiceWSPortType;

public class IntegrationLandscapeServiceEndPoint {
	
	public static IntegrationLandscapeServiceWSPortType createEndPoint(IntegrationLandscapeServiceWS svc, ServiceEndPointType endPointType){
		if(endPointType.equals(ServiceEndPointType.HTTPSOAP11)){
			return svc.getIntegrationLandscapeServiceWSHttpSoap11Endpoint();
		}else if(endPointType.equals(ServiceEndPointType.HTTPSOAP12)){
			return svc.getIntegrationLandscapeServiceWSHttpSoap12Endpoint();
		}else {
			return svc.getIntegrationLandscapeServiceWSHttpEndpoint(); //default
		}
	}
	
}
