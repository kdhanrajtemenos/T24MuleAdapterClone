package com.temenos.adapter.mule.T24outbound.config;

import java.io.File;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.ReflectionInvokationHandler.Optional;
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
//import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;

import org.mule.api.annotations.param.Default;
import com.temenos.adapter.mule.T24outbound.definition.ObjectFactory;
import com.temenos.adapter.mule.T24outbound.definition.T24UserDetails;


import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;
import com.temenos.adapter.mule.T24outbound.proxy.ServiceEndPointType;

import com.temenos.adapter.mule.T24outbound.utils.AddressChecker;
//import com.temenos.adapter.mule.T24outbound.utils.IoResourseUtil;
import com.temenos.adapter.mule.T24outbound.utils.UserCrededntialsInterface;


/**
 * This class is used to configure the connector connection properties, both for
 * run-time and design-time modes.
 */

@ConnectionManagement(configElementName = "config", friendlyName = "Connection Managament type strategy")
public class ConnectorConfig {

	
	private static final String SERVICE_URL_LOCATION = "IntegrationLandscapeServiceWS?wsdl";
	
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

	/////////////////////// THIS WILL BE
	/////////////////////// COMMENTED////////////////////////////////
	/////////////////////// SECTION T24 Runtime
	/////////////////////// Configuration////////////////////////////////
	/**
	 * Sets drop down list in the design-time connector configuration window for
	 * selecting the T24 Runtime (either TAFJ or TAFC)
	 */
	/*
	 * @Configurable
	 * 
	 * @Default(value = "TAFJ")
	 * 
	 * @Placement(order = 1, group = "T24 Runtime Configuration", tab =
	 * "Runtime Configuration")
	 * 
	 * @FriendlyName("T24 Runtime Type")
	 */

	private RuntimeConfigSelector t24RunTime = RuntimeConfigSelector.TAFJ;

	public RuntimeConfigSelector getT24RunTime() {
		return t24RunTime;
	}

	public void setT24RunTime(RuntimeConfigSelector t24RunTime) {
		this.t24RunTime = t24RunTime;
	}
	////////////////////////////////////////////////////////////////////////////

	/////////////////////// T24 Runtime Port////////////////////////////////
	/**
	 * Sets a numeric filed in the design-time connector configuration window
	 * for inputting the T24 Runtime port
	 */
	@Configurable
	@Required
	@Default(value = "4447")
	@Placement(order = 2, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@FriendlyName("T24 Port")
	private Integer port;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	/////////////////////// T24 Runtime Host////////////////////////////////
	/**
	 * Sets a text filed in the design-time connector configuration window for
	 * inputting the T24 Runtime host address
	 */
	@Configurable
	@Default(value = "localhost")
	@Placement(order = 3, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@FriendlyName("T24 Host")
	private String agentHost;

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}


	/**
	 * Sets a text filed in the design-time connector configuration window for
	 * inputting the T24 Runtime user name
	 */
	@Configurable
	@Default(value = "SSOUSER1")
	@Placement(order = 4, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@FriendlyName("T24 User")
	private String agentUser;

	public String getAgentUser() {
		return agentUser;
	}

	public void setAgentUser(String agentUser) {
		this.agentUser = agentUser;
	}

	
	/**
	 * Defines T24 Server type
	 */
	@Configurable
	@FriendlyName("T24 Server Type")
	@Placement(order = 5, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@Default(value = "JBOSS_7_2")
	private ServerType serverType;

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}


	
	@Configurable
	@FriendlyName("Jboss node names")
	@Placement(order = 6, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@Default(value = "node1")
	private String nodeNames;
	
	public String getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(String nodeNames) {
		this.nodeNames = nodeNames;
	}


	/*
	 * @Configurable
	 * 
	 * @Password
	 * 
	 * @Placement(order = 5, group = "Agent Runtime Configuration", tab =
	 * "General")
	 * 
	 * @FriendlyName("Agent Password") private String agentPass;
	 * 
	 * public String getAgentPass() { return agentPass; }
	 * 
	 * public void setAgentPass(String agentPass) { this.agentPass = agentPass;
	 * }
	 */
	private String folder;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
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
	
	
	@Configurable
	@Default(value = "123456")
	@Password
	@Required
	@Placement(order = 5, group = "Connection", tab = "General")
	@FriendlyName("Password")
	public String servicePassword;
	

	public String getServicePassword() {
		return servicePassword;
	}

	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}
	
	@Configurable
	@Password
	@Required
	@Default(value = "123456")
	@Placement(order = 7, group = "T24 Runtime Configuration", tab = "Runtime Configuration")
	@FriendlyName("Password")
	public String t24Password;

	public String getT24Password() {
		return t24Password;
	}

	public void setT24Password(String t24Password) {
		this.t24Password = t24Password;
	}

	/**
	 * This method is used by the connector at design-time test connectivity
	 * 
	 * @param username
	 * @param password
	 * @param serviceUrl
	 * @param coCode
	 * @param emptyCredentialFile
	 * @throws ConnectionException
	 * 
	 *             There are two cases modes for T24 run-time: 1. TAFJ -
	 *             Credentials are needed only for the EJB client 2. TAFC - It
	 *             will be implemented in Future
	 */

	@TestConnectivity
	public void testConnect(@ConnectionKey String username,
			@Optional @Default("http://localhost:9089/axis2/services/") String serviceUrl,
			@Path @Optional @Default("D:\\Schemas\\settings.properties") String emptyCredentialFile) throws ConnectionException {

		serviceUrl += SERVICE_URL_LOCATION;

		if (!verifyUserInput(username, serviceUrl, emptyCredentialFile)) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", "Incorrect input parameters!");
		}



		UserCrededntialsInterface ui = new UserCrededntialsInterface();
	
		ui.setServicePassword(servicePassword);
		
		ui.setT24password(t24Password);
		//ui.setInputFields("T24 password:", "Service password:").showUserDialog("Set credentials");


		setServiseURL(serviceUrl);

		initIntegrationServiceLandscape(serviceUrl);

		setWsConnectorUser(username, ui.getServicePassword()); // setWsConnectorUser(username,
																// password,
																// coCode);

		if (!isConnected()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "112", "Can't connect to web service!");
		}

		String testSOAP = client.getVersions(userWsDeatils).getVersionNames().toString();
		if (testSOAP == null || testSOAP.isEmpty()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "113", "Service not available!");
		}

		try {
			String dir = ui.saveEncryptedFile(emptyCredentialFile, this);
			setFolder(dir);
		} catch (RuntimeException exception) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111","Cannot save connection properties file!" + exception.getMessage());
		}
		setConnectorMode(ConnectorOperationMode.DESIGN_TIME);
	}

	/**
	 * These connector method will be used only at runtime. It should have the
	 * same parameters as TestConfig method otherwise it would not compile
	 * (Because the class is annotated with @ConnectionManagement)
	 * 
	 * @param username
	 * @param password
	 * @param serviceUrl
	 * @param coCode
	 * @param emptyCredentialFile
	 * @throws ConnectionException
	 */
	@Connect
	public void connect(@ConnectionKey String username,
			@Optional @Default("http://localhost:9089/axis2/services/") String serviceUrl,
			@Path @Optional @Default("D:\\Schemas\\settings.properties") String emptyCredentialFile) throws ConnectionException {

		serviceUrl += SERVICE_URL_LOCATION;
	
		
		if (emptyCredentialFile == null || emptyCredentialFile.isEmpty() /*|| !IoResourseUtil.isFile(emptyCredentialFile)*/) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", "Incorrect Credential File parameters!");
		}

		UserCrededntialsInterface ui = new UserCrededntialsInterface();
		Properties allProperties = ui.resolveCredentialFileAndPath(emptyCredentialFile); // This
		
		outBoundConnection = T24OutboundConfig.getInstance();
		
		if(allProperties != null){
			
			// returns
			String directory = allProperties.getProperty(UserCrededntialsInterface.FOLDER);														
			
			String t24Host = allProperties.getProperty(UserCrededntialsInterface.T24_HOST);
			
			String t24Port = allProperties.getProperty(UserCrededntialsInterface.T24_PORT);
			
			String t24serverType = 	allProperties.getProperty(UserCrededntialsInterface.T24_SERVER_TYPE);
			
			ServerType server_type = ServerType.resolve(t24serverType);
			
			String t24User = allProperties.getProperty(UserCrededntialsInterface.T24_USER);
			
			String nodeNames = allProperties.getProperty(UserCrededntialsInterface.T24_NODE_NAMES);
	
			
	
			outBoundConnection.setRuntime(RuntimeConfigSelector.getRunTimeSelector(t24RunTime), t24Host, Integer.parseInt(t24Port), t24User, ui.getT24password(), server_type, nodeNames);
	
			setFolder(directory);
	
			setServiseURL(serviceUrl);
			
			setAgentHost(t24Host);
			
			setServerType(server_type);
			
			setAgentUser(t24User);
			
			setNodeNames(nodeNames);
			
			setPort(Integer.valueOf(t24Port));
			
			setWsConnectorUser(username, ui.getServicePassword());
			
		}else{
			String folder = null;
			emptyCredentialFile = emptyCredentialFile.replace("/", File.separator);
			int filePathSeparratorIdx = emptyCredentialFile.lastIndexOf(File.separator);
			if(filePathSeparratorIdx != -1 && filePathSeparratorIdx < emptyCredentialFile.length()){
				folder = emptyCredentialFile.substring(0,filePathSeparratorIdx );
				setFolder(folder);
			}
			
			outBoundConnection.setRuntime(RuntimeConfigSelector.getRunTimeSelector(t24RunTime), agentHost, port, agentUser, t24Password, serverType, nodeNames);
			setWsConnectorUser(username, servicePassword);
		}

		

		setConnectorMode(ConnectorOperationMode.RUN_TIME);

	}

	private boolean verifyUserInput(String username, /* String password, */ String serviceUrl, String folder) {
		if (StringUtils.isBlank(username) /* || StringUtils.isBlank(password) */) {
			return false;
		}
		if (StringUtils.isBlank(serviceUrl) || !AddressChecker.isValidURL(serviceUrl)) {
			return false;
		}
		if (port < 1000 || port > 65535) {
			return false;
		}
		if (StringUtils.isBlank(agentHost) || !AddressChecker.checkHostIp(agentHost)) {
			return false;
		}
		if (StringUtils.isBlank(agentUser)) {
			return false;
		}
		if (StringUtils.isBlank(folder)) {
			return false;
		}

		return true;
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
	 * @param coCode
	 *            - company code
	 */
	private void setWsConnectorUser(String username, String password) {
		ObjectFactory jaxbFactory = new ObjectFactory();
		userWsDeatils = new T24UserDetails();
		/*
		 * userWsDeatils.setCoCode(jaxbFactory.createT24UserDetailsCoCode(coCode
		 * ));
		 */
		userWsDeatils.setPassword(jaxbFactory.createT24UserDetailsPassword(password));
		userWsDeatils.setUser(jaxbFactory.createT24UserDetailsUser(username));
	}

}