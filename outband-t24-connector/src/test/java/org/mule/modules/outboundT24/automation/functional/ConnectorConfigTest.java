package org.mule.modules.outboundT24.automation.functional;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mule.api.ConnectionException;

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.config.RuntimeConfigSelector;


public class ConnectorConfigTest {
	
	@Test
	public void testConnectorConfigTest() throws ConnectionException{
		ConnectorConfig cf = new ConnectorConfig();
		cf.setT24Host("localhost");
		cf.setT24Port(4447);
		cf.setT24RunTime(RuntimeConfigSelector.TAFJ);
		
		cf.setT24User("INPUTT");
		//cf.setAgentPass("123456");
		cf.setSettingsFilePath("D:/Schemas4");
		cf.testConnect("http://localhost:9089/axis2/services/");
		boolean conStatus = cf.isConnected();
		assertTrue(conStatus);
		
		
		if(conStatus){
			System.out.println("Method testConnect works!");
		}else{
			System.out.println("Method testConnect has failed!");
		}
	}
	

	
}
