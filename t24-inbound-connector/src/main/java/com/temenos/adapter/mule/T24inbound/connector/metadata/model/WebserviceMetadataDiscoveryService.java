package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import com.temenos.adapter.mule.T24inbound.connector.metadata.model.WebserviceInboundMetadataDiscoveryService;

public class WebserviceMetadataDiscoveryService implements MetadataDiscoveryService
{
	//private String webserviceUrl;
	private String userName;
	private String password;
  
	public WebserviceMetadataDiscoveryService(String userName, String password)
	{
		//this.webserviceUrl = webserviceUrl;
		this.userName = userName;
		this.password = password;
	}
  

	  public InboundMetadataDiscoveryService getInboundService()
	  {
	    WebserviceMetadataConnectionConfiguration configuration = new WebserviceMetadataConnectionConfiguration(/*this.webserviceUrl, */this.userName, this.password);
	    
	    return new WebserviceInboundMetadataDiscoveryService(configuration);
	  }
}