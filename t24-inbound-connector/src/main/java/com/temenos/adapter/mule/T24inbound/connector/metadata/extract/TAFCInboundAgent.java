package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;

import com.temenos.adapter.mule.T24inbound.connector.metadata.model.MetadataDiscoveryService;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.T24MetadataDiscoveryServiceFactory;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;


public class TAFCInboundAgent extends T24InboundAPICall {


	
    public TAFCInboundAgent(String hostName, int port, IntegrationFlowServiceWSClient client) {
       super(client);
       MetadataDiscoveryService metadataDiscoveryService = T24MetadataDiscoveryServiceFactory.getAgentMetadataService(hostName, port);
       inboundService = metadataDiscoveryService.getInboundService();
    }
}
