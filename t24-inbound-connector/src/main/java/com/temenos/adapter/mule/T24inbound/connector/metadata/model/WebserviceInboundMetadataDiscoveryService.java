package com.temenos.adapter.mule.T24inbound.connector.metadata.model;


//import com.temenos.adapter.common.metadata.MetadataException;

//import com.temenos.adapter.common.metadata.AdapterPortMetadataImpl;
//import com.temenos.adapter.common.metadata.InboundFlow;
//import com.temenos.adapter.common.metadata.InboundMetadataDescription;




import com.temenos.adapter.common.metadata.T24MetadataException;
/*

import com.temenos.services.integrationflow.data.response.xsd.GetAllFlowNamesResponse;
import com.temenos.services.integrationflow.data.response.xsd.GetFlowSchemaResponse;
import com.temenos.services.integrationflow.data.xsd.IntegrationFlowSchema;

import com.temenos.services.integrationlandscape.data.response.xsd.GetCompaniesResponse;

import com.temenos.soa.services.data.xsd.ObjectFactory;
import com.temenos.soa.services.data.xsd.ResponseDetails;
import com.temenos.soa.services.data.xsd.T24UserDetails;
*/
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;

//import integrationflowservicews.IntegrationFlowServiceWS;
//import integrationflowservicews.IntegrationFlowServiceWSPortType;

//import java.net.MalformedURLException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//import javax.xml.bind.JAXBElement;
//import javax.xml.namespace.QName;


////////////////
import org.mule.modules.t24inbound.definition.*;

///////////////

