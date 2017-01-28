package org.mule.modules.outboundT24.automation.runner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.modules.outboundT24.automation.functional.BatchOfsTestCases;
import org.mule.modules.outboundT24.automation.functional.BatchOfsmlTestCases;

import org.mule.modules.outboundT24.automation.functional.MetaDataExtractorTestCases;
import org.mule.modules.outboundT24.automation.functional.ServiceXmlTestCases;
import org.mule.modules.outboundT24.automation.functional.SingleOfsTestCases;
import org.mule.modules.outboundT24.automation.functional.SingleOfsmlTestCases;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

import com.temenos.adapter.mule.T24outbound.connector.OutboundT24Connector;

@RunWith(Suite.class)
@SuiteClasses({ BatchOfsTestCases.class, BatchOfsmlTestCases.class, ServiceXmlTestCases.class,
		SingleOfsTestCases.class, SingleOfsmlTestCases.class, MetaDataExtractorTestCases.class})

public class FunctionalTestSuite {

	@BeforeClass
	public static void initialiseSuite() {
		ConnectorTestContext.initialize(OutboundT24Connector.class);
	}

	@AfterClass
	public static void shutdownSuite() {
		ConnectorTestContext.shutDown();
	}

}