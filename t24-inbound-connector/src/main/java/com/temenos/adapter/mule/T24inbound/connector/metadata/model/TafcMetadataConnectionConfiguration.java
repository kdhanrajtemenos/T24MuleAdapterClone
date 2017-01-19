package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import com.temenos.adapter.common.util.ConfigDataUtil;

public class TafcMetadataConnectionConfiguration
implements MetadataConnectionConfiguration
{
private String host;
private int port;

public TafcMetadataConnectionConfiguration(String host, int port)
{
  if ((ConfigDataUtil.isNullOrEmpty(host)) || (!ConfigDataUtil.isPositiveInt(port + ""))) {
    throw new IllegalArgumentException("Invalid argument: host [" + host + "] port [" + port + "]");
  }
  this.host = host;
  this.port = port;
}

public T24MetadataConnectionType getConnectionType()
{
  return T24MetadataConnectionType.TAFC_AGENT;
}

public String getHost()
{
  return this.host;
}

public int getPort()
{
  return this.port;
}
}
