package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

public class MetadataSchema
{
  private String schemaNamespace;
  private String schemaLocation;
  private String schemaContent;
  private String toStringValue;
  
  public MetadataSchema(String schemaNamespace, String schemaLocation, String schemaContent)
  {
    this.schemaNamespace = schemaNamespace;
    this.schemaLocation = schemaLocation;
    this.schemaContent = schemaContent;
  }
  
  public String getNamespace()
  {
    return this.schemaNamespace;
  }
  
  public String getLocation()
  {
    return this.schemaLocation;
  }
  
  public String getContent()
  {
    return this.schemaContent;
  }
  
  public String toString()
  {
    if (this.toStringValue == null)
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[Schema namespace=");
      buffer.append(this.schemaNamespace);
      buffer.append(", Schema location=");
      buffer.append(this.schemaLocation);
      buffer.append(", Schema content=");
      buffer.append(this.schemaContent);
      buffer.append("]");
      this.toStringValue = buffer.toString();
    }
    return this.toStringValue;
  }
}

