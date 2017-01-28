package org.mule.modules.outboundT24.automation.functional;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.outboundT24.automation.tests.InitConnectorConfig;

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.connector.OutboundT24Connector;

public class BatchOfsmlTestCases  {

	private OutboundT24Connector connector;
	private InitConnectorConfig initConfig;
	
	
	@Before
	public void setup() throws ConnectionException {
		initConfig = InitConnectorConfig.getInstance();
		ConnectorConfig config = initConfig.getConfigFromFile(InitConnectorConfig.CONNECTOR_CFG_FILE);
		connector = new OutboundT24Connector();		
		connector.setConfig(config);
	}

	@After
	public void tearDown() {
		
	}
	

	@Test
	public void verifyBatchOfs() {
		Properties resource  = initConfig.readResourse(InitConnectorConfig.BATCH_OFSML_TEST);
		String singleBatchOfsmlRequest  = resource.getProperty(InitConnectorConfig.REQUEST);
		String expectedResponse = resource.getProperty(InitConnectorConfig.RESPONSE);

		System.out.println(InitConnectorConfig.REQUEST + ": " + singleBatchOfsmlRequest);
		String actualResponce = connector.batchOfsml(singleBatchOfsmlRequest);
		

		assertEquals(expectedResponse, actualResponce);
		
		System.out.println(InitConnectorConfig.RESPONSE + ": " + actualResponce);
		System.out.println("EXPECTED: " + expectedResponse);
	}

}