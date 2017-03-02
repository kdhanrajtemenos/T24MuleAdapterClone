package com.temenos.adapter.mule.T24inbound.connector.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

//import javax.transaction.TransactionManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Required;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.modules.t24inbound.definition.ObjectFactory;
import org.mule.modules.t24inbound.definition.Response;
import org.mule.modules.t24inbound.definition.ResponseDetails;
import org.mule.modules.t24inbound.definition.T24UserDetails;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorOperationMode;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigServerSelector;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;
import com.temenos.adapter.mule.T24inbound.connector.proxy.ServiceEndPointType;
import com.temenos.adapter.mule.T24inbound.connector.utils.AddressChecker;
import com.temenos.adapter.mule.T24inbound.connector.utils.UserCredentialsInterface;


@ConnectionManagement(configElementName = "config", friendlyName = "Connection type parameters")
public class ConnectorConfig {
	
    protected final transient Log log = LogFactory.getLog(getClass());

    private final String WS_NAME = "/IntegrationFlowServiceWS?wsdl";
	private final String WS_HOST = "http://localhost:9089/axis2/services";	
	
	public static final String DEFAULT_SETTINGS_FILE_NAME = "settings.txt"; 
	

	/**
	 * Holding the SOAP User credentials
	 */
	private org.mule.modules.t24inbound.definition.T24UserDetails userWsDeatils;		

	
  	public T24UserDetails getUserWsDeatils() {
		return userWsDeatils;
	}

	public void setUserWsDeatils(T24UserDetails userWsDeatils) {
		this.userWsDeatils = userWsDeatils;
	}
	
	/** 
	 * Sets a numeric filed in the design-time connector configuration window
	 * for inputting the T24 Runtime port
	 * */
	@Configurable
	@Default(value="4447")
	@Placement(order = 3, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Port")
    @Summary("T24 runtime connection port value")
	private Integer t24Port;

	public Integer getT24Port() {
		return t24Port;
	}

	public void setT24Port(Integer t24Port) {
		this.t24Port = t24Port;
	}
	
	/** 
	 * Sets a text filed in the design-time connector configuration window
	 * for inputting the T24 Runtime host address
	 * */
	@Configurable
	@Required
	@Default(value="localhost")
	@Placement(order = 2, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Host")
    @Summary("T24 runtime connection host name")
	private String t24Host;
	
	public String getT24Host() {
		return t24Host;
	}

	public void setT24Host(String t24Host) {
		this.t24Host = t24Host;
	}
	
	/** 
	 * Sets a text filed in the design-time connector configuration window
	 * for inputting the T24 Runtime user name
	 * */
	@Configurable
	@Required
	@Default(value = "SSOUSER1")
	@Placement(order = 4, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 User")
    @Summary("T24 runtime connection user name")
	private String t24User;
	
	public String getT24User() {
		return t24User;
	}

	public void setT24User(String t24User) {
		this.t24User = t24User;
	}
	
	
	/** 
	 * Sets a text filed in the design-time connector configuration window
	 * for inputting the T24 Runtime password
	 * */
	@Configurable
	@Required
	@Password
	@Default(value = "123456")
	@Placement(order = 5, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Password")
    @Summary("T24 runtime connection user password")
	private String t24Password;
	
	public String getT24Password() {
		return t24Password;
	}

	public void setT24Password(String t24password) {
		this.t24Password = t24password;
	}
	
	/** 
	 * Sets drop down list in the design-time connector configuration window
	 * for selecting the T24 Runtime (either TAFJ or TAFC)
	 * */
	@Configurable
	@Default(value = "JBOSS72")
	@Placement(order = 1, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Server Type")
    @Summary("T24 instance application server type")
	private RuntimeConfigServerSelector t24RunTime;
	
	public RuntimeConfigServerSelector getT24RunTime() {
		return t24RunTime;
	}

	public void setT24RunTime(RuntimeConfigServerSelector t24RunTime) {
		this.t24RunTime = t24RunTime;
	}
	
	
    @Configurable
    @Default("node1")
    @FriendlyName("T24 Jboss Node")
    @Placement(order = 6, group = "Connector Runtime Configuration", tab = "Runtime")
    @Summary("T24 JBoss server node name")
    private String nodeName;
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Configurable
    @Default("true")
    @FriendlyName("T24 Ejb Stateful")
    @Placement(order = 7, group = "Connector Runtime Configuration", tab = "Runtime")
    @Summary("T24 ejb stateful option")
    private Boolean ejbStateful;
	
    public Boolean getEjbStateful() {
		return ejbStateful;
	}

	public void setEjbStateful(Boolean ejbStateful) {
		this.ejbStateful = ejbStateful;
	}

    @Configurable
    @Default("")
    @FriendlyName("T24 Ejb Bean Name")
    @Placement(order = 8, group = "Connector Runtime Configuration", tab = "Runtime")
    @Summary("T24 JNDI ejb name")
    private String ejbName;
	
	public String getEjbName() {
		return ejbName;
	}

	public void setEjbName(String ejbName) {
		this.ejbName = ejbName;
	}

	
	
	/**
	 * The SOAP client
	 */
	private IntegrationFlowServiceWSClient client;

	public IntegrationFlowServiceWSClient getClient() {
		return client;
	}

	public void setClient(IntegrationFlowServiceWSClient client) {
		this.client = client;
	}
	
	private String settingsFolder;

	public String getSettingsFolder() {
		return settingsFolder;
	}

	public void setSettingsFolder(String settingsFolder) {
		this.settingsFolder = settingsFolder;
	}
	
	private String settingsFileName;

	public String getSettingsFileName() {
		return settingsFileName;
	}

	public void setSettingsFileName(String settingsFileName) {
		this.settingsFileName = settingsFileName;
	}

	private String serviseURL;
	
	public String getServiseURL() {
		return serviseURL;
	}

	public void setServiseURL(String serviseURL) {
		this.serviseURL = serviseURL;
	}
	
	/**
	 * ConnectorConfig operational mode: either DESIGN_TIME or RUN_TIME 
	 * it is internal not an UI element, just to mark the current operation mode
	 * because connector use this class both in design-time and run-time
	 */
	private ConnectorOperationMode connectorMode;
	

	public ConnectorOperationMode getConnectorMode() {
		return connectorMode;
	}

	public void setConnectorMode(ConnectorOperationMode connectorMode) {
		this.connectorMode = connectorMode;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
// Connection tab parameters ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Web service credentials User name
	 */
	@Configurable
	@Default("INPUTT")
	@Required 
	@Placement(order = 1, group = "Connection", tab = "General")
	private String serviceUserName;
	
	public String getServiceUserName() {
		return serviceUserName;
	}

	public void setServiceUserName(String serviceUserName) {
		this.serviceUserName = serviceUserName;
	}
	
	
	/**
	 * Web service credentials User password
	 */
	@Configurable
	@Default("123456")
	@Required
	@Password
	@Placement(order = 2, group = "Connection", tab = "General")
	private String servicePassword;

	public String getServicePassword() {
		return servicePassword;
	}

	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}
	
	/**
	 * T24 company code used runtime, but have to cover the same parameter in design time method
	 * 
	 * 

	@Configurable
	@Default("GB0010001")
	@Placement(order = 3, group = "Connection", tab = "General")
	private String coCode;
	
	public String getCoCode() {
		return coCode;
	}

	public void setCoCode(String coCode) {
		this.coCode = coCode;
	}

	 */
	
	/**
	 * Sets the connector configuration file and location which is used to store the connection setting,
	 * and to define path to the folder where input and output schemas will be stored when metadata
	 * discovery is used for Service Xml request. 
	 */
	@Configurable
	@Path
	@Required
	@Default(DEFAULT_SETTINGS_FILE_NAME)
	@Placement(order = 1, group = "Save Connection Setting", tab = "General")
	@FriendlyName("Settings File Location")
	private String settingsFilePath;

	public String getSettingsFilePath() {
		return settingsFilePath;
	}

	public void setSettingsFilePath(String settingsFilePath) {
		this.settingsFilePath = settingsFilePath;
	}

		
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * Test Web Service connection with T24 server
	 * 
	 * @param serviceUrl Web Service URL (Example: http://localhost:9089/axis2/services)
	 * 
	 * @throws ConnectionException
	 */
	@TestConnectivity(label="Test and Save Settings")
	public void testConnect(@ConnectionKey @Default("http://localhost:9089/axis2/services") String serviceUrl
			) throws ConnectionException{

		if(!verifyUserInput(serviceUserName,  serviceUrl, settingsFilePath)){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Incorrect input parameters!");
		}
		
		UserCredentialsInterface ui = new UserCredentialsInterface();
		//ui.setInputFields("T24 password:", "Service password:").showUserDialog("Set credentials");

		ui.setServicePassword(servicePassword);
		ui.setT24password(t24Password);
		
		splitFullFileName();
		
		if(serviceUrl.trim().length() == 0) {
			serviceUrl = WS_HOST;
		}

		setServiseURL(serviceUrl+WS_NAME);

		initIntegrationServiceFlow(serviceUrl+WS_NAME);
		
		//setWsConnectorUser(username, ui.getServicePassword(), coCode); 
		setWsConnectorUser(serviceUserName, servicePassword); 
					
		if(!isConnected()){ 
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"112", "Can't connect to service!");
		}			

		ResponseDetails responseDetails = client.getAllFlowNames(userWsDeatils).getResponseDetails().getValue();
		String testSOAP  = responseDetails.getReturnCode().getValue();
		if(testSOAP==null || testSOAP.isEmpty()){
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"113", "Can't work with service!");
		}
		
		if("FAILURE".equals(testSOAP)) {
			String result = "";
			for (Response resp : responseDetails.getResponses()) {
				result += resp.getResponseCode().getValue() + " ";
			} 
			
			throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS,"114", "Failure in web service call with code: "+result);
		}
		
		
		// remove configuration store in properties file

//		try{
//			ui.saveEncryptedFile(this);
//		}catch(RuntimeException exception){
//			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Cannot save file! File name: " + settingsFilePath + " Exception: "+ exception.getMessage());
//		}

		setConnectorMode(ConnectorOperationMode.DESIGN_TIME);
		
/*		
		// test exception - the only way to output debug information design time
		String result = "";
		for (Response resp : response.getResponses()) {
			result += resp.getResponseCode().getValue() + " ";
		}

		throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS,"115", "WS call with user: "+username
				+" password: "+password+" status returned: "+testSOAP+ " response codes: "+result);

*/
	}
	

	// flag for single loading of configuration from property file
	private boolean loadedFromFile = false;
	
	@Connect
	public void connect(@ConnectionKey @Default("http://localhost:9089/axis2/services") String serviceUrl
			) throws ConnectionException{
		
		if(loadedFromFile) { // single loading of configuration
			return;
		}
		
		////Modified////
		splitFullFileName();

		// remove configuration from properties loading

//		loadConfigurationFromProperties();
		loadedFromFile = false;
		
		
		if(serviceUrl.trim().length() == 0) {
			serviceUrl = WS_HOST;
		}
		setServiseURL(serviceUrl+WS_NAME);

//		if(!verifyUserInput(username, serviceUrl, emptyCredentialFile, coCode)){
//			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Incorrect input parameters!");
//		}

		//setWsConnectorUser(username, ui.getServicePassword(), coCode); 
		setWsConnectorUser(serviceUserName, servicePassword); 		
		
		/////HERE IS THE PLACE FOR T24
		//setT24password( ui.getT24password());

		setConnectorMode(ConnectorOperationMode.RUN_TIME);
	}
	
    /**
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
       client = null;
       loadedFromFile = false;
    }
    
    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {
    	return client != null;
    }
    
    /**
     * Connection id
     */
    @ConnectionIdentifier
    public String connectionId() {
        return "001";
    }
    
	private boolean verifyUserInput(String username,  /*String password,*/  String serviceUrl, String folder) {
		if (StringUtils.isBlank(username) /*|| StringUtils.isBlank(password)*/){
			return false;
	    }			
		
		if(StringUtils.isBlank(serviceUrl) || !AddressChecker.isValidURL(serviceUrl)){
			return false;
		}
		
//		if(port<1000 || port >65535){
//			return false;
//		}			
//
//		if(StringUtils.isBlank(agentHost) || !AddressChecker.checkHostIp(agentHost)){
//			return false; 
//		}			
			
		if(StringUtils.isBlank(folder)){
			return false;
		}
		return true;
	}
    
    /**
     * Creates SOAP client for the given WSDL file
     * @param serviceUrl - the SOAP service WSDL location
     * */
    public void initIntegrationServiceFlow(String serviceUrl) throws ConnectionException{
		IntegrationFlowServiceWSClient proxyClient;
		proxyClient = new IntegrationFlowServiceWSClient(serviceUrl);
		proxyClient.clientConnect(ServiceEndPointType.HTTPSOAP12); 
		setClient(proxyClient);
    }
    
    /**
     * Set T24UserDetails
     * @param username - SOAP client user name
     * @param password - SOAP client password
     * @param coCode  - company code
     */
    private void setWsConnectorUser(String username, String password){
    	ObjectFactory jaxbFactory = new ObjectFactory();
		userWsDeatils = new T24UserDetails();
		userWsDeatils.setPassword(jaxbFactory.createT24UserDetailsPassword(password));
		userWsDeatils.setUser(jaxbFactory.createT24UserDetailsUser(username));
    }
    
	private void splitFullFileName() {
		// fill parsed settings file name and directory here
        File file = new File(settingsFilePath);
        
        // check for existing file and create if missing to fix schema loading
        if ( ! file.exists() ) {
            try {
    			file.createNewFile();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    		}
        }
        
        if (file.isFile()) {
        	setSettingsFolder(file.getParent());
        	setSettingsFileName(file.getName());
        }
	}
	
	@SuppressWarnings("unused")
	private void loadConfigurationFromProperties() {
		UserCredentialsInterface ui = new UserCredentialsInterface();
		Properties allProperties = ui.resolveCredentialFileAndPath(settingsFilePath); 
		
		if(null != allProperties && allProperties.contains(UserCredentialsInterface.T24_HOST)) { // Extract values from file
			
			log.info("Load configuration from file: "+settingsFilePath);
			
			String t24Host = allProperties.getProperty(UserCredentialsInterface.T24_HOST);
			setT24Host(t24Host);
			
			String t24Port = allProperties.getProperty(UserCredentialsInterface.T24_PORT);
			setT24Port(Integer.valueOf(t24Port));

			String t24serverType = allProperties.getProperty(UserCredentialsInterface.T24_SERVER_TYPE);
			this.setT24RunTime(RuntimeConfigServerSelector.valueOf(t24serverType));
			
			String t24runtime = allProperties.getProperty(UserCredentialsInterface.T24_RUNTIME);
			RuntimeConfigSelector agentType = RuntimeConfigSelector.valueOf(t24runtime);
			
			String t24User = allProperties.getProperty(UserCredentialsInterface.T24_USER);
			setT24User(t24User);
			
			String folder = allProperties.getProperty(UserCredentialsInterface.FOLDER);
			setSettingsFolder(folder);
			
			String nodeNames  = allProperties.getProperty(UserCredentialsInterface.T24_NODE_NAMES);
			setNodeName(nodeNames);
			
			String t24password = allProperties.getProperty(UserCredentialsInterface.T24_PASS);
			setT24Password(t24password);
			

			String stateful = allProperties.getProperty(UserCredentialsInterface.T24_EJB_STATEFUL);
			setEjbStateful(Boolean.valueOf(stateful));

			String ejbName = allProperties.getProperty(UserCredentialsInterface.T24_EJB_NAME);
			if(null == ejbName) {
				ejbName = "";
			}
			setEjbName(ejbName);

		}
	}
	
}