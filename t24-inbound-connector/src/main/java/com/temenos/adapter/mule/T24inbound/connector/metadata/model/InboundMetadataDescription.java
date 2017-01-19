package com.temenos.adapter.mule.T24inbound.connector.metadata.model;


import java.util.List;

public abstract interface InboundMetadataDescription
  extends MetadataDescription
{
  public abstract AdapterPortMetadata getOutputPortMetadata();
  
  public abstract List<String> getOutputSchemaDocuments();
}