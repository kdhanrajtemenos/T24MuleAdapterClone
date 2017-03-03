package com.temenos.adapter.mule.T24outbound.proxy;

import java.net.MalformedURLException;
import java.net.URL;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import com.temenos.adapter.mule.T24outbound.definition.IntegrationLandscapeServiceWS;
import com.temenos.adapter.mule.T24outbound.definition.IntegrationLandscapeServiceWSPortType;

public class IntegrationLandscapeServiceWSfactory {
	
	private IntegrationLandscapeServiceWSclient proxyClient;

	public IntegrationLandscapeServiceWSfactory(IntegrationLandscapeServiceWSclient integrationLandscapeServiceWSclient) {
		this.proxyClient = integrationLandscapeServiceWSclient;
	}

	public IntegrationLandscapeServiceWSPortType initialize() throws ConnectionException {
		String wsdlLoacation = proxyClient.getWsdlLoaction();
		IntegrationLandscapeServiceWS svc = null;
		try {
			svc = new IntegrationLandscapeServiceWS(new URL(wsdlLoacation));
		} catch (MalformedURLException e) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "010", "incorrect wsdl location!");
		}
		
		IntegrationLandscapeServiceWSPortType endpoint = IntegrationLandscapeServiceEndPoint.createEndPoint(svc, proxyClient.getEndPointType());
		if(endpoint == null){
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "011", "cannot initialize end point!");
		}
		return endpoint;
	}

}
