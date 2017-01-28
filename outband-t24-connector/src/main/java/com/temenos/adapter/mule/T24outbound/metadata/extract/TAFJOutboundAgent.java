package com.temenos.adapter.mule.T24outbound.metadata.extract;

import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataDiscoveryService;
import com.temenos.adapter.mule.T24outbound.metadata.model.T24MetadataDiscoveryServiceFactory;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;



public class TAFJOutboundAgent extends T24OutboundAPICall {

	public TAFJOutboundAgent( String signOnName, String password, IntegrationLandscapeServiceWSclient client) {
		super(client);
		MetadataDiscoveryService metadataDiscoveryService = T24MetadataDiscoveryServiceFactory.getWebserviceMetadataService(signOnName, password);
		
		outboundService = metadataDiscoveryService.getOutboundService();
		
	}
}
