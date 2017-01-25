package com.temenos.adapter.mule.T24outbound.metadata.model;


import com.temenos.adapter.common.metadata.MetadataException;

import com.temenos.adapter.common.metadata.ServiceOperation;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;

public abstract interface OutboundMetadataDiscoveryService
{
	
	public abstract MetadataTree getMetadataTree(IntegrationLandscapeServiceWSclient proxyClient) throws MetadataException;
	
	public abstract ServiceOperation getServiceOperation(Metadata paramMetadata) throws MetadataException;
  
	public abstract OutboundMetadataDescription getMetadataDescription(Metadata paramMetadata, IntegrationLandscapeServiceWSclient proxyClient) throws MetadataException;
	
	
}