package com.temenos.adapter.mule.T24inbound.connector.config;

import org.mule.api.ConnectionException;
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

@ConnectionManagement(configElementName = "TAFC", friendlyName = "TAFC Configuration")
public class TAFCConnectorConfig extends AbstractConnectorConfig {

	public TAFCConnectorConfig() {
		super(RuntimeConfigSelector.TAFC);
	}

	private String agentHost;

	private Integer agentPort;

	@Configurable
	@Default(value = "JBOSS72")
	@Placement(order = 1, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Server Type")
	@Summary("T24 instance application server type")
	private RuntimeConfigServerSelector t24RunTime;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 1, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Enviroment Var")
	@Summary("T24 Agent Enviroment Var")
	private String agentEnvVariables;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 2, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Action TimeOut")
	@Summary("T24 Agent Action TimeOut")
	private String agentActionTimeout;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 3, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent User")
	@Summary("T24 Agent User")
	private String agentUser;

	@Configurable
	@Default(value = "20002")
	@Required
	@Password
	@Placement(order = 4, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Password")
	@Summary("T24 Agent Password")
	private String agentPassword;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 5, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Load Balancing")
	@Summary("T24 Agent Load Balancing")
	private String agentLoadBalancing;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 6, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Allow Inputs")
	@Summary("T24 Agent Allow Inputs")
	private String agentAllowInput;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 7, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Compression")
	@Summary("T24 Agent Compression")
	private String agentCompression;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 8, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Compression Threshold")
	@Summary("T24 Agent Compression Threshold")
	private String agentCompressionThreshold;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 9, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Charset")
	@Summary("T24 Agent Charset")
	private String agentCharset;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 10, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Trust Manager")
	@Summary("T24 Agent Trust Manager")
	private String agentNaiveTrustManager;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 11, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent SSL")
	@Summary("T24 Agent SSL")
	private String agentSSL;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 12, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Connection Type")
	@Summary("T24 Agent Connection Type")
	private String connectionType;

	@Configurable
	@Default(value = "20002")
	@Required
	@Placement(order = 13, group = "Connector Runtime Configuration", tab = "Runtime")
	@FriendlyName("T24 Agent Url Package Prefix")
	@Summary("T24 Agent Url Package Prefix")
	private String urlPackagePrefixes;

	@Connect
	public void connect(@ConnectionKey @Default("localhost") String agentHost, @Default("2222") String agentPort)
			throws ConnectionException {
		this.agentHost = agentHost;
		this.agentPort = Integer.valueOf(agentPort);
		splitFullFileName();
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@TestConnectivity(label = "Test and Save Settings")
	public void testConnect(@ConnectionKey @Default("localhost") String agentHost, @Default("2222") String agentPort)
			throws ConnectionException {

		this.agentHost = agentHost;
		this.agentPort = Integer.valueOf(agentPort);
		splitFullFileName();

	}

	/**
	 * @return the agentHost
	 */
	public String getAgentHost() {
		return agentHost;
	}

	/**
	 * @param agentHost the agentHost to set
	 */
	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	/**
	 * @return the agentPort
	 */
	public Integer getAgentPort() {
		return agentPort;
	}

	/**
	 * @param agentPort the agentPort to set
	 */
	public void setAgentPort(Integer agentPort) {
		this.agentPort = agentPort;
	}

	/**
	 * @return the t24RunTime
	 */
	public RuntimeConfigServerSelector getT24RunTime() {
		return t24RunTime;
	}

	/**
	 * @param t24RunTime the t24RunTime to set
	 */
	public void setT24RunTime(RuntimeConfigServerSelector t24RunTime) {
		this.t24RunTime = t24RunTime;
	}

	/**
	 * @return the agentEnvVariables
	 */
	public String getAgentEnvVariables() {
		return agentEnvVariables;
	}

	/**
	 * @param agentEnvVariables the agentEnvVariables to set
	 */
	public void setAgentEnvVariables(String agentEnvVariables) {
		this.agentEnvVariables = agentEnvVariables;
	}

	/**
	 * @return the agentActionTimeout
	 */
	public String getAgentActionTimeout() {
		return agentActionTimeout;
	}

	/**
	 * @param agentActionTimeout the agentActionTimeout to set
	 */
	public void setAgentActionTimeout(String agentActionTimeout) {
		this.agentActionTimeout = agentActionTimeout;
	}

	/**
	 * @return the agentUser
	 */
	public String getAgentUser() {
		return agentUser;
	}

	/**
	 * @param agentUser the agentUser to set
	 */
	public void setAgentUser(String agentUser) {
		this.agentUser = agentUser;
	}

	/**
	 * @return the agentPassword
	 */
	public String getAgentPassword() {
		return agentPassword;
	}

	/**
	 * @param agentPassword the agentPassword to set
	 */
	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}

	/**
	 * @return the agentLoadBalancing
	 */
	public String getAgentLoadBalancing() {
		return agentLoadBalancing;
	}

	/**
	 * @param agentLoadBalancing the agentLoadBalancing to set
	 */
	public void setAgentLoadBalancing(String agentLoadBalancing) {
		this.agentLoadBalancing = agentLoadBalancing;
	}

	/**
	 * @return the agentAllowInput
	 */
	public String getAgentAllowInput() {
		return agentAllowInput;
	}

	/**
	 * @param agentAllowInput the agentAllowInput to set
	 */
	public void setAgentAllowInput(String agentAllowInput) {
		this.agentAllowInput = agentAllowInput;
	}

	/**
	 * @return the agentCompression
	 */
	public String getAgentCompression() {
		return agentCompression;
	}

	/**
	 * @param agentCompression the agentCompression to set
	 */
	public void setAgentCompression(String agentCompression) {
		this.agentCompression = agentCompression;
	}

	/**
	 * @return the agentCompressionThreshold
	 */
	public String getAgentCompressionThreshold() {
		return agentCompressionThreshold;
	}

	/**
	 * @param agentCompressionThreshold the agentCompressionThreshold to set
	 */
	public void setAgentCompressionThreshold(String agentCompressionThreshold) {
		this.agentCompressionThreshold = agentCompressionThreshold;
	}

	/**
	 * @return the agentCharset
	 */
	public String getAgentCharset() {
		return agentCharset;
	}

	/**
	 * @param agentCharset the agentCharset to set
	 */
	public void setAgentCharset(String agentCharset) {
		this.agentCharset = agentCharset;
	}

	/**
	 * @return the agentNaiveTrustManager
	 */
	public String getAgentNaiveTrustManager() {
		return agentNaiveTrustManager;
	}

	/**
	 * @param agentNaiveTrustManager the agentNaiveTrustManager to set
	 */
	public void setAgentNaiveTrustManager(String agentNaiveTrustManager) {
		this.agentNaiveTrustManager = agentNaiveTrustManager;
	}

	/**
	 * @return the agentSSL
	 */
	public String getAgentSSL() {
		return agentSSL;
	}

	/**
	 * @param agentSSL the agentSSL to set
	 */
	public void setAgentSSL(String agentSSL) {
		this.agentSSL = agentSSL;
	}

	/**
	 * @return the connectionType
	 */
	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * @param connectionType the connectionType to set
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 * @return the urlPackagePrefixes
	 */
	public String getUrlPackagePrefixes() {
		return urlPackagePrefixes;
	}

	/**
	 * @param urlPackagePrefixes the urlPackagePrefixes to set
	 */
	public void setUrlPackagePrefixes(String urlPackagePrefixes) {
		this.urlPackagePrefixes = urlPackagePrefixes;
	}

}
