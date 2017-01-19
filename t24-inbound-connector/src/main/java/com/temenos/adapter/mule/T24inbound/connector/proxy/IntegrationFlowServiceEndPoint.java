package com.temenos.adapter.mule.T24inbound.connector.proxy;

import org.mule.modules.t24inbound.definition.IntegrationFlowServiceWS;
import org.mule.modules.t24inbound.definition.IntegrationFlowServiceWSPortType;

public class IntegrationFlowServiceEndPoint {
	
	public static IntegrationFlowServiceWSPortType createEndPoint(IntegrationFlowServiceWS svc, ServiceEndPointType endPointType){
		if(endPointType.equals(ServiceEndPointType.HTTPSOAP11)){
			return svc.getIntegrationFlowServiceWSHttpSoap11Endpoint();
		}else if(endPointType.equals(ServiceEndPointType.HTTPSOAP12)){
			return svc.getIntegrationFlowServiceWSHttpSoap12Endpoint();
		}else {
			return svc.getIntegrationFlowServiceWSHttpEndpoint(); //default
		}
	}
	
}
