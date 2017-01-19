package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.Properties;

import com.jbase.jremote.DefaultJConnectionFactory;
import com.jbase.jremote.JConnection;
import com.jbase.jremote.JRemoteException;
import com.jbase.jremote.JSubroutineNotFoundException;
import com.jbase.jremote.JSubroutineParameters;
import com.temenos.adapter.common.metadata.T24MetadataException;

public abstract class AbstractTafcMetadataDiscovery
{
  //private static final String T24_INITIALIZER_SERVICE = "JF.INITIALISE.CONNECTION";
  //private static final String T24_DEFAULT_OFS_SOURCE = "JAVA.FRAMEWORK";
  private TafcMetadataConnectionConfiguration configuration;
  
  public AbstractTafcMetadataDiscovery(TafcMetadataConnectionConfiguration configuration)
  {
    if (configuration == null) {
      throw new NullPointerException("Metadata connection configuration for TAFC is null");
    }
    this.configuration = configuration;
  }
  
  protected JConnection getConnection()
    throws T24MetadataException
  {
    DefaultJConnectionFactory connectionFactory = new DefaultJConnectionFactory();
    connectionFactory.setHost(this.configuration.getHost());
    connectionFactory.setPort(this.configuration.getPort());
    
    JConnection connection = null;
    try
    {
      Properties connectionProperties = new Properties();
      connectionProperties.setProperty("env.OFS_SOURCE", "JAVA.FRAMEWORK");
      
      connection = connectionFactory.getConnection("", "", connectionProperties);
      
      connection.call("JF.INITIALISE.CONNECTION", new JSubroutineParameters());
      
      return connection;
    }
    catch (JSubroutineNotFoundException e)
    {
      closeConnection(connection);
      throw new T24MetadataException("Failed to initialise T24 connection. " + e.getMessage(), e);
    }
    catch (JRemoteException e)
    {
      closeConnection(connection);
      throw new T24MetadataException("Failed to connect to T24 for metadata discovery. " + e.getMessage(), e);
    }
  }
  
  protected void closeConnection(JConnection connection)
  {
    if (connection == null) {
      return;
    }
    try
    {
      connection.close();
    }
    catch (JRemoteException e)
    {
      connection = null;
    }
  }
}