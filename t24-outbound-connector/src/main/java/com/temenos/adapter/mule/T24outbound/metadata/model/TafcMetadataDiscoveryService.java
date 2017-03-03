package com.temenos.adapter.mule.T24outbound.metadata.model;



public class TafcMetadataDiscoveryService implements MetadataDiscoveryService
{
	  private String agentHost;
	  private int agentPort;
	  
	  public TafcMetadataDiscoveryService(String agentHost, int agentPort)
	  {
		    this.agentHost = agentHost;
		    this.agentPort = agentPort;
	  }
	  
	
	  
	  public OutboundMetadataDiscoveryService getOutboundService()
	  {
	    TafcMetadataConnectionConfiguration metadataConnectionConfiguration = new TafcMetadataConnectionConfiguration(this.agentHost, this.agentPort);
	    
	    return new TafcOutboundMetadataDiscoveryService(metadataConnectionConfiguration);
	  }
}
