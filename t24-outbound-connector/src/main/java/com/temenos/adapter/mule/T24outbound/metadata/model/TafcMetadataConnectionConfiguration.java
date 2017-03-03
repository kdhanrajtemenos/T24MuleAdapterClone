package com.temenos.adapter.mule.T24outbound.metadata.model;


public class TafcMetadataConnectionConfiguration implements MetadataConnectionConfiguration
{
	  private String host;
	  private int port;
	  
	  public TafcMetadataConnectionConfiguration(String host, int port)
	  {
		    if (host == null || host.isEmpty() || !(port >1000 && port<65535)) {
		    	throw new IllegalArgumentException("Invalid argument: host [" + host + "] port [" + port + "]");
		    }
		    this.host = host;
		    this.port = port;
	  }
	  
	  public T24MetadataConnectionType getConnectionType()
	  {
		  return T24MetadataConnectionType.TAFC_AGENT;
	  }
	  
	  public String getHost()
	  {
		  return this.host;
	  }
	  
	  public int getPort()
	  {
		  return this.port;
	  }
}
