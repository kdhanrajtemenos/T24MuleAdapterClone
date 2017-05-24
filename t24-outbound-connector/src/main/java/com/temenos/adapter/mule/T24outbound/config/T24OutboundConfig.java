package com.temenos.adapter.mule.T24outbound.config;

import java.util.Properties;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;

import com.temenos.adapter.common.conf.AdapterProperties;
import com.temenos.adapter.common.conf.DefaultRuntimeConfiguration;
import com.temenos.adapter.common.conf.T24InvalidConfigurationException;
import com.temenos.adapter.common.conf.T24RuntimeConfiguration;
import com.temenos.adapter.common.conf.T24RuntimeConfigurationFactory;
import com.temenos.adapter.common.metadata.T24ServiceXmlMetadata;
import com.temenos.adapter.common.runtime.RuntimeType;
//import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.outbound.RequestType;
import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.adapter.mule.T24outbound.rmi.OutboundRequestExecutor;
import com.temenos.adapter.mule.T24outbound.rmi.ResponseRecord;
import com.temenos.adapter.mule.T24outbound.rmi.T24ConnectionType;
import com.temenos.adapter.mule.T24outbound.rmi.T24RequestSpec;
import com.temenos.adapter.mule.T24outbound.rmi.TafjConnectionProperties;
//import com.temenos.adapter.mule.T24outbound.utils.IoResourceUtil;
//import com.temenos.adapter.oracle.outbound.request.exception.T24RequestProcessingException;
import com.temenos.soa.services.data.CFConstants;

/**
 * This class holds the T24 runtime connection configuration
 */
public class T24OutboundConfig {


    public static final String JBOSS_OFS_CONNECTOR_SERVICE_BEAN_TAFJOFS = "OFSConnectorServiceBeanTAFJOFS";
    public static final String WEBLOGIC_EJB_JNDI_OFSCONNECTOR_SERVICE_BEAN_REMOTE_OFS = "ejb/OFSConnectorServiceBeanRemoteOFS";
    public static final String WEBSPHERE_OFS_CONNECTOR_SERVICE_BEAN_REMOTE_TAFJ = "OFSConnectorServiceBeanRemoteTAFJOFS";

	public static final String runtimeVersionTAFJ = "TAFJ";
	public static final String runtimeVersionTAFC = "TAFC";
	
	private static final int DEFAULT_MAX_POOL_SIZE = 3;
	private static final int DEFAULT_MIN_POOL_SIZE = 0;
	private static final int DEFAULT_ACTION_TIMEOUT = 30;
	private static final int DEFAULT_IDLE_TIMEOUT = 1800;
	private static final String EJB_CLIENT_NAMING = "org.jboss.ejb.client.naming";
	public static final String JBOSS_VERSION = "JBoss 7.2";
	private static final String DEAFAULT_CHARSET = "UTF-8";
	
	private static final String TAFJ_CONNCECTION_TYPE = "WebService";
	private static final String TAFC_CONNCECTION_TYPE = "Agent";
	
	private static final String TAFJ_REMOTE_CONNECTION_HOST = "RemoteConnectionHost";
	private static final String TAFJ_REMOTE_CONNECTION_PORT = "RemoteConnectionPort"; //RemoteConnectionPort //remote.connection.default.port
	private static final String JBOSS_NODE_NAME = "JbossNodeName";
		
	private static final String WEBLOGIC_PROTOCOL_VALUE="t3";
	
	
	private T24ConnectionType connectionType;	
	
	private String runtime;
	
	private TafjConnectionProperties tafjConnectionProperties;
	private T24RuntimeConfiguration tafcRuntimeConfiguration;
	private T24RuntimeConfiguration tafjRuntimeConfiguration;
	
	/* getTafjConnectionProperties */
	private String urlPackagePrefixes; 
	
	/* Need for getTafjRuntimeConfiguration() */
	private String securityPrincipal; 
	private String securityCredentials; 
	private String remotingServerType; 
	private String remotingHost; 
	private Integer remotingPort; 
	
	/* Need for getTafcRuntimeConfiguration() */
	private String agentUser; 
	private String agentPassword; 
	private String agentHosts; 
	private String agentPorts; 
	private Integer agentActionTimeout; 
	private String agentCharset; 
	private String agentEnvVariables; 
	private Integer agentMinPoolSize; 
	private Integer agentMaxPoolSize; 
	private boolean agentPoolingEnabled;
	private Integer agentIdleTimeout; 
	private boolean agentNaiveTrustManager; 
	private boolean agentSSL;
	private String serverType;
	
	private ServerType serverTypeObject;
	
	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	private String nodeNames;
	
	
	/* For debug */
	private static boolean devDebug = false;	
	
	private static T24OutboundConfig instance;
	
	private boolean isConfigured;
	
	
	public boolean isConfigured() {
		return isConfigured;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}
	
	private T24OutboundConfig(){
		
	}
	
	/**
	 * Singleton initialization (Thread safe with double check)
	 * @return T24OutboundConfig - singleton
	 */
	public static T24OutboundConfig getInstance() {
		if (instance == null) {
			synchronized (T24OutboundConfig.class) {
				if (instance == null) {
					instance = new T24OutboundConfig();
					
					/* Sets some non user input runtime parameters */
					instance.setMinimalInitProperties();
				}
			}
		}
		return instance;
	}
	


	/**
	 * Sets non User Input common runtime parameters
	 */
	public void setMinimalInitProperties(){
		instance.setUrlPackagePrefixes(EJB_CLIENT_NAMING); 
		
		instance.setAgentEnvVariables("OFS_SOURCE=IFPA"); //IRISPA IFPA ???
		instance.setAgentSSL(false);
		instance.setAgentCharset(DEAFAULT_CHARSET);
		instance.setAgentNaiveTrustManager(false);
		instance.setAgentIdleTimeout(DEFAULT_IDLE_TIMEOUT);
		instance.setAgentMinPoolSize(DEFAULT_MIN_POOL_SIZE);
		instance.setAgentMaxPoolSize(DEFAULT_MAX_POOL_SIZE);
		instance.setAgentActionTimeout(DEFAULT_ACTION_TIMEOUT);
		instance.setAgentPoolingEnabled(true); //this is the default value
	}
	
	/**
	 * Sets User Input parameters
	 * @param runtime - String (agent type - either TAFJ or TAFC)
	 * @param agentHost - Integer (port number)
	 * @param agentport - Integer (port number)
	 * @param username - String (Agent user)
	 * @param password - String (Agent user password)
	 * @param serverType - ServerType (JBoss 7.2 or Websphere or....)
	 * @param nodeNames - String (node 1, node 2, ....)
	 * @throws RuntimeException
	 */
	public void setRuntime(String runtime, String agentHost, Integer agentport, String username, String password, ServerType serverType, String nodeNames) throws RuntimeException{
		if(runtime.equals(runtimeVersionTAFJ)){
			instance.setConnectionType(T24ConnectionType.resolve(TAFJ_CONNCECTION_TYPE));
			instance.setRemotingHost(agentHost);
			instance.setRemotingPort(agentport);
		}else if (runtime.equals(runtimeVersionTAFC)){
			instance.setConnectionType(T24ConnectionType.resolve(TAFC_CONNCECTION_TYPE));
			instance.setAgentHosts(agentHost);
			instance.setAgentPorts(agentport.toString());
		}else{
			throw new RuntimeException("Incorrect runtime version");
		}
		instance.setSecurityPrincipal(username);
		instance.setSecurityCredentials(password);
		instance.setAgentUser(username);
		instance.setAgentPassword(password);
		instance.setRuntimeVersion(runtime);
		
		instance.serverTypeObject = serverType;
		instance.setRemotingServerType(ServerType.getServerTypeSelector(serverType));
		instance.setNodeNames(nodeNames);
		
	}


	public String getRuntimeVersion() {
		return runtime;
	}

	private void setRuntimeVersion(String runtime) {
		this.runtime = runtime;
	}

	public String getUrlPackagePrefixes() {
		return urlPackagePrefixes;
	}

	public void setUrlPackagePrefixes(String urlPackagePrefixes) {
		this.urlPackagePrefixes = urlPackagePrefixes;
	}

	public String getSecurityPrincipal() {
		return securityPrincipal;
	}

	public void setSecurityPrincipal(String securityPrincipal) {
		this.securityPrincipal = securityPrincipal;
	}

	public String getSecurityCredentials() {
		return securityCredentials;
	}

	public void setSecurityCredentials(String securityCredentials) {
		this.securityCredentials = securityCredentials;
	}

	public String getRemotingServerType() {
		return remotingServerType;
	}

	public void setRemotingServerType(String remotingServerType) {
		this.remotingServerType = remotingServerType;
	}

	public String getRemotingHost() {
		return remotingHost;
	}

	public void setRemotingHost(String remotingHost) {
		this.remotingHost = remotingHost;
	}

	public Integer getRemotingPort() {
		return remotingPort;
	}

	public void setRemotingPort(Integer remotingPort) {
		this.remotingPort = remotingPort;
	}

	public String getAgentUser() {
		return agentUser;
	}

	public void setAgentUser(String agentUser) {
		this.agentUser = agentUser;
	}

	public String getAgentPassword() {
		return agentPassword;
	}

	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}

	public String getAgentHosts() {
		return agentHosts;
	}

	public void setAgentHosts(String agentHosts) {
		this.agentHosts = agentHosts;
	}

	public String getAgentPorts() {
		return agentPorts;
	}

	public void setAgentPorts(String agentPorts) {
		this.agentPorts = agentPorts;
	}

	public Integer getAgentActionTimeout() {
		return agentActionTimeout;
	}

	public void setAgentActionTimeout(Integer agentActionTimeout) {
		this.agentActionTimeout = agentActionTimeout;
	}

	public String getAgentCharset() {
		return agentCharset;
	}

	public void setAgentCharset(String agentCharset) {
		this.agentCharset = agentCharset;
	}

	public String getAgentEnvVariables() {
		return agentEnvVariables;
	}

	public void setAgentEnvVariables(String agentEnvVariables) {
		this.agentEnvVariables = agentEnvVariables;
	}

	public Integer getAgentMinPoolSize() {
		return agentMinPoolSize;
	}

	public void setAgentMinPoolSize(Integer agentMinPoolSize) {
		this.agentMinPoolSize = agentMinPoolSize;
	}

	public Integer getAgentMaxPoolSize() {
		return agentMaxPoolSize;
	}

	public void setAgentMaxPoolSize(Integer agentMaxPoolSize) {
		this.agentMaxPoolSize = agentMaxPoolSize;
	}

	public boolean isAgentPoolingEnabled() {
		return agentPoolingEnabled;
	}

	public void setAgentPoolingEnabled(boolean agentPoolingEnabled) {
		this.agentPoolingEnabled = agentPoolingEnabled;
	}

	public Integer getAgentIdleTimeout() {
		return agentIdleTimeout;
	}

	public void setAgentIdleTimeout(Integer agentIdleTimeout) {
		this.agentIdleTimeout = agentIdleTimeout;
	}

	public boolean isAgentNaiveTrustManager() {
		return agentNaiveTrustManager;
	}

	public void setAgentNaiveTrustManager(boolean agentNaiveTrustManager) {
		this.agentNaiveTrustManager = agentNaiveTrustManager;
	}

	public boolean isAgentSSL() {
		return agentSSL;
	}

	public void setAgentSSL(boolean agentSSL) {
		this.agentSSL = agentSSL;
	}
	
	public static boolean isDevDebug() {
		return devDebug;
	}

	public static void setDevDebug(boolean devDebug) {
		T24OutboundConfig.devDebug = devDebug;
	}

	public T24ConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(T24ConnectionType connectionType) {
		this.connectionType = connectionType;
	}
	
	/**
	 * Get TAFJ Connection Properties (NOT USED)
	 */
	public TafjConnectionProperties getTafjConnectionProperties() {
		if (tafjConnectionProperties == null) {
			tafjConnectionProperties = new TafjConnectionProperties(
					getSecurityPrincipal(), getSecurityCredentials(),
					getRemotingServerType(), getRemotingHost(),
					getRemotingPort(), getUrlPackagePrefixes());
			tafjConnectionProperties.validateConnectionProperties();
		}
		return tafjConnectionProperties;
	}

	/**
	 * Get TAFJ Runtime Configuration
	 * @param requestType 
	 * @throws RuntimeException
	 */
    public T24RuntimeConfiguration getTafjRuntimeConfiguration(RequestType requestType) {
        if (tafjRuntimeConfiguration == null) {
            DefaultRuntimeConfiguration runtimeConfiguration = new DefaultRuntimeConfiguration();
            Properties connectionProperties = runtimeConfiguration.getTafjProperties();

            /* set connection properties */
            connectionProperties.put(TAFJ_REMOTE_CONNECTION_HOST, getRemotingHost());
            connectionProperties.put(TAFJ_REMOTE_CONNECTION_PORT, String.valueOf(getRemotingPort()));
            connectionProperties.put(AdapterProperties.TAFJ_REMOTE_SERVER_TYPE, getRemotingServerType()); 
            connectionProperties.put(AdapterProperties.TAFJ_SECURITY_PRINCIPAL, getSecurityPrincipal());
            connectionProperties.put(AdapterProperties.TAFJ_SECURITY_CREDENTIALS, getSecurityCredentials());
            
            connectionProperties.put(AdapterProperties.T24_AUTH_USER_NAME, getSecurityPrincipal());
            connectionProperties.put(AdapterProperties.T24_AUTH_PASSWORD, getSecurityCredentials());
            
            connectionProperties.setProperty(AdapterProperties.WEBLOGIC_PROTOCOL, WEBLOGIC_PROTOCOL_VALUE); //should be tested without this
            
            connectionProperties.put("jboss.naming.client.ejb.context", "true");
            connectionProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED","false");


            connectionProperties.put(JBOSS_NODE_NAME, getNodeNames());  
            
            if (!RequestType.SERVICE_XML.equals(requestType)){
                switch (serverTypeObject) {
    	            case WEBLOGIC_12C:
    	            case WEBLOGIC_11G:
    	            	connectionProperties.setProperty(AdapterProperties.TAFJ_EJB_NAME, WEBLOGIC_EJB_JNDI_OFSCONNECTOR_SERVICE_BEAN_REMOTE_OFS);
    	                break;
    	            case JBOSS_4_2_3:
    	            case JBOSS_7_2:
    	            	connectionProperties.setProperty(AdapterProperties.TAFJ_EJB_NAME, JBOSS_OFS_CONNECTOR_SERVICE_BEAN_TAFJOFS);
    	                break;
    	            case WEBSPHERE:
    	            	connectionProperties.setProperty(AdapterProperties.TAFJ_EJB_NAME, WEBSPHERE_OFS_CONNECTOR_SERVICE_BEAN_REMOTE_TAFJ);
    	                break;
    	            default:
    	                throw new RuntimeException("Unsupported application server type provided. Please check [server type] property");
                }
            }
            
            
            
            
            /*
            Properties prop = new Properties();
            prop.put("TheUserPort", String.valueOf(getRemotingPort()));
            IoResourseUtil io = new IoResourseUtil();
            io.writePropertiesToFile(prop, "D:\\LOGGER", "connectionProperties.txt");
            */
            

            /* set the authentication properties */
            Properties authenticationProperties = new Properties();
    		authenticationProperties.put(AdapterProperties.T24_AUTH_USER_NAME, getSecurityPrincipal());
    		authenticationProperties.put(AdapterProperties.T24_AUTH_PASSWORD, getSecurityCredentials());
            try {
                instance.tafjRuntimeConfiguration = T24RuntimeConfigurationFactory.buildRuntimeConfiguration(RuntimeType.TAFJ,connectionProperties, authenticationProperties, false);
            } catch (T24InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }     
        return tafjRuntimeConfiguration;
    }
    

	/**
	 * Get TAFC Runtime Configuration
	 * @throws RuntimeException
	 */
	public T24RuntimeConfiguration getTafcRuntimeConfiguration() {
		if (tafcRuntimeConfiguration == null) {
			DefaultRuntimeConfiguration runtimeConfiguration = new DefaultRuntimeConfiguration();
			Properties connectionProperties = runtimeConfiguration.getTafcProperties();

			/* set connection properties */
			connectionProperties.put(AdapterProperties.TAFC_PORTS, getAgentPorts());
			connectionProperties.put(AdapterProperties.TAFC_ENV_PROPS, getAgentEnvVariables());
			connectionProperties.put(AdapterProperties.TAFC_CHARSET, getAgentCharset());
			connectionProperties.put(AdapterProperties.TAFC_ACTION_TIMEOUT, getAgentActionTimeout());
			connectionProperties.put(AdapterProperties.TAFC_MIN_POOL_SIZE, getAgentMinPoolSize());
			connectionProperties.put(AdapterProperties.TAFC_MAX_POOL_SIZE, getAgentMaxPoolSize());
			connectionProperties.put(AdapterProperties.TAFC_SSL, String.valueOf(isAgentSSL()));
			connectionProperties.put(AdapterProperties.TAFC_POOLING_ENABLED, isAgentPoolingEnabled());
			connectionProperties.put(AdapterProperties.TAFC_IDLE_TIMEOUT, getAgentIdleTimeout());
			connectionProperties.put(AdapterProperties.TAFC_TRUST_MANAGER, String.valueOf(isAgentNaiveTrustManager()));

			/* set the authentication properties */
			Properties authProperties = new Properties();
			authProperties.setProperty(AdapterProperties.T24_AUTH_USER_NAME, getAgentUser());
			authProperties.setProperty(AdapterProperties.T24_AUTH_PASSWORD, getAgentPassword());
			try {
				instance.tafcRuntimeConfiguration = T24RuntimeConfigurationFactory.buildRuntimeConfiguration(RuntimeType.TAFC, connectionProperties, authProperties, false);
			} catch (T24InvalidConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
		return tafcRuntimeConfiguration;
	}


	/**
	 * Clears the configuration after successful test connection
	 */
	/*
	public void clearTestConnectData(){
		if(instance.tafcRuntimeConfiguration!=null){
			instance.tafcRuntimeConfiguration=null;			
		}
		if(instance.tafjRuntimeConfiguration!=null){
			instance.tafjRuntimeConfiguration=null;
		}
		instance.setAgentUser("");
		instance.setAgentPassword("");
		setSecurityPrincipal("");
		setSecurityCredentials("");
	}
	*/
	
	/** 
	 * Request-response processing
	 * @param request - String
	 * @param requestType - RequestType
	 * @param xmlMetadata - T24ServiceXmlMetadata
	 * @return ResponseRecord
	 * @throws RuntimeException
	 * */
	protected ResponseRecord getAdapterResponce(String request, RequestType requestType, T24ServiceXmlMetadata xmlMetadata) throws ConnectionException {
		
		T24RuntimeConfiguration config = null;
		if(this.runtime.equals(runtimeVersionTAFJ)){
			config = instance.getTafjRuntimeConfiguration(requestType);
		}else{
			config = instance.getTafcRuntimeConfiguration();
		}
		
  		T24RequestSpec requestSpec = new T24RequestSpec();
  		
  		requestSpec.setT24RequestType(requestType);
  		requestSpec.setT24ServiceMetadata(xmlMetadata);
  	
  		//BaseOutboundProcessor baseOutboundProcessor = new BaseOutboundProcessor(config, requestSpec);
  		OutboundRequestExecutor outboundProcessor = new OutboundRequestExecutor(config, requestSpec);
  		ResponseRecord responce = null;
  		try {
  			responce = outboundProcessor.processRequest(request, requestSpec);
		} catch (RuntimeException e) {
			 new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"201", e.getMessage());
		}
  		return responce;
	}
	
	public String ejbConnectionRepsonse(String requst, RequestType type, T24ServiceXmlMetadataImpl xmlMetaData) throws ConnectionException{
  		String response;
		try {
			ResponseRecord responseRecord = getAdapterResponce(requst, type, xmlMetaData);
			String responceCode = responseRecord.getReturnCode();
			if(!responceCode.equals(CFConstants.RETURN_CODE_SUCCESS)){
				throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"202",responseRecord.getResponse());
			}else{
				response = responseRecord.getResponse();
			}
		} catch (RuntimeException e) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"203",e.getMessage());
		}
    	return response;
    }

	public String getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(String nodeNames) {
		this.nodeNames = nodeNames;
	}
	


}
