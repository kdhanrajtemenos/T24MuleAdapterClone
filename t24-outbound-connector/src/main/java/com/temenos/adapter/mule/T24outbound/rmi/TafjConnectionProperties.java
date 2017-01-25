package com.temenos.adapter.mule.T24outbound.rmi;

import javax.naming.Context;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TafjConnectionProperties {

    private final String securityPrincipal;
    private final String securityCredentials;
    private final String remotingHost;
    private final Integer remotingPort;
    private final String remotingServerType;
    private final Properties contextProperties;
    private final List<String> supportedServerTypeList = Arrays.asList("JBoss 7.2");

    public TafjConnectionProperties(String securityPrincipal, String securityCredentials, String remotingServerType, String remotingHost,
                                    Integer remotingPort, String urlPackagePrefixes) {
        this.securityPrincipal = securityPrincipal;
        this.securityCredentials = securityCredentials;
        this.remotingServerType = remotingServerType;
        this.remotingHost = remotingHost;
        this.remotingPort = remotingPort;

        contextProperties = new Properties();
        contextProperties.put(Context.URL_PKG_PREFIXES, urlPackagePrefixes);
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public String getSecurityCredentials() {
        return securityCredentials;
    }

    public String getRemotingHost() {
        return remotingHost;
    }

    public Integer getRemotingPort() {
        return remotingPort;
    }

    public Properties getContextProperties() {
        return contextProperties;
    }
    
    public void validateConnectionProperties() {
        //validate the server Type
        if (!supportedServerTypeList.contains(this.remotingServerType) ) {
            throw new IllegalArgumentException("Unsupported Remoting Server Type defined in connection properties.");
        }
    }
}
