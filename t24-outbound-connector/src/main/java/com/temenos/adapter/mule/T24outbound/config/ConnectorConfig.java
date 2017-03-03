package com.temenos.adapter.mule.T24outbound.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
//import org.apache.cxf.common.util.ReflectionInvokationHandler.Optional;
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
import org.mule.api.annotations.param.ConnectionKey;

import org.mule.api.annotations.param.Default;
import com.temenos.adapter.mule.T24outbound.definition.ObjectFactory;
import com.temenos.adapter.mule.T24outbound.definition.T24UserDetails;
import com.temenos.adapter.mule.T24outbound.definition.Response;
import com.temenos.adapter.mule.T24outbound.definition.ResponseDetails;

//import com.temenos.adapter.common.runtime.outbound.RequestType;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;
import com.temenos.adapter.mule.T24outbound.proxy.ServiceEndPointType;
//import com.temenos.adapter.mule.T24outbound.rmi.ResponseRecord;
import com.temenos.adapter.mule.T24outbound.utils.AddressChecker;

import com.temenos.adapter.mule.T24outbound.utils.UserCredentialsInterface;


/**
 * This class is used to configure the connector connection properties, both for
 * run-time and design-time modes.
 */

@ConnectionManagement(configElementName = "config", friendlyName = "Connection Managament type strategy")
public class ConnectorConfig {

	public static final String DEFAULT_SETTINGS_FILE_NAME = "settings.txt"; 
	
	/**
	 * Holding the SOAP User credentials
	 */
	private T24UserDetails userWsDeatils;

	public T24UserDetails getUserWsDeatils() {
		return userWsDeatils;
	}

	public void setUserWsDeatils(T24UserDetails userWsDeatils) {
		this.userWsDeatils = userWsDeatils;
	}

	/**
	 * The SOAP client
	 */
	private IntegrationLandscapeServiceWSclient client;

	public IntegrationLandscapeServiceWSclient getClient() {
		return client;
	}

	public void setClient(IntegrationLandscapeServiceWSclient client) {
		this.client = client;
	}

	/**
	 * The T24 Agent runtime connection holder
	 */
	private T24OutboundConfig outBoundConnection;

	public T24OutboundConfig getOutBoundConnection() {
		return outBoundConnection;
	}

