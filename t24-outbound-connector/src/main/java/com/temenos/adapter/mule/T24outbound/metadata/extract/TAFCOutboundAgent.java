package com.temenos.adapter.mule.T24outbound.metadata.extract;

import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataDiscoveryService;
import com.temenos.adapter.mule.T24outbound.metadata.model.T24MetadataDiscoveryServiceFactory;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;


public class TAFCOutboundAgent extends T24OutboundAPICall {


	
    public TAFCOutboundAgent(String hostName, int port, IntegrationLandscapeServiceWSclient client) {
       super(client);
       MetadataDiscoveryService metadataDiscoveryService = T24MetadataDiscoveryServiceFactory.getAgentMetadataService(hostName, port);
       outboundService = metadataDiscoveryService.getOutboundService();
    }
}
