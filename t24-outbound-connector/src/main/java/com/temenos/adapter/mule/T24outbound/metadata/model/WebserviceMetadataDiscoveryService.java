package com.temenos.adapter.mule.T24outbound.metadata.model;

//import com.temenos.adapter.common.metadata.outbound.WebserviceOutboundMetadataDiscoveryService;

public class WebserviceMetadataDiscoveryService implements MetadataDiscoveryService
{
	//private String webserviceUrl;
	private String userName;
	private String password;
  
	public WebserviceMetadataDiscoveryService(/*String webserviceUrl, */String userName, String password)
	{
		//this.webserviceUrl = webserviceUrl;
		this.userName = userName;
		this.password = password;
	}
  

  
	public OutboundMetadataDiscoveryService getOutboundService()
	{
		WebserviceMetadataConnectionConfiguration configuration = new WebserviceMetadataConnectionConfiguration(/*this.webserviceUrl, */this.userName, this.password);
    
		return new WebserviceOutboundMetadataDiscoveryService(configuration);
	}
}