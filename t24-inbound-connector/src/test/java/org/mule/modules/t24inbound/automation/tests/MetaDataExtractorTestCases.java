package org.mule.modules.t24inbound.automation.tests;

//import java.util.List;

import org.mule.api.ConnectionException;

import com.temenos.adapter.mule.T24inbound.connector.T24InboundConnector;
import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;

public class MetaDataExtractorTestCases {

	
	public static void main(String[] args) throws ConnectionException {
		ConnectorConfig config = new ConnectorConfig();

		config.setServiceUserName("INPUTT");
		config.setServicePassword("123456");
		config.testConnect("http://localhost:9089/axis2/services/IntegrationFlowServiceWS?wsdl");
		
		T24InboundConnector connector = new T24InboundConnector();
		connector.setConfig(config);
		//List<T24Event>= connector.eventPool( null, "FinancialReporting-Category");
		
		System.out.println("RESULT: " + "");
		/*
		T24InboundDesignTimeMetaDataExtractor extractor =  new T24InboundDesignTimeMetaDataExtractor(config);
		if(extractor.getEventsNames().isEmpty()){
			System.out.println("Error metadata extract");
		}else{
			System.out.println("Metadata extract ok!");
		}
		*/

	}
}
