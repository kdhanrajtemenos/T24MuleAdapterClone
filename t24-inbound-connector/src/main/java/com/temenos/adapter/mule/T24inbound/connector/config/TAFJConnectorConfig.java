package com.temenos.adapter.mule.T24inbound.connector.config;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.Required;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;

import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;
import com.temenos.adapter.mule.T24inbound.connector.proxy.ServiceEndPointType;
import com.temenos.adapter.mule.T24inbound.connector.utils.AddressChecker;
import com.temenos.soa.services.data.xsd.ObjectFactory;
import com.temenos.soa.services.data.xsd.Response;
import com.temenos.soa.services.data.xsd.ResponseDetails;
import com.temenos.soa.services.data.xsd.T24UserDetails;

@ConnectionManagement(configElementName = "TAFJ", friendlyName = "TAFJ Configuration")
public class TAFJConnectorConfig extends AbstractConnectorConfig {

	public TAFJConnectorConfig() {
		super(RuntimeConfigSelector.TAFJ);
	}

	private final String WS_NAME = "/IntegrationFlowServiceWS?wsdl";
	
	 /**
     * Holding the SOAP User credentials
     */
    private T24UserDetails userWsDeatils;


    /**
     * The SOAP client
     */
    private IntegrationFlowServiceWSClient client;

    
	/**
	 * Web service Url
	 */
	@Configurable
	@Default("http://localhost:9089/axis2/services")
	@Required
	@Placement(order = 1, group = "Connection", tab = "General")
	private String serviceUrl;

	private String serviceUserName;

	private String servicePassword;

	@Configurable
	@Default(value = "JBOSS72")
	@Placement(order = 1, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Server Type")
	@Summary("T24 instance application server type")
	private RuntimeConfigServerSelector t24RunTime;
	/**
	 * Sets a numeric filed in the design-time connector configuration window
	 * for inputting the T24 Runtime port
	 */
	@Configurable
	@Default(value = "localhost")
	@Placement(order = 2, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Host")
	@Summary("T24 runtime connection port value")
	private String t24Host;

	/**
	 * Sets a numeric filed in the design-time connector configuration window
	 * for inputting the T24 Runtime port
	 */
	@Configurable
	@Default(value = "4447")
	@Placement(order = 3, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Port")
	@Summary("T24 runtime connection port value")
	private Integer t24Port;

	@Configurable
	@Default(value = "SSOUSER1")
	@Placement(order = 4, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 User")
	@Summary("T24 runtime connection port value")
	private String t24User;

	@Configurable
	@Default(value = "123456")
	@Password
	@Placement(order = 5, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 password")
	@Summary("T24 runtime connection port value")
	private String t24Password;

	@Configurable
	@Default("node1")
	@FriendlyName("T24 Jboss Node")
	@Placement(order = 6, group = "Connector Runtime Configuration", tab = "Runtime")
	@Summary("T24 JBoss server node name")
	private String nodeName;

	@Configurable
	@Default("true")
	@FriendlyName("T24 Ejb Stateful")
	@Placement(order = 7, group = "Connector Runtime Configuration", tab = "Runtime")
	@Summary("T24 ejb stateful option")
	private Boolean ejbStateful;

	@Configurable
	@Default("")
	@FriendlyName("T24 Ejb Bean Name")
	@Placement(order = 8, group = "Connector Runtime Configuration", tab = "Runtime")
	@Summary("T24 JNDI ejb name")
	private String ejbName;

	@Connect
	public void connect(@ConnectionKey @Default("INPUTT") String serviceUserName,
			@Default("123456") @Password String servicePassword) throws ConnectionException {
		this.serviceUserName = serviceUserName;
		this.servicePassword = servicePassword;
		
		setWsConnectorUser(serviceUserName, servicePassword);
		splitFullFileName();
	}

	@Override
	public void disconnect() {

	}

	@Override
	public boolean isConnected() {
		return client != null;
	}

	@TestConnectivity(label = "Test and Save Settings")
	public void testConnect(@ConnectionKey @Default("INPUTT") String serviceUserName,
			@Default("123456") @Password String servicePassword) throws ConnectionException {

		if (!verifyUserInput(serviceUserName, getServiceUrl(), getSettingsFolder())) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", "Incorrect input parameters!");
		}
		initIntegrationServiceFlow(getServiceUrl());
		if (!isConnected()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "112", "Can't connect to service!");
		}

		ResponseDetails responseDetails = client.getAllFlowNames(userWsDeatils).getResponseDetails().getValue();
		String testSOAP = responseDetails.getReturnCode().getValue();
		if (testSOAP == null || testSOAP.isEmpty()) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "113", "Can't work with service!");
		}

		if ("FAILURE".equals(testSOAP)) {
			String result = "";
			for (Response resp : responseDetails.getResponses()) {
				result += resp.getResponseCode().getValue() + " ";
			}

			throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "114",
					"Failure in web service call with code: " + result);
		}

		splitFullFileName();

	}

	/**
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * @param serviceUrl
	 *            the serviceUrl to set
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl + WS_NAME;
	}

	/**
	 * @return the serviceUserName
	 */
	public String getServiceUserName() {
		return serviceUserName;
	}

	/**
	 * @param serviceUserName
	 *            the serviceUserName to set
	 */
	public void setServiceUserName(String serviceUserName) {
		this.serviceUserName = serviceUserName;
	}

	/**
	 * @return the servicePassword
	 */
	public String getServicePassword() {
		return servicePassword;
	}

	/**
	 * @param servicePassword
	 *            the servicePassword to set
	 */
	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}

	/**
	 * @return the t24RunTime
	 */
	public RuntimeConfigServerSelector getT24RunTime() {
		return t24RunTime;
	}

	/**
	 * @param t24RunTime
	 *            the t24RunTime to set
	 */
	public void setT24RunTime(RuntimeConfigServerSelector t24RunTime) {
		this.t24RunTime = t24RunTime;
	}

	/**
	 * @return the t24Host
	 */
	public String getT24Host() {
		return t24Host;
	}

	/**
	 * @param t24Host
	 *            the t24Host to set
	 */
	public void setT24Host(String t24Host) {
		this.t24Host = t24Host;
	}

	/**
	 * @return the t24Port
	 */
	public Integer getT24Port() {
		return t24Port;
	}

	/**
	 * @param t24Port
	 *            the t24Port to set
	 */
	public void setT24Port(Integer t24Port) {
		this.t24Port = t24Port;
	}

	/**
	 * @return the t24User
	 */
	public String getT24User() {
		return t24User;
	}

	/**
	 * @param t24User
	 *            the t24User to set
	 */
	public void setT24User(String t24User) {
		this.t24User = t24User;
	}

	/**
	 * @return the t24Password
	 */
	public String getT24Password() {
		return t24Password;
	}

	/**
	 * @param t24Password
	 *            the t24Password to set
	 */
	public void setT24Password(String t24Password) {
		this.t24Password = t24Password;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName
	 *            the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the ejbStateful
	 */
	public Boolean getEjbStateful() {
		return ejbStateful;
	}

	/**
	 * @param ejbStateful
	 *            the ejbStateful to set
	 */
	public void setEjbStateful(Boolean ejbStateful) {
		this.ejbStateful = ejbStateful;
	}

	/**
	 * @return the ejbName
	 */
	public String getEjbName() {
		return ejbName;
	}

	/**
	 * @param ejbName
	 *            the ejbName to set
	 */
	public void setEjbName(String ejbName) {
		this.ejbName = ejbName;
	}

	  /**
     * Set T24UserDetails
     *
     * @param username - SOAP client user name
     * @param password - SOAP client password
     * @param coCode   - company code
     */
    private void setWsConnectorUser(String username, String password) {
        ObjectFactory jaxbFactory = new ObjectFactory();
        userWsDeatils = new T24UserDetails();
        userWsDeatils.setPassword(jaxbFactory.createT24UserDetailsPassword(password));
        userWsDeatils.setUser(jaxbFactory.createT24UserDetailsUser(username));
    }
    
    /**
	 * @return the userWsDeatils
	 */
	public T24UserDetails getUserWsDeatils() {
		return userWsDeatils;
	}

	/**
	 * @param userWsDeatils the userWsDeatils to set
	 */
	public void setUserWsDeatils(T24UserDetails userWsDeatils) {
		this.userWsDeatils = userWsDeatils;
	}

	/**
	 * @return the client
	 */
	public IntegrationFlowServiceWSClient getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(IntegrationFlowServiceWSClient client) {
		this.client = client;
	}

	/**
     * Creates SOAP client for the given WSDL file
     *
     * @param serviceUrl - the SOAP service WSDL location
     */
    public void initIntegrationServiceFlow(String serviceUrl) throws ConnectionException {
        IntegrationFlowServiceWSClient proxyClient;
        proxyClient = new IntegrationFlowServiceWSClient(serviceUrl);
        proxyClient.clientConnect(ServiceEndPointType.HTTPSOAP12);
        setClient(proxyClient);
    }
    private boolean verifyUserInput(String username, String serviceUrl, String folder) {
        if (StringUtils.isBlank(username)) {
            return false;
        }

        if (StringUtils.isBlank(serviceUrl) || !AddressChecker.isValidURL(serviceUrl)) {
            return false;
        }

        if (StringUtils.isBlank(folder)) {
            return false;
        }
        return true;
    }
}