public class WebserviceInboundMetadataDiscoveryService
  implements InboundMetadataDiscoveryService
{
  private WebserviceMetadataConnectionConfiguration configuration;
 // private List<String> companies;
  
  public WebserviceInboundMetadataDiscoveryService(WebserviceMetadataConnectionConfiguration configuration)
  {
    this.configuration = configuration;
  }
  
  
  
  public T24MetadataTree getMetadataTree(IntegrationFlowServiceWSClient client) throws T24MetadataException
  {
    IntegrationFlowServiceWSPortType service = client.getPort();
    
    T24UserDetails t24UserDetails = buildT24UserDetails(this.configuration.getUserName(), this.configuration.getPassword());
    
    GetAllFlowNamesResponse2 getflowNamesResponse = service.getAllFlowNames(t24UserDetails);
    
    ResponseDetails responseDetailsElement = (ResponseDetails)getflowNamesResponse.getResponseDetails().getValue();
    if (!"FAILURE".equals(responseDetailsElement.getReturnCode().getValue()))
    {
      List<String> allFlowNames = getflowNamesResponse.getFlowNames();
      return buildEventMetaDataTree(allFlowNames);
    }
    String errorMessage = "Failed to retrieve metadata tree. Error:" + responseDetailsElement.getReturnCode();
    
    throw new T24MetadataException(errorMessage);
  }
  
  public InboundFlow getSelectedFlow(final Metadata selectedMetadata)
    throws T24MetadataException
  {
    if (selectedMetadata != null) {
      new InboundFlow()
      {
        public String getFlowName()
        {
          return selectedMetadata.getName();
        }
      };
    }
    throw new T24MetadataException("Invalid metadata [" + selectedMetadata + "]");
  }
  
  public InboundMetadataDescription getMetadataDescription(Metadata metadata, IntegrationFlowServiceWSClient client)
    throws T24MetadataException
  {
    if (metadata == null) {
      throw new NullPointerException("Metadata is null");
    }
    String eventName = metadata.getName();
    
    IntegrationFlowSchema flowSchema = getFlowSchema(eventName, client);
    return buildEventMetadata(flowSchema, eventName);
  }
  
  private T24MetadataTree buildEventMetaDataTree(List<String> allFlowNames)
    throws T24MetadataException
  {
    T24MetadataTree eventMetadataTree = new T24MetadataTree();
    for (String flowName : allFlowNames)
    {
      Metadata metadata = new T24InboundMetadata(flowName);
      eventMetadataTree.addMetadata(metadata);
    }
   // eventMetadataTree.setCompanies(getCompanyList());
    return eventMetadataTree;
  }
  
  private T24UserDetails buildT24UserDetails(String userName, String password)
  {
    ObjectFactory factory = new ObjectFactory();
    T24UserDetails userDetails = new T24UserDetails();
    userDetails.setUser(factory.createT24UserDetailsUser(userName));
    userDetails.setPassword(factory.createT24UserDetailsPassword(password));
    return userDetails;
  }
  
  private IntegrationFlowSchema getFlowSchema(String flowName, IntegrationFlowServiceWSClient client )
    throws T24MetadataException
  {
    IntegrationFlowServiceWSPortType service = client.getPort();
    
    T24UserDetails t24UserDetails = buildT24UserDetails(this.configuration.getUserName(), this.configuration.getPassword());
    
    GetFlowSchemaResponse2 flowSchemaResponse = service.getFlowSchema(t24UserDetails, flowName);
    
    ResponseDetails responseDetailsElement = (ResponseDetails)flowSchemaResponse.getResponseDetails().getValue();
    if (!"FAILURE".equals(responseDetailsElement.getReturnCode().getValue()))
    {
      IntegrationFlowSchema flowSchema = (IntegrationFlowSchema)flowSchemaResponse.getIntegrationFlowSchema().getValue();
      
      return flowSchema;
    }
    String errorMessge = "Failed to retrieve metadata for flow [" + flowName + "]. Error:" + responseDetailsElement.getReturnCode();
    
    throw new T24MetadataException(errorMessge);
  }
  
  private InboundMetadataDescription buildEventMetadata(IntegrationFlowSchema flowSchema, String flowName)
    throws T24MetadataException
  {
    MetadataSchema masterSchema = new MetadataSchema((String)flowSchema.getTargetNamespace().getValue(), (String)flowSchema.getSchemaName().getValue(), (String)flowSchema.getSchema().getValue());
    
    AdapterPortMetadataImpl outputPortData = new AdapterPortMetadataImpl(getRootElementFromSchemaName(flowName), masterSchema);
    
    int importedSchemaCount = flowSchema.getImportedNamespaces().size();
    List<String> outputSchemaDocuments = new ArrayList<String>();
    for (int importedSchemaIndex = 0; importedSchemaIndex < importedSchemaCount; importedSchemaIndex++)
    {
      String importedNameSpace = (String)flowSchema.getImportedNamespaces().get(importedSchemaIndex);
      
      String importedSchema = (String)flowSchema.getImportedSchemaNames().get(importedSchemaIndex);
      
      String importedSchemaDoc = (String)flowSchema.getImportedSchemaDocuments().get(importedSchemaIndex);
      
      MetadataSchema metaDataImportedSchema = new MetadataSchema(importedNameSpace, importedSchema, importedSchemaDoc);
      
      outputPortData.addImportedSchema(metaDataImportedSchema);
      outputSchemaDocuments.add(importedSchemaDoc);
    }
    return new T24InboundDescription(flowName, outputPortData, outputSchemaDocuments);
  }
  
  private String getRootElementFromSchemaName(String flowName)
    throws T24MetadataException
  {
    int rootElementNameStartIndex = flowName.indexOf('-') + 1;
    if ((rootElementNameStartIndex > 0) && (flowName.length() > rootElementNameStartIndex)) {
      return flowName.substring(rootElementNameStartIndex);
    }
    throw new T24MetadataException("Invalid flow Name [" + flowName + "]");
  }
  



  
  /*
  public List<String> getCompanyList()
    throws T24MetadataException
  {
    if (this.companies != null) {
      return this.companies;
    }
    IntegrationLandscapeServiceWSPortType service = getLandscapeService(this.configuration.getWebServiceUrl());
    
    T24UserDetails t24UserDetails = buildT24UserDetails(this.configuration.getUserName(), this.configuration.getPassword());
    
    GetCompaniesResponse companiesResponse = service.getCompanies(t24UserDetails);
    ResponseDetails responseDetailsElement = (ResponseDetails)companiesResponse.getResponseDetails().getValue();
    if (!"FAILURE".equals(responseDetailsElement.getReturnCode().getValue()))
    {
      this.companies = companiesResponse.getCompanyIds();
      return this.companies;
    }
    String errorMessge = "Failed to retrieve metadata. Error:" + MetadataDiscoveryServiceHelper.buildErrorMessage(responseDetailsElement);
    
    throw new T24MetadataException(errorMessge);
  }
  */
}