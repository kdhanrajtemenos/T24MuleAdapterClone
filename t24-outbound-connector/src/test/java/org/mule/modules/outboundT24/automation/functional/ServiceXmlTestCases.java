package org.mule.modules.outboundT24.automation.functional;

import static org.junit.Assert.*;


import java.util.Properties;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.outboundT24.automation.tests.InitConnectorConfig;

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.connector.T24OutboundConnector;



public class ServiceXmlTestCases  {

	private T24OutboundConnector connector;
	private InitConnectorConfig initConfig;
	
	
	@Before
	public void setup() throws ConnectionException {
		initConfig = InitConnectorConfig.getInstance();
		ConnectorConfig config = initConfig.getConfigFromFile(InitConnectorConfig.CONNECTOR_CFG_FILE);
		connector = new T24OutboundConnector();		
		connector.setConfig(config);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void verify() {
		Properties resource  = initConfig.readResourse(InitConnectorConfig.SERVICE_XML_TEST);
		String operation = resource.getProperty(InitConnectorConfig.OPERATION);
		String request = resource.getProperty(InitConnectorConfig.REQUEST);
		String expectedResponse = resource.getProperty(InitConnectorConfig.RESPONSE);
		
		System.out.println(InitConnectorConfig.REQUEST + ": " + request);
		String actualResponse = connector.serviceXml(operation, request);
		System.out.println(InitConnectorConfig.RESPONSE + ": " + actualResponse);
		System.out.println("EXPECTED: " + expectedResponse);
		assertEquals(expectedResponse, actualResponse);
		

	}

}