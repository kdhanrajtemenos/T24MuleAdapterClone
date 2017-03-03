package com.temenos.adapter.mule.T24outbound.metadata.model;

import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;


import com.temenos.adapter.mule.T24outbound.definition.GetComposerLandscapeResponse;

import com.temenos.adapter.mule.T24outbound.definition.IntegrationLandscapeServiceWSPortType;
import com.temenos.adapter.mule.T24outbound.definition.ObjectFactory;
import com.temenos.adapter.mule.T24outbound.definition.ResponseDetails;
import com.temenos.adapter.mule.T24outbound.definition.Schema;
import com.temenos.adapter.mule.T24outbound.definition.T24UserDetails;

public class WebserviceOutboundMetadataDiscoveryService implements OutboundMetadataDiscoveryService
{
	  private static final String T24SERVICE_ENQUIRY = "enquiry";
	  
	  private WebserviceMetadataConnectionConfiguration configuration;
	  
	  public WebserviceOutboundMetadataDiscoveryService(WebserviceMetadataConnectionConfiguration configuration)
	  {
	    this.configuration = configuration;
	  }
	  
	  public OutboundMetadataDescription getMetadataDescription(Metadata metadata, IntegrationLandscapeServiceWSclient proxyClient) throws T24MetadataException
	  {
		    if (metadata == null) {
		      throw new NullPointerException("Metadata is null");
		    }
		    /*tuk trqbva da vkaram moq service */
		    IntegrationLandscapeServiceWSPortType service = proxyClient.getPort();
		    
		    T24UserDetails userDetails = buildT24UserDetails();
		   
		    
		    T24ServiceOperationImpl serviceOperation = getOperation(metadata);
		    
		    if (serviceOperation.getAction().equals(T24SERVICE_ENQUIRY))
		    {
		      String responseSchema = (String)((Schema)service.getEnquirySchemaTyped(userDetails, serviceOperation.getTarget()).getEnquirySchema().getValue()).getSchema().getValue();
		      
		      List<String> schemas = new ArrayList<String>();
		      schemas.add(responseSchema);
		      return buildMetaDataDescription(serviceOperation, metadata.getName(), schemas);
		    }
		    
		    /*tuk sum go editaval*/
		    List<Schema> schemas = service.getVersionSchemasTyped(userDetails, serviceOperation.getTarget()).getSchemas();
		    
		    List<String> schemasList = getVersionSchemas(schemas);
		    return buildMetaDataDescription(serviceOperation, metadata.getName(), schemasList);
	  }
	  
	  public T24ServiceOperationImpl getServiceOperation(Metadata metadata) throws T24MetadataException
	  {
		  return getOperation(metadata);
	  }
	  
	  public MetadataTree getMetadataTree(IntegrationLandscapeServiceWSclient proxyClient) throws T24MetadataException
	  {
		   IntegrationLandscapeServiceWSPortType service = proxyClient.getPort();
		    
		   T24UserDetails userDetails = buildT24UserDetails();
		    
		   GetComposerLandscapeResponse response = service.getComposerLandscape(userDetails);
		    
		   ResponseDetails responseDetailsElement = (ResponseDetails)response.getResponseDetails().getValue();
		   if (!"FAILURE".equals(responseDetailsElement.getReturnCode().getValue()))
		   {
		    	JAXBElement<Schema> responseSchema = response.getLandscapeSchema();
		    	return new T24ServiceTreeBuilder().build((String)((Schema)responseSchema.getValue()).getSchema().getValue());
		   }
		   String errorMessge = "Failed to retrieve metadata tree. Error:" + MetadataDiscoveryServiceHelper.buildErrorMessage(responseDetailsElement);
		    
		   throw new T24MetadataException(errorMessge);
	  }
	  /*
	   * THIS METHOD IS NOT WORKING
	   * 
	  private IntegrationLandscapeServiceWSPortType getService() throws T24MetadataException
	  {
		    String landscapeServiceUrl = this.configuration.getWebServiceUrl();
		    try
		    {
			      URL wsdlUrl = new URL(landscapeServiceUrl + "/IntegrationLandscapeServiceWS?wsdl");
			      IntegrationLandscapeServiceWS service = new IntegrationLandscapeServiceWS(wsdlUrl, new QName("http://IntegrationLandscapeServiceWS", "IntegrationLandscapeServiceWS"));
			      
			      return service.getIntegrationLandscapeServiceWSHttpSoap11Endpoint();
		    }
		    catch (MalformedURLException e)
		    {
		    	throw new T24MetadataException("Metadata discovery failed. T24 Landscape service URL is not well-formed [" + landscapeServiceUrl + "]");
		    }
		    catch (WebServiceException e)
		    {
		    	throw new T24MetadataException("Metadata discovery failed [" + e.getMessage() + "]");
		    }
	  }
	  */
	  private List<String> getVersionSchemas(List<Schema> schemas)
	  {
		    List<String> schemasList = new ArrayList<String>();
		    for (Schema schema : schemas) {
		      schemasList.add(schema.getSchema().getValue());
		    }
		    return schemasList;
	  }
	  
	  private OutboundMetadataDescription buildMetaDataDescription(T24ServiceOperationImpl serviceOperation, String name, List<String> schemasList) throws T24MetadataException
	  {
		  return MetadataDiscoveryServiceHelper.buildDescription(serviceOperation, name, schemasList);
	  }
	  
	  private T24ServiceOperationImpl getOperation(Metadata metadata)	throws T24MetadataException
	  {
		    if ((metadata instanceof T24OperationMetadata)) {
		    	return ((T24OperationMetadata)metadata).getOperation();
		    }
		    throw new T24MetadataException("Invalid metadata selected [" + metadata + "]");
	  }
	  
	  private T24UserDetails buildT24UserDetails()
	  {
		    ObjectFactory factory = new ObjectFactory();
		    T24UserDetails userDetails = new T24UserDetails();
		    userDetails.setUser(factory.createT24UserDetailsUser(this.configuration.getUserName()));
		    userDetails.setPassword(factory.createT24UserDetailsPassword(this.configuration.getPassword()));
		    return userDetails;
	  }
}
