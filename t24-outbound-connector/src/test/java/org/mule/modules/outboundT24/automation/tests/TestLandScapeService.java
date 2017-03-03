package org.mule.modules.outboundT24.automation.tests;

import javax.xml.bind.JAXBElement;

import org.mule.api.ConnectionException;
import com.temenos.adapter.mule.T24outbound.definition.GetComposerLandscapeResponse;
import com.temenos.adapter.mule.T24outbound.definition.IntegrationLandscapeServiceWSPortType;
import com.temenos.adapter.mule.T24outbound.definition.ResponseDetails;
import com.temenos.adapter.mule.T24outbound.definition.Schema;
import com.temenos.adapter.mule.T24outbound.definition.T24UserDetails;

import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataDiscoveryServiceHelper;
import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataTree;
import com.temenos.adapter.mule.T24outbound.metadata.model.T24MetadataTree;
import com.temenos.adapter.mule.T24outbound.metadata.model.T24ServiceTreeBuilder;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;

public class TestLandScapeService {

	private ConnectorConfig config;

	public TestLandScapeService(ConnectorConfig config){
		this.config = config;
	}
	
	public MetadataTree getMetadataTree(IntegrationLandscapeServiceWSclient proxyClient, T24UserDetails user) throws T24MetadataException
	  {
		   IntegrationLandscapeServiceWSPortType service = proxyClient.getPort();
		    
		   T24UserDetails userDetails = user;
		    
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
	
	
	public static void main(String args[]) throws ConnectionException, T24MetadataException{
		
		ConnectorConfig config =  new ConnectorConfig();
		config.setT24Host("localhost");
		config.setT24User("INPUTT");
		config.setT24Port(4447);
		config.setT24RunTime(RuntimeConfigSelector.TAFJ);
		
		config.testConnect( "http://localhost:9089/axis2/services/");
		
		TestLandScapeService test = new TestLandScapeService(config);
		
		test.config.connect( config.getServiseURL());
		
		T24MetadataTree tree = (T24MetadataTree) test.getMetadataTree(test.config.getClient(), test.config.getUserWsDeatils());
		
		if(tree != null){
			System.out.println("OK");
		}
		//IntegrationLandscapeServiceWSclient client = test.config.getClient();
	}
	
}
