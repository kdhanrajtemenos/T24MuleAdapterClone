package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.ArrayList;
import java.util.List;

import com.jbase.jremote.JConnection;
import com.jbase.jremote.JDynArray;
import com.jbase.jremote.JRemoteException;
import com.jbase.jremote.JSubroutineParameters;
//import com.temenos.adapter.common.metadata.MetadataException;
import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;

public class TafcInboundMetadataDiscoveryService extends AbstractTafcMetadataDiscovery implements InboundMetadataDiscoveryService
{
/*	
private static final String METADATA_DISCOVERY_SERVICE = "IntegrationFlowService.getAllFlowNames";
private static final String METADATA_RETRIEVAL_SERVICE = "IntegrationFlowService.getFlowSchema";
private static final String COMPANY_RETRIEVAL_SERVICE = "IntegrationlandscapeService.getCompanies";
private static final int MASTER_SCHEMA_CONTENT_POSITION = 1;
private static final int MASTER_SCHEMA_LOCATION_POSITION = 2;
private static final int MASTER_SCHEMA_NAMESPACE_POSITION = 3;
private static final int IMPORTED_SCHEMA_NAMESPACE_POSITION = 4;
private static final int IMPORTED_SCHEMA_LOCATION_POSITION = 5;
private static final int IMPORTED_SCHEMA_CONTENT_POSITION = 6;
*/
private List<String> companies;

public TafcInboundMetadataDiscoveryService(TafcMetadataConnectionConfiguration configuration)
{
  super(configuration);
}

public MetadataTree getMetadataTree(IntegrationFlowServiceWSClient client)
  throws T24MetadataException
{
  JConnection connection = getConnection();
  JDynArray flowNames = getFlowNames(connection);
  T24MetadataTree metadataTree = new T24MetadataTree();
  for (int flowNameIndex = 1; flowNameIndex <= flowNames.getNumberOfAttributes(); flowNameIndex++) {
    metadataTree.addMetadata(new T24InboundMetadata(flowNames.get(flowNameIndex)));
  }
  metadataTree.setCompanies(getCompanyList());
  return metadataTree;
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
  String flowName = metadata.getName();
  JConnection connection = getConnection();
  JDynArray flowSchema = getFlowSchema(connection, flowName);
  return buildEventMatadata(flowSchema, flowName);
}

private JDynArray getFlowNames(JConnection connection)
  throws T24MetadataException
{
  JSubroutineParameters input = new JSubroutineParameters();
  input.add(new JDynArray());
  try
  {
    JSubroutineParameters output = connection.call("IntegrationFlowService.getAllFlowNames", input);
    
    JDynArray outputArr = (JDynArray)output.get(0);
    return outputArr;
  }
  catch (JRemoteException e)
  {
    throw new T24MetadataException("Metadata discovery service failed. " + e.getMessage());
  }
  finally
  {
    closeConnection(connection);
  }
}

private JDynArray getFlowSchema(JConnection connection, String flowName)
  throws T24MetadataException
{
  JSubroutineParameters input = new JSubroutineParameters();
  input.add(new JDynArray(flowName));
  input.add(new JDynArray());
  try
  {
    JSubroutineParameters output = connection.call("IntegrationFlowService.getFlowSchema", input);
    
    JDynArray outputArr = (JDynArray)output.get(1);
    return outputArr;
  }
  catch (JRemoteException e)
  {
    throw new T24MetadataException("Metadata retrieval service failed. " + e.getMessage());
  }
  finally
  {
    closeConnection(connection);
  }
}

private InboundMetadataDescription buildEventMatadata(JDynArray eventSchema, String flowName)
  throws T24MetadataException
{
  String masterSchemaContent = eventSchema.get(1);
  
  String masterSchemaLocation = eventSchema.get(2);
  
  String masterSchemaNamespace = eventSchema.get(3);
  
  MetadataSchema masterSchema = new MetadataSchema(masterSchemaNamespace, masterSchemaLocation, masterSchemaContent);
  
  int importedSchemaCount = eventSchema.getNumberOfValues(4);
  
  AdapterPortMetadataImpl portData = new AdapterPortMetadataImpl(getRootElementFromSchemaName(flowName), masterSchema);
  
  List<String> outputSchemaDocuments = new ArrayList<String>();
  for (int importedSchemaIndex = 1; importedSchemaIndex <= importedSchemaCount; importedSchemaIndex++)
  {
    MetadataSchema importedSchema = new MetadataSchema(eventSchema.get(4, importedSchemaIndex), eventSchema.get(5, importedSchemaIndex), eventSchema.get(6, importedSchemaIndex));
    
    portData.addImportedSchema(importedSchema);
    outputSchemaDocuments.add(eventSchema.get(6, importedSchemaIndex));
  }
  return new T24InboundDescription(flowName, portData, outputSchemaDocuments);
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

@SuppressWarnings("unchecked")
public List<String> getCompanyList()
  throws T24MetadataException
{
  if (this.companies != null) {
    return this.companies;
  }
  JSubroutineParameters input = new JSubroutineParameters();
  input.add(new JDynArray());
  JConnection connection = getConnection();
  try
  {
    JSubroutineParameters output = connection.call("IntegrationlandscapeService.getCompanies", input);
    
    this.companies = ((List<String>)output.get(0));
    
    return this.companies;
  }
  catch (JRemoteException e)
  {
    throw new T24MetadataException("Metadata discovery service failed. " + e.getMessage());
  }
  finally
  {
    closeConnection(connection);
  }
}
}