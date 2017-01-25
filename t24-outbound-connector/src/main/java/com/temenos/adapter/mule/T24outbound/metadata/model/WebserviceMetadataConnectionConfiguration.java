package com.temenos.adapter.mule.T24outbound.metadata.model;

public class WebserviceMetadataConnectionConfiguration implements MetadataConnectionConfiguration
{
	//private String webServiceUrl;
	private String userName;
	private String password;
	
	public WebserviceMetadataConnectionConfiguration(/*String webServiceUrl,*/ String userName, String password)
	{
		  //this.webServiceUrl = webServiceUrl;
		  this.userName = userName;
		  this.password = password;
	}
	
	public T24MetadataConnectionType getConnectionType()
	{
		return T24MetadataConnectionType.WEB_SERVICE;
	}
	
	/*
	public String getWebServiceUrl()
	{
		return this.webServiceUrl;
	}
	*/
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
}
