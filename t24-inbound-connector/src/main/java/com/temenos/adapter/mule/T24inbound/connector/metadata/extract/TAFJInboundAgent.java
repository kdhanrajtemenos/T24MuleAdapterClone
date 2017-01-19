package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;

import com.temenos.adapter.mule.T24inbound.connector.metadata.model.MetadataDiscoveryService;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.T24MetadataDiscoveryServiceFactory;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;;



public class TAFJInboundAgent extends T24InboundAPICall {

	public TAFJInboundAgent( String signOnName, String password, IntegrationFlowServiceWSClient client) {
		super(client);
		MetadataDiscoveryService metadataDiscoveryService = T24MetadataDiscoveryServiceFactory.getWebserviceMetadataService(signOnName, password);
		
		inboundService = metadataDiscoveryService.getInboundService();
		
	}
}
