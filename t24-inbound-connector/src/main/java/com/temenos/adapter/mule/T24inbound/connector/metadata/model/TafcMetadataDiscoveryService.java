package com.temenos.adapter.mule.T24inbound.connector.metadata.model;



public class TafcMetadataDiscoveryService implements MetadataDiscoveryService
{
	  private String agentHost;
	  private int agentPort;
	  
	  public TafcMetadataDiscoveryService(String agentHost, int agentPort)
	  {
		    this.agentHost = agentHost;
		    this.agentPort = agentPort;
	  }
	  
	
	  public InboundMetadataDiscoveryService getInboundService()
	  {
	    TafcMetadataConnectionConfiguration metadataConnectionConfiguration = new TafcMetadataConnectionConfiguration(this.agentHost, this.agentPort);
	    
	    return new TafcInboundMetadataDiscoveryService(metadataConnectionConfiguration);
	  }
}