	public void setOutBoundConnection(T24OutboundConfig outBoundConnection) {
		this.outBoundConnection = outBoundConnection;
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
	 * ConnectorConfig operational mode: either DESIGN_TIME or RUN_TIME it is
	 * internal not an UI element, just to mark the current operation mode
	 * because connector use this class both in design-time and run-time
	 */
	private ConnectorOperationMode connectorMode;

	public ConnectorOperationMode getConnectorMode() {
		return connectorMode;
	}

	public void setConnectorMode(ConnectorOperationMode connectorMode) {
		this.connectorMode = connectorMode;
	}
	
	
	
	
	
	
	

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
////////////////////////////T24 CONNECTION CONFIGURATION FIELDS///////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private RuntimeConfigSelector t24RunTime = RuntimeConfigSelector.TAFJ;

	public RuntimeConfigSelector getT24RunTime() {
		return t24RunTime;
	}

	public void setT24RunTime(RuntimeConfigSelector t24RunTime) {
		this.t24RunTime = t24RunTime;
	}
	
	

	/**
	 * Defines T24 Server type
	 */
	@Configurable
	@FriendlyName("T24 Server Type")
	@Placement(order = 1, group = "Connection Setting", tab = "Runtime")
	@Default(value = "JBOSS_7_2") // 
	private ServerType t24ServerType;

	public ServerType getT24ServerType() {
		return t24ServerType;
	}

	public void setT24ServerType(ServerType t24ServerType) {
		this.t24ServerType = t24ServerType;
	}
	
	
	/**
	 * T24 Runtime host address
	 */
	@Configurable
	@Default(value = "localhost")
	@Placement(order = 2, group = "Connection Setting", tab = "Runtime")
	@FriendlyName("T24 Host")
	private String t24Host;

	public String getT24Host() {
		return t24Host;
	}

	public void setT24Host(String t24Host) {
		this.t24Host = t24Host;
	}
		

	/**
	 * T24 Runtime port
	 */
	@Configurable
	@Default(value="4447")
	@Placement(order = 3, group = "Connection Setting", tab = "Runtime")
	@FriendlyName("T24 Port")
	private Integer t24Port;

	public Integer getT24Port() {
		return t24Port;
	}

	public void setT24Port(Integer t24Port) {
		this.t24Port = t24Port;
	}
	
	
	/**
	 * T24 credentials for User name
	 */
	@Configurable
	@Default(value="SSOUSER1")
	@Required
	@Placement(order = 4, group = "Connection Setting", tab = "Runtime")
	@FriendlyName("T24 User")
	private String t24User;

	public String getT24User() {
		return t24User;
	}

	public void setT24User(String t24User) {
		this.t24User = t24User;
	}
	
	
	/**
	 * T24 credentials for User password	 
	 */
	@Configurable
	@Default(value="123456")
	@Required
	@Password
	@Placement(order = 5, group = "Connection Setting", tab = "Runtime")
	@FriendlyName("T24 Password")
	private String t24Password;
	
	public String getT24Password() {
		return t24Password;
	}

	public void setT24Password(String t24Password) {
		this.t24Password = t24Password;
	}

		
	/**
	 * Defines a list of comma separate node name values
	 */	
	@Configurable
	@FriendlyName("T24 Node Names")
	@Placement(order = 6, group = "Connection Setting", tab = "Runtime")
	@Default(value = "node1")
	private String t24NodeNmes;	
	
	public String getT24NodeNmes() {
		return t24NodeNmes;
	}

	public void setT24NodeNmes(String t24NodeNmes) {
		this.t24NodeNmes = t24NodeNmes;
	}
	

	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////T24 CONNECTION CONFIGURATION FIELDS///////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	private static final String INTGRATION_LANDSCAPE_SERVICE_WSDL = "IntegrationLandscapeServiceWS?wsdl";
	
	/**
	 * Web service credentials User name
	 */
	@Configurable
	@Default(value="INPUTT")
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
	@Default(value="123456")
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
	 * Sets the connector configuration file and location which is used to store the connection setting,
	 * and to define path to the folder where input and output schemas will be stored when metadata
	 * discovery is used for Service Xml request. 
	 */
	@Configurable
	@Default(value = DEFAULT_SETTINGS_FILE_NAME)
	@Path
	@Required
	@Placement(order = 1, group = "Save Connection Setting", tab = "General")
	@FriendlyName("Settings File Location")
	private String settingsFilePath;

	public String getSettingsFilePath() {
		return settingsFilePath;
	}

	public void setSettingsFilePath(String settingsFile) {
		this.settingsFilePath = settingsFile;
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * This method is used by the connector at design-time test connectivity
	 * @param serviceUrl - Web service URL (example: http://localhost:9089/axis2/services/)
	 * @throws ConnectionException
	 */
	@TestConnectivity(label="Test and Save Settings")
	public void testConnect(@ConnectionKey @Default("http://localhost:9089/axis2/services/") String serviceUrl) throws ConnectionException {

		serviceUrl += INTGRATION_LANDSCAPE_SERVICE_WSDL;

		String check = verifyConfigurationInputs(true, serviceUserName, servicePassword, serviceUrl, settingsFilePath, t24User, t24Password);
		
		if (!check.isEmpty() && !check.equals("Alternate resourse")) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", "Incorrect input parameters! " + check);
		}

		UserCredentialsInterface ui = new UserCredentialsInterface();
		ui.setServicePassword(servicePassword);
		ui.setT24password(t24Password);
		
		initIntegrationServiceLandscape(serviceUrl);

		setWsConnectorUser(serviceUserName, servicePassword); 
				

		if (!isConnected()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "112", "Can't connect to web service!");
		}

		ResponseDetails responseDetails = client.getVersions(userWsDeatils).getResponseDetails().getValue();
		String testSOAP = responseDetails.getReturnCode().getValue();
		if (testSOAP == null || testSOAP.isEmpty()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "113", "Service not available!");
		}

		if("FAILURE".equals(testSOAP)) {
			String result = "";
			for (Response resp : responseDetails.getResponses()) {
				result += resp.getResponseCode().getValue() + " ";
			} 
			
			throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS,"114", "Failure in web service call with code: "+result);
		}		
		
		setServiseURL(serviceUrl);
		
		splitFullFileName();

		// remove configuration store in properties file
		
//		try {
//			ui.saveEncryptedFile(this);
//		} catch (RuntimeException exception) {
//			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", "Cannot save connection properties file!" + exception.getMessage());
//		}
		
		setConnectorMode(ConnectorOperationMode.DESIGN_TIME);
		
		/*
		// test exception - the only way to output debug information design time
		String result = "";
		for (Response resp : responseDetails.getResponses()) {
			result += resp.getResponseCode().getValue() + " ";
		}

		throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS,"115", "WS call with user: "
				+serviceUserName
				+" password: "+servicePassword+" status returned: "+testSOAP+ " response codes: "+result);

		 */
	}
	
	/**
	 * These connector method will be used only at runtime. It should have the
	 * same parameters as TestConfig method otherwise it would not compile
	 * (Because the class is annotated with @ConnectionManagement)
	 * @param serviceUrl - java.lang.String
	 * @throws ConnectionException
	 */
	@Connect
	public void connect(@ConnectionKey @Default("http://localhost:9089/axis2/services/") String serviceUrl) throws ConnectionException {

		serviceUrl += INTGRATION_LANDSCAPE_SERVICE_WSDL;
		setServiseURL(serviceUrl);
		
		outBoundConnection = T24OutboundConfig.getInstance();
		if(outBoundConnection.isConfigured()) {
			return;
		}
		
		splitFullFileName();

		// remove configuration from properties loading

//		loadConfigurationFromProperties();

		String check = verifyConfigurationInputs(false, serviceUserName, servicePassword, serviceUrl, settingsFilePath, t24User, t24Password);
		
		/* OK Let's suppose that user inputs are injected from flow XML...If it is the case, they are probably not null */		

		if (!check.isEmpty()) {	
			// we have errors in check parameters

			throw new RuntimeException("Bad runtime configuration: "+check);
		}
		
		setWsConnectorUser(serviceUserName, servicePassword);
		
		outBoundConnection.setRuntime(RuntimeConfigSelector.getRunTimeSelector(t24RunTime), t24Host, t24Port, t24User, t24Password, t24ServerType, t24NodeNmes);

		outBoundConnection.setConfigured(true);
		setConnectorMode(ConnectorOperationMode.RUN_TIME);

	}

	private String verifyConfigurationInputs(boolean isDesignTime, String serviceUserName, String servicePassword, String serviceUrl, String settingsFilePath, String t24User, String t24Password) {

		// no need to check service parameters runtime
		
		if(isDesignTime) {
			if (serviceUserName == null ||StringUtils.isBlank(serviceUserName)) {

				return "Service username is empty";
			}
			
			if (servicePassword == null ||StringUtils.isBlank(servicePassword)) {
			
				return "Service password is empty";
			}
		}
		
		if (serviceUrl== null || StringUtils.isBlank(serviceUrl) || !AddressChecker.isValidURL(serviceUrl)) {

			return "Service URL is empty";
		}

		if (StringUtils.isBlank(t24Host) || !AddressChecker.checkHostIp(t24Host)) {

			return "T24 Host is empty";
		}
		if (t24User== null ||StringUtils.isBlank(t24User)) {

			return "T24 User is empty";
		}
		
		if (t24Password== null ||StringUtils.isBlank(t24Password)) {

			return "T24 Password is empty";
		}
		
		
		if (settingsFilePath == null || StringUtils.isBlank(settingsFilePath)) {
			return "Settings file path is empty";
		}
		
//		boolean success = false;
//		try{
//			java.nio.file.Path p =  Paths.get(settingsFilePath);
//			settingsFileName = p.getFileName().toString();
//			settingsFolder = p.getParent().toString();
//			success = true;
//		}catch(InvalidPathException e){
//			
//		}catch(Exception e){
//			
//		}finally{
//		
//			if(!success){
//				if(settingsFileName==null ||StringUtils.isBlank(settingsFileName)){
//					settingsFileName = DEFAULT_SETTINGS_FILE_NAME;
//				}
//				if(settingsFolder == null || StringUtils.isBlank(settingsFolder)){
//					settingsFolder ="";
//				}
//				return "Alternate resourse";
//			}
//		}

		return "";
	}

	/**
	 * Disconnect
	 */
	@Disconnect
	public void disconnect() {
		client = null;
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

	/**
	 * Creates SOAP client for the given WSDL file
	 * 
	 * @param serviceUrl
	 *            - the SOAP service WSDL location
	 */
	public void initIntegrationServiceLandscape(String serviceUrl) throws ConnectionException {
		IntegrationLandscapeServiceWSclient proxyClient;
		proxyClient = new IntegrationLandscapeServiceWSclient(serviceUrl);
		proxyClient.clientConnect(ServiceEndPointType.HTTPSOAP12);
		setClient(proxyClient);
	}

	/**
	 * Set T24UserDetails
	 * 
	 * @param username
	 *            - SOAP client user name
	 * @param password
	 *            - SOAP client password
	 */
	private void setWsConnectorUser(String username, String password) {
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
	
	private void loadConfigurationFromProperties() {
		
		// try loading from file
		UserCredentialsInterface ui = new UserCredentialsInterface();
		Properties allProperties = ui.resolveCredentialFileAndPath(this);
		
		if(null != allProperties && null != allProperties.getProperty(UserCredentialsInterface.T24_HOST)) {
			
			String t24Host = allProperties.getProperty(UserCredentialsInterface.T24_HOST);
			setT24Host(t24Host);
			
			String t24Port = allProperties.getProperty(UserCredentialsInterface.T24_PORT);
			setT24Port(Integer.valueOf(t24Port));

			String t24serverType = allProperties.getProperty(UserCredentialsInterface.T24_SERVER_TYPE);

			ServerType server_type = ServerType.resolve(t24serverType);
			setT24ServerType(server_type);
			
			String t24User = allProperties.getProperty(UserCredentialsInterface.T24_USER);
			setT24User(t24User);
			
			String folder = allProperties.getProperty(UserCredentialsInterface.FOLDER);
			setSettingsFolder(folder);
			
			String nodeNames  = allProperties.getProperty(UserCredentialsInterface.T24_NODE_NAMES);
			setT24NodeNmes(nodeNames);
			
			String t24password = allProperties.getProperty(UserCredentialsInterface.T24_PASS);
			setT24Password(t24password);
			
			// last two seems redundant
			String servicepassword = allProperties.getProperty(UserCredentialsInterface.SERVICE_PASS);
			setServicePassword(servicepassword);
			
			String serviceusername = allProperties.getProperty(UserCredentialsInterface.SERVICE_USER);
			setServiceUserName(serviceusername);
			
		}
		
	}

}