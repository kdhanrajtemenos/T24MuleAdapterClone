package org.mule.modules.t24inbound.automation.tests;

import org.mule.api.ConnectionException;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;

public class FlowServiceTest {
	

	public static void main(String[] args) throws ConnectionException {
		ConnectorConfig cf = new ConnectorConfig();

		cf.setServiceUserName("INPUTT");
		cf.setServicePassword("123456");
		cf.testConnect("http://localhost:9089/axis2/services/IntegrationFlowServiceWS?wsdl");
	}

}
