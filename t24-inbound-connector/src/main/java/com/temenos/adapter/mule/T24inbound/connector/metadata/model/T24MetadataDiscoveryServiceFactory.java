package com.temenos.adapter.mule.T24inbound.connector.metadata.model;



public class T24MetadataDiscoveryServiceFactory
{
  
	public static MetadataDiscoveryService getAgentMetadataService(String agentHost, int agentPort)
	{
		return new TafcMetadataDiscoveryService(agentHost, agentPort);
	}
  
	public static MetadataDiscoveryService getWebserviceMetadataService(/*String webserviceUrl,*/ String userName, String password)
	{
		return new WebserviceMetadataDiscoveryService(/*webserviceUrl, */userName, password);
	}
  
}