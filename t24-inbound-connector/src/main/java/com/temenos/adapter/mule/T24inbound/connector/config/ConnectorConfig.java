package com.temenos.adapter.mule.T24inbound.connector.config;

//import javax.transaction.TransactionManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.ReflectionInvokationHandler.Optional;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.modules.t24inbound.definition.ObjectFactory;
import org.mule.modules.t24inbound.definition.T24UserDetails;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorOperationMode;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigServerSelector;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;
import com.temenos.adapter.mule.T24inbound.connector.proxy.ServiceEndPointType;
import com.temenos.adapter.mule.T24inbound.connector.utils.AddressChecker;
import com.temenos.adapter.mule.T24inbound.connector.utils.UserCrededntialsInterface;


@ConnectionManagement(configElementName = "config", friendlyName = "Connection type parameters")
public class ConnectorConfig {
	
	private final String WS_NAME = "/IntegrationFlowServiceWS?wsdl";
	private final String WS_HOST = "http://localhost:9089/axis2/services";
	

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
	@Placement(order = 2, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 port")
	private Integer port;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	/** 
	 * Sets a text filed in the design-time connector configuration window
	 * for inputting the T24 Runtime host address
	 * */
	@Configurable
	@Default(value="localhost")
	@Placement(order = 3, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 host")
	private String agentHost;
	
	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}
	
	/** 
	 * Sets a text filed in the design-time connector configuration window
	 * for inputting the T24 Runtime user name
	 * */
	@Configurable
	@Default(value = "SSOUSER1")
	@Placement(order = 4, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 username")
	private String agentUser;
	
	public String getAgentUser() {
		return agentUser;
	}

	public void setAgentUser(String agentUser) {
		this.agentUser = agentUser;
	}
	
	/** 
	 * Sets drop down list in the design-time connector configuration window
	 * for selecting the T24 Runtime (either TAFJ or TAFC)
	 * */
	@Configurable
	@Default(value = "JBOSS72")
	@Placement(order = 1, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 server type")
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
    @FriendlyName("T24 jboss node")
    @Placement(order = 5, group = "Connector Runtime Configuration", tab = "Runtime")
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
    @FriendlyName("T24 ejb stateful")
    @Placement(order = 6, group = "Connector Runtime Configuration", tab = "Runtime")
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
    @FriendlyName("T24 ejb bean name")
    @Placement(order = 7, group = "Connector Runtime Configuration", tab = "Runtime")
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
	
	
	private String t24password;
	
	public String getT24password() {
		return t24password;
	}

	public void setT24password(String t24password) {
		this.t24password = t24password;
	}
	

	@TestConnectivity(label="WebService test")
	public void testConnect(@ConnectionKey String username, @Optional @Default("http://localhost:9089/axis2/services") String serviceUrl, @Optional @Default("GB0010001") String coCode, @Path @Optional @Default("D:/Schemas") String folder) throws ConnectionException{

		if(!verifyUserInput(username,  serviceUrl, folder, coCode)){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Incorrect input parameters!");
		}
		
		UserCrededntialsInterface ui = new UserCrededntialsInterface();
		ui.setInputFields("T24 password:", "Service password:").showUserDialog("Set credentials");
					
		//setFolder(folder); 
		
		if(serviceUrl.trim().length() == 0) {
			serviceUrl = WS_HOST;
		}

		setServiseURL(serviceUrl+WS_NAME);

		initIntegrationServiceFlow(serviceUrl+WS_NAME);
		
		setWsConnectorUser(username, ui.getServicePassword(), coCode); //setWsConnectorUser(username, password, coCode); 
					
		if(!isConnected()){ 
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"112", "Can't connect to service!");
		}			

		String testSOAP  = client.getAllFlowNames(userWsDeatils).getResponseDetails().getValue().getReturnCode().getValue();
		if(testSOAP==null || testSOAP.isEmpty()){
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"113", "Can't work with service!");
		}
		

		setT24password( ui.getT24password());

		try{
			String dir = ui.saveEncryptedFile(folder);
			setFolder(dir); 
		}catch(RuntimeException exception){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Cannot save file!" + exception.getMessage());
		}
		setConnectorMode(ConnectorOperationMode.DESIGN_TIME);

	}
	
	
	@Connect
	public void connect(@ConnectionKey String username, @Optional @Default("http://localhost:9089/axis2/services") String serviceUrl, @Optional @Default("GB0010001") String coCode, @Path @Optional @Default("D:/Schemas") String folder) throws ConnectionException{
		if(!verifyUserInput(username, serviceUrl, folder, coCode)){
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111","Incorrect input parameters!");
		}
		
		////Modified////
		UserCrededntialsInterface ui = new UserCrededntialsInterface();
		String directory = ui.resolveCredentialFileAndPath(folder); //This returns the directory
		
		
		setFolder(directory); // setFolder(folder);
		
		if(serviceUrl.trim().length() == 0) {
			serviceUrl = WS_HOST;
		}
		setServiseURL(serviceUrl+WS_NAME);

		setWsConnectorUser(username, ui.getServicePassword(), coCode);  //setWsConnectorUser(username, password, coCode);
		
		
		/////HERE IS THE PLACE FOR T24
		setT24password( ui.getT24password());

		setConnectorMode(ConnectorOperationMode.RUN_TIME);
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
    
	private boolean verifyUserInput(String username,  /*String password,*/  String serviceUrl, String coCode,  String folder) {
		if (StringUtils.isBlank(username) /*|| StringUtils.isBlank(password)*/){
			return false;
	    }			
		if(StringUtils.isBlank(serviceUrl) || !AddressChecker.isValidURL(serviceUrl)){
			return false;
		}
		if(port<1000 || port >65535){
			return false;
		}			
		if(StringUtils.isBlank(agentHost) || !AddressChecker.checkHostIp(agentHost)){
			return false; 
		}			
		if(StringUtils.isBlank(agentUser) ){
			return false;
		}
			
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
    private void setWsConnectorUser(String username, String password, String coCode){
    	ObjectFactory jaxbFactory = new ObjectFactory();
		userWsDeatils = new T24UserDetails();
		userWsDeatils.setCoCode(jaxbFactory.createT24UserDetailsCoCode(coCode));
		userWsDeatils.setPassword(jaxbFactory.createT24UserDetailsPassword(password));
		userWsDeatils.setUser(jaxbFactory.createT24UserDetailsUser(username));
    }
    



    

}