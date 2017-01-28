package com.temenos.adapter.mule.T24outbound.metadata.model;


import java.util.ArrayList;
import java.util.List;

import com.jbase.jremote.JConnection;
import com.jbase.jremote.JDynArray;
import com.jbase.jremote.JRemoteException;
import com.jbase.jremote.JSubroutineParameters;


import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.common.metadata.outbound.ActionType;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;

public class TafcOutboundMetadataDiscoveryService extends AbstractTafcMetadataDiscovery implements OutboundMetadataDiscoveryService {

	@SuppressWarnings("unused")
	private static final String METADATA_DISCOVERY_SERVICE = "IntegrationLandscapeService.getComposerLandscape";
	@SuppressWarnings("unused")
	private static final String T24VERSION_SCHEMA_SERVICE = "IntegrationLandscapeService.getVersionSchemasTyped";
	@SuppressWarnings("unused")
	private static final String T24ENQUIRY_SCHEMA_SERVICE = "IntegrationLandscapeService.getEnquirySchemaTyped";
	  
	public TafcOutboundMetadataDiscoveryService(TafcMetadataConnectionConfiguration configuration)
	{
	    super(configuration);
	}


	@Override
	public MetadataTree getMetadataTree(IntegrationLandscapeServiceWSclient client)  throws T24MetadataException
	{
		 JSubroutineParameters input = new JSubroutineParameters();
		 input.add(new JDynArray());
		 JConnection connection = getConnection();
		 try
		 {
		      JSubroutineParameters output = connection.call("IntegrationLandscapeService.getComposerLandscape", input);
			      
			  JDynArray outputArr = (JDynArray)output.get(0);
		      String servicesXml = outputArr.get(1);
		      return new T24ServiceTreeBuilder().build(servicesXml);
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
			  
	public T24ServiceOperationImpl getServiceOperation(Metadata metadata) throws T24MetadataException
	{
		 return getOperation(metadata);
	}
	
	@Override
	public OutboundMetadataDescription getMetadataDescription(Metadata metadata, IntegrationLandscapeServiceWSclient client) throws T24MetadataException
	{
	    if (metadata == null) {
		     throw new NullPointerException("Metadata is null");
	    }
	    T24ServiceOperationImpl operation = getOperation(metadata);
	    JConnection connection = getConnection();
	    try
	    {
	    	JSubroutineParameters input = new JSubroutineParameters();
			input.add(new JDynArray(operation.getTarget()));
			input.add(new JDynArray());
			JSubroutineParameters output = connection.call(getServiceName(operation), input);
			      
			return buildMetadataDescription(operation, metadata.getName(), output);
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
			  
	private T24ServiceOperationImpl getOperation(Metadata metadata) throws T24MetadataException
	{
	    if ((metadata instanceof T24OperationMetadata)) {
	    	return ((T24OperationMetadata)metadata).getOperation();
		}
		throw new T24MetadataException("Invalid metadata selected [" + metadata + "]");
	}
			  
	private OutboundMetadataDescription buildMetadataDescription(T24ServiceOperationImpl serviceOperation, String name, JSubroutineParameters output)  throws T24MetadataException
	{
	    List<String> schemasList = extractSchemas(output);
	    return MetadataDiscoveryServiceHelper.buildDescription(serviceOperation, name, schemasList);
	}
			  
	private List<String> extractSchemas(JSubroutineParameters output)
	{
		JDynArray schemas = (JDynArray)output.get(1);
		int schemaSize = schemas.getNumberOfAttributes();
		List<String> schemasList = new ArrayList<String>();
		for (int pos = 1; pos <= schemaSize; pos++) {
			schemasList.add(schemas.get(pos));
		}
		return schemasList;
	}
			  
	private String getServiceName(T24ServiceOperationImpl operation)
	{
		 return ActionType.ENQUIRY.equals(operation.getActionType()) ? "IntegrationLandscapeService.getEnquirySchemaTyped" : "IntegrationLandscapeService.getVersionSchemasTyped";
	}





}
