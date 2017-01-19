package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.Map;

public abstract interface AdapterPortMetadata
{
  public abstract MetadataSchema getMasterSchema();
  
  public abstract MetadataSchema getImportedSchema(String paramString);
  
  public abstract Map<String, MetadataSchema> getImportedSchemas();
  
  public abstract String getRootElement();
}