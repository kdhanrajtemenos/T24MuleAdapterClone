package com.temenos.adapter.mule.T24inbound.connector.rmi;

import com.temenos.adapter.common.conf.AdapterProperties;
import com.temenos.adapter.common.conf.T24InvalidConfigurationException;
import com.temenos.adapter.common.conf.T24RuntimeConfiguration;
import com.temenos.adapter.common.conf.T24RuntimeConfigurationFactory;
import com.temenos.adapter.common.runtime.RuntimeType;
import com.temenos.adapter.common.runtime.TafjServerType;

import java.util.*;

public class TAFJRuntimeConfigurationBuilder {


    private static final String AZ = "abcdefghijklmnopqrstuvwxyz";

    private static String randomString(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AZ.charAt(rnd.nextInt(AZ.length())));
        return sb.toString();
    }

    private static final String REMOTE_HOST = "remote.connection.%s.host";
    private static final String REMOTE_PORT = "remote.connection.%s.port";
    private static final String REMOTE_USERNAME = "remote.connection.%s.username";
    private static final String REMOTE_PASSWORD = "remote.connection.%s.password";
    private static final String REMOTE_POLICY_NONANON = "remote.connection.%s.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS";
    private static final String REMOTE_POLICY_NOPLNTXT = "remote.connection.%s.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT";
    private static final String REMOTE_TIMEOUT = "remote.connection.%s.connect.timeout";
    private static final String REMOTE_SOTIMEOUT = "remote.connection.%s.connect.sotimeout";

    private static String setupNode(String ip, String port, String username,
                                    String password, Properties props) {
        while (true) {
            String hostName = randomString(10);
            String host = String.format(REMOTE_HOST, hostName);
            if (props.containsKey(host)) // random failure, already exists
                continue;
            props.put(host, ip);
            props.put(String.format(REMOTE_PORT, hostName), port);
            props.put(String.format(REMOTE_USERNAME, hostName), username);
            props.put(String.format(REMOTE_PASSWORD, hostName), password);
            props.put(String.format(REMOTE_POLICY_NONANON, hostName), "false");
            props.put(String.format(REMOTE_POLICY_NOPLNTXT, hostName), "false");
            props.put(String.format(REMOTE_TIMEOUT, hostName), "30000");
            props.put(String.format(REMOTE_SOTIMEOUT, hostName), "30000");
            return hostName;
        }
    }

    private static String concat(List<String> values, String sep) {
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s).append(sep);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static T24RuntimeConfiguration createTAFJRuntimeConfiguration(
            String remoteServerType, List<String> hostAddresses,
            List<Integer> ports, List<String> nodes, String securityPrincipal,
            String securityCredential, boolean remotingBeanStateful,
            String weblogicProtocol) throws T24InvalidConfigurationException {
    	
        validateServerConfiguration(hostAddresses, ports);
        TafjServerType serverType = TafjServerType.fromValue(remoteServerType);
        if (serverType == null) {
            throw new RuntimeException("Invalid remote server type property [" + remoteServerType + "]");
        }
        System.out.println("Building runtime configuration for remote server type [" + remoteServerType + "]");
        switch (serverType) {
            case JBOSS_7_2:
                return createJBoss72Configuration(serverType, hostAddresses, ports, nodes, securityPrincipal, securityCredential, remotingBeanStateful);
            case WEBLOGIC_11G:
            	/* Not yet implmented*/
            	throw new RuntimeException("Unsupported remote server type [" + remoteServerType + "]" + " WEBLOGIC12G Not yet implmented");
            case WEBLOGIC_12C:
                return createWeblogicConfiguration(serverType, hostAddresses, ports, nodes, securityPrincipal, securityCredential, weblogicProtocol);
            default:
                throw new RuntimeException("Unsupported remote server type [" + remoteServerType + "]");
        }
    }

    private static T24RuntimeConfiguration createWeblogicConfiguration(
            TafjServerType serverType, List<String> hostAddresses,
            List<Integer> ports, List<String> nodes, String securityPrincipal,
            String securityCredential, String weblogicProtocol) throws T24InvalidConfigurationException {

        Properties authenticationProperties = new Properties();
        authenticationProperties.put(AdapterProperties.T24_AUTH_USER_NAME, securityPrincipal);
        authenticationProperties.put(AdapterProperties.T24_AUTH_PASSWORD,securityCredential);

        Properties connectionProperties = new Properties();
        connectionProperties.setProperty(AdapterProperties.TAFJ_REMOTE_SERVER_TYPE, serverType.toString());
        connectionProperties.setProperty(AdapterProperties.TAFJ_REMOTE_CONNECTION_HOSTS, getCommaSeparatedHostList(hostAddresses));
        connectionProperties.setProperty(AdapterProperties.TAFJ_REMOTE_CONNECTION_PORTS, getCommaSeparatedPortList(ports));
        connectionProperties.setProperty(AdapterProperties.TAFJ_SECURITY_PRINCIPAL, securityPrincipal);
        connectionProperties.setProperty(AdapterProperties.TAFJ_SECURITY_CREDENTIALS, securityCredential);
        connectionProperties.setProperty(AdapterProperties.WEBLOGIC_PROTOCOL, weblogicProtocol);
        
        return T24RuntimeConfigurationFactory.buildRuntimeConfiguration( RuntimeType.TAFJ, connectionProperties, authenticationProperties, true);

    }

    private static String getCommaSeparatedHostList(List<String> input){
        StringBuilder result = new StringBuilder();
        for(String string : input) {
            result.append(string);
            result.append(",");
        }
        return trimLastCommaIfNeeded(result);
    }

    private static String trimLastCommaIfNeeded(StringBuilder result) {
        return result.length() > 0 ? result.substring(0, result.length() - 1): "";
    }

    private static String getCommaSeparatedPortList(List<Integer> input){
        StringBuilder result = new StringBuilder();
        for(Integer string : input) {
            result.append(string);
            result.append(",");
        }
        return trimLastCommaIfNeeded(result);
    }

    private static T24RuntimeConfiguration createJBoss72Configuration(TafjServerType serverType,
                                                                      List<String> hostAddresses, List<Integer> ports,
                                                                      List<String> nodes, String securityPrincipal,
                                                                      String securityCredential, boolean remotingBeanStateful) throws T24InvalidConfigurationException {

        Properties connectionProperties = new Properties();
        List<String> servers = new ArrayList<String>();
        for (int i = 0; i < hostAddresses.size(); i++) {
            servers.add(setupNode(hostAddresses.get(i),
                    String.valueOf(ports.get(i)), securityPrincipal,
                    securityCredential, connectionProperties));
        }
        // connections to be used
        connectionProperties.setProperty(AdapterProperties.TAFJ_REMOTE_SERVER_TYPE, serverType.toString());
        connectionProperties.put("remote.connections", concat(servers, ","));
        connectionProperties.put("jboss.naming.client.ejb.context", "true");
        connectionProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED","false");

        //connectionProperties.put(AdapterProperties.JBOSS_NODE_NAME, concat(nodes, ",")); //"JbossNodeName"
        connectionProperties.put("JbossNodeName", concat(nodes, ",")); 
        connectionProperties.put(AdapterProperties.TAFJ_EJB_BEAN_STATEFUL, Boolean.toString(remotingBeanStateful));
        
        // Set<Map.Entry<Object, Object>> entries = connectionProperties.entrySet();

        Properties authenticationProperties = new Properties();
        authenticationProperties.put(AdapterProperties.T24_AUTH_USER_NAME, securityPrincipal);
        authenticationProperties.put(AdapterProperties.T24_AUTH_PASSWORD, securityCredential);

        return T24RuntimeConfigurationFactory.buildRuntimeConfiguration( RuntimeType.TAFJ, connectionProperties, authenticationProperties, true);
    }

    private static void validateServerConfiguration(List<String> hostAddresses, List<Integer> ports) {
        if (hostAddresses.size() < 1 || ports.size() < 1) {
            throw new RuntimeException(
                    String.format(
                            "Either of 'RemotingHosts' or 'RemotingPorts' not specified\nRemoting hosts: %s\nRemoting ports: %s",
                            hostAddresses, ports));
        }
        if (hostAddresses.size() != ports.size()) {
            throw new RuntimeException(
                    String.format("'RemotingHosts' and 'RemotingPorts' length mismatch\nRemoting hosts: %s\nRemoting ports: %s", hostAddresses, ports));
        }
    }

}

