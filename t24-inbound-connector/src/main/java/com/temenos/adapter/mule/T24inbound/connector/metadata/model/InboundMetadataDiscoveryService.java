package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import com.temenos.adapter.common.metadata.MetadataException;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;

public abstract interface InboundMetadataDiscoveryService
{
  public abstract MetadataTree getMetadataTree(IntegrationFlowServiceWSClient client)
    throws MetadataException;
  
  public abstract InboundFlow getSelectedFlow(Metadata paramMetadata)
    throws MetadataException;
  
  public abstract InboundMetadataDescription getMetadataDescription(Metadata metadata, IntegrationFlowServiceWSClient client)
    throws MetadataException;
  /*
  public abstract List<String> getCompanyList()
    throws MetadataException;*/
}