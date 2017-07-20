package org.mule.modules.t24inbound.automation.tests;

import org.mule.api.ConnectionException;

import com.temenos.adapter.mule.T24inbound.connector.config.TAFJConnectorConfig;

public class FlowServiceTest {
	

	public static void main(String[] args) throws ConnectionException {
		TAFJConnectorConfig cf = new TAFJConnectorConfig();
		cf.setServiceUserName("INPUTT");
		cf.setServicePassword("123456");
		cf.testConnect("INPUTT", "INPUTT");
		cf.setServiceUrl("http://localhost:9089/axis2/services/IntegrationFlowServiceWS?wsdl");
	}

}
