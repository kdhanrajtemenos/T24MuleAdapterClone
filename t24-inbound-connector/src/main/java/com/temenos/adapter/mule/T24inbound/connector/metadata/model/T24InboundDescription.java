package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.List;



public class T24InboundDescription
  implements InboundMetadataDescription
{
  private String name;
  private AdapterPortMetadata outputPortData;
  private List<String> outputSchemaDocuments;
  
  public T24InboundDescription(String name, AdapterPortMetadata outputPortData, List<String> outputSchemaDocuments)
  {
    if (name==null || name.isEmpty()) {
      throw new IllegalArgumentException("Name is invalid [" + name + "]");
    }
    if (outputPortData == null) {
      throw new IllegalArgumentException("Output port data is null");
    }
    if ((outputSchemaDocuments == null) || (outputSchemaDocuments.isEmpty())) {
      throw new IllegalArgumentException("Output schema documents is invalid [" + outputSchemaDocuments + "] for flow [" + name + "]");
    }
    this.name = name;
    this.outputPortData = outputPortData;
    this.outputSchemaDocuments = outputSchemaDocuments;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AdapterPortMetadata getOutputPortMetadata()
  {
    return this.outputPortData;
  }
  
  public List<String> getOutputSchemaDocuments()
  {
    return this.outputSchemaDocuments;
  }
}