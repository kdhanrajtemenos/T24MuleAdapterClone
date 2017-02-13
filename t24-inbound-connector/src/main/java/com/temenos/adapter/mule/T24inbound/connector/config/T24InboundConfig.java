package com.temenos.adapter.mule.T24inbound.connector.config;

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

/**
 * Keep T24 runtime connection configuration here
 */
public class T24InboundConfig {

	public static final String runtimeVersionTAFJ = "TAFJ";
	public static final String runtimeVersionTAFC = "TAFC";
	
	private static final int DEFAULT_MAX_POOL_SIZE = 3;
	private static final int DEFAULT_MIN_POOL_SIZE = 0;
	private static final int DEFAULT_ACTION_TIMEOUT = 30;
	private static final int DEFAULT_IDLE_TIMEOUT = 1800;
	private static final String EJB_CLIENT_NAMING = "org.jboss.ejb.client.naming";
	private static final String JBOSS_VERSION = "JBoss 7.2";
	private static final String DEAFAULT_CHARSET = "UTF-8";
	
	private static final String TAFJ_CONNCECTION_TYPE = "WebService";
	private static final String TAFC_CONNCECTION_TYPE = "Agent";
	
	private static final String TAFJ_REMOTE_CONNECTION_HOST = "RemoteConnectionHost";
	private static final String TAFJ_REMOTE_CONNECTION_PORT = "RemoteConnectionPort"; //RemoteConnectionPort //remote.connection.default.port
	private static final String JBOSS_NODE_NAME = "JbossNodeName";
	
	private static final String JBOSS_NODE_NAME_VALUE = "node1";
	
	private static final String WEBLOGIC_PROTOCOL_VALUE="t3";
	
}
