package org.mule.modules.t24inbound.automation.tests;

import org.mule.api.ConnectionException;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;

public class FlowServiceTest {
	

	public static void main(String[] args) throws ConnectionException {
		ConnectorConfig cf = new ConnectorConfig();

		cf.setPort(4447);
		cf.setAgentUser("INPUTT");
		cf.setAgentHost("localhost");
		cf.testConnect("INPUTT", "123456", "http://localhost:9089/axis2/services/IntegrationFlowServiceWS?wsdl","GB0010001", "D:/Schemas/UserPass.txt");
	}

}
