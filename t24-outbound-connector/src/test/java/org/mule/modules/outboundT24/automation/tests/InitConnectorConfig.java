package org.mule.modules.outboundT24.automation.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24outbound.utils.IoResourceUtil;
import com.temenos.adapter.mule.T24outbound.utils.PasswdUtil;

public class InitConnectorConfig {
	
	/* FOR CONNECTOR CONFIGURATION */	
//	private static final String WS_USR_NAME = "WS_USR_NAME";
//	private static final String WS_ENC_PASS= "WS_ENC_PASS";
	
	private static final String AGENT_USR_NAME = "AGENT_USR_NAME";
	private static final String AGENT_ENC_PASS= "AGENT_ENC_PASS";
	
	private static final String WSDL_LOCATION = "WSDL_LOCATION";
	private static final String AGENT_PORT= "AGENT_PORT";
	private static final String AGENT_HOST= "AGENT_HOST";
	private static final String AGENT_TYPE= "AGENT_TYPE";
	private static final String SERVICE_XML_SCHEMA_DIR = IoResourceUtil.SHEMA_DIR_KEY;
	
	/*PRE-DEFINED TEST FILES */
	private static final String CONNECTOR_TEST_DIR = "testExamples/";
	public static final String CONNECTOR_CFG_FILE = "credentials.prop";
	public static final String OFS_TEST= CONNECTOR_TEST_DIR +"ofs.txt";
	public static final String OFSML_TEST= CONNECTOR_TEST_DIR +"ofsml.txt";
	public static final String BATCH_OFS_TEST= CONNECTOR_TEST_DIR + "batchOfs.txt";
	public static final String BATCH_OFSML_TEST= CONNECTOR_TEST_DIR + "batchOfsml.txt";
	public static final String SERVICE_XML_TEST= CONNECTOR_TEST_DIR +"serviceXml.txt";
	public static final String CUSTOM_XML_TEST= CONNECTOR_TEST_DIR +"customXml.txt";
	
	/* FOR SERVICE XML */
	public static final String OPERATION="OPERATION";	
	public static final String rootNameRequest="rootNameRequest";
	public static final String rootNameResponse="rootNameResponse";
	public static final String serviceOperationAction="serviceOperationAction";
	public static final String serviceOperationName="serviceOperationName";
	public static final String serviceOperationTarget="serviceOperationTarget";
	

	/* FOR ALL TEST  FILES */
	public static final String REQUEST = "REQUEST";	
	public static final String RESPONSE = "RESPONSE";
	
	
	private static InitConnectorConfig instance = null;
	
	private InitConnectorConfig(){
		
	}
	
	public static InitConnectorConfig getInstance(){
		if(instance==null){
			synchronized(InitConnectorConfig.class){
				if(instance==null){
					instance = new InitConnectorConfig();
				}
			}
		}
		return instance;
	}

	private InputStream readConfigarableResouse(String resourseFile) throws RuntimeException {
		InputStream is = null;
		try{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourseFile);
		}catch(NullPointerException e){
			throw new RuntimeException(e.getMessage());
		}
		return is;
	}

	public Properties readResourse(String filename)  throws RuntimeException {
		if(filename==null || filename.isEmpty()){
			throw new RuntimeException("Incorrect resourse file");
		}
		
		Properties prop = new Properties();
		InputStream fis = readConfigarableResouse(filename);
		if(fis != null){
			try {
				prop.load(fis);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}finally{
				try {
					fis.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
			return prop;
		}else{
			throw new RuntimeException("Cannot find resourse");
		}	
	}
	
	public ConnectorConfig getConfigFromFile(String configfile) throws ConnectionException {
		Properties configProperties = null;
		try{
			configProperties = readResourse(configfile); 
		}catch(RuntimeException e){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"401","Incorrect config resource!");
		}
		
//		String username = configProperties.getProperty(WS_USR_NAME, "INPUTT");
//		String password = configProperties.getProperty(WS_ENC_PASS, "123456");
//		if(!password.endsWith("123456")){
//			password = PasswdUtil.decrypt(password);
//		}
		
		String agenthost = configProperties.getProperty(AGENT_HOST, "localhost");
		String _agentport = configProperties.getProperty(AGENT_PORT, "4447");
		String agentType = configProperties.getProperty(AGENT_TYPE, "TAFJ");
		
		String _agentUser = configProperties.getProperty(AGENT_USR_NAME, "INPUTT");
		String _agentPass = configProperties.getProperty(AGENT_ENC_PASS, "123456");
		String _directory = configProperties.getProperty(SERVICE_XML_SCHEMA_DIR, "D:/Schemas");
		
		if(!_agentPass.endsWith("123456")){
			_agentPass = PasswdUtil.decrypt(_agentPass);
		}
		
		String wsdllocation = configProperties.getProperty(WSDL_LOCATION, "http://localhost:9089/axis2/services/IntegrationLandscapeServiceWS?wsdl");
		Integer port = 4447;
		try{
			port = Integer.parseInt(_agentport);
			if(port<1000 || port>65535){
				throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"402","Incorect port setting!");
			}
		}catch(NumberFormatException e){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"403","Incorect port setting!");
		}
		ConnectorConfig config = new ConnectorConfig();
		config.setT24Host(agenthost);
		config.setT24Port(port);
		
		config.setT24User(_agentUser);
		//config.setAgentPass(_agentPass);
		config.setSettingsFilePath(_directory);
		
		if(agentType.equals("TAFJ")){
			config.setT24RunTime(RuntimeConfigSelector.TAFJ);
		}/*else{
			config.setT24RunTime(RuntimeConfigSelector.TAFC);
		}*/
		config.connect(wsdllocation);

		return config;

	}

	

}
