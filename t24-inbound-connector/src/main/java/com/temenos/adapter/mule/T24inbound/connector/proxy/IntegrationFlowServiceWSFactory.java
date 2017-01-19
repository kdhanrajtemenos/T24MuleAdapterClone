package com.temenos.adapter.mule.T24inbound.connector.proxy;

import java.net.MalformedURLException;
import java.net.URL;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;

import org.mule.modules.t24inbound.definition.IntegrationFlowServiceWS;
import org.mule.modules.t24inbound.definition.IntegrationFlowServiceWSPortType;

public class IntegrationFlowServiceWSFactory {
	
	private IntegrationFlowServiceWSClient proxyClient;

	public IntegrationFlowServiceWSFactory(IntegrationFlowServiceWSClient integrationLandscapeServiceWSclient) {
		this.proxyClient = integrationLandscapeServiceWSclient;
	}

	public IntegrationFlowServiceWSPortType initialize() throws ConnectionException {
		String wsdlLoacation = proxyClient.getWsdlLoaction();
		IntegrationFlowServiceWS svc = null;
		try {
			svc = new IntegrationFlowServiceWS(new URL(wsdlLoacation));
		} catch (MalformedURLException e) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "010", "incorrect wsdl location!");
		}
		
		IntegrationFlowServiceWSPortType endpoint = IntegrationFlowServiceEndPoint.createEndPoint(svc, proxyClient.getEndPointType());
		if(endpoint == null){
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "011", "cannot initialize end point!");
		}
		return endpoint;
	}

}
