package org.mule.modules.outboundT24.automation.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.mule.modules.outboundT24.automation.functional.*;
import org.junit.runner.Result;

/**
 * 
 * @author petar.zhivkov
 * JUnit test suit
 */

public class JUnitTestApi {
	
	private void executeTest(Class<?> clazz){
		Result result = JUnitCore.runClasses(clazz);
		for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	    }	
	    System.out.println(result.wasSuccessful());
	}
	
	/* Full Integration test */
	public static void main(String[] args) {
		JUnitTestApi api_test = new JUnitTestApi();
		
		api_test.executeTest(ConnectorConfigTest.class);
		
		api_test.executeTest(MetaDataExtractorTestCases.class);
		/*
		api_test.executeTest(SingleOfsTestCases.class);
		api_test.executeTest(BatchOfsmlTestCases.class);
		api_test.executeTest(BatchOfsTestCases.class);
		
		api_test.executeTest(SingleOfsmlTestCases.class);
		api_test.executeTest(ServiceXmlTestCases.class);
		*/
	}
	
}
