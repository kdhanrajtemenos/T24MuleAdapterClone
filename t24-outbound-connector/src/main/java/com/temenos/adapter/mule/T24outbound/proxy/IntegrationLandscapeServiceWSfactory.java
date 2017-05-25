package com.temenos.adapter.mule.T24outbound.proxy;

import integrationlandscapeservicews.IntegrationLandscapeServiceWS;
import integrationlandscapeservicews.IntegrationLandscapeServiceWSPortType;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;

import java.net.MalformedURLException;
import java.net.URL;

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
        if (endpoint == null) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "011", "cannot initialize end point!");
        }
        return endpoint;
    }

}
