package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.HashMap;
import java.util.Map;

public class AdapterPortMetadataImpl
  implements AdapterPortMetadata
{
  private String rootElement;
  private MetadataSchema masterSchema;
  private Map<String, MetadataSchema> importedSchemas;
  
  public AdapterPortMetadataImpl(String rootElement, MetadataSchema masterSchema)
  {
    if (rootElement==null || rootElement.isEmpty()) {
      throw new IllegalArgumentException("Invalid rootElement [" + rootElement + "]");
    }
    if (masterSchema == null) {
      throw new NullPointerException("masterSchemaDocument is null");
    }
    this.rootElement = rootElement;
    this.masterSchema = masterSchema;
    this.importedSchemas = new HashMap<String, MetadataSchema>();
  }
  
  public MetadataSchema getMasterSchema()
  {
    return this.masterSchema;
  }
  
  public void addImportedSchema(MetadataSchema importedSchema)
  {
    this.importedSchemas.put(importedSchema.getNamespace(), importedSchema);
  }
  
  public MetadataSchema getImportedSchema(String schemaNamespace)
  {
    return (MetadataSchema)this.importedSchemas.get(schemaNamespace);
  }
  
  public Map<String, MetadataSchema> getImportedSchemas()
  {
    return this.importedSchemas;
  }
  
  public String getRootElement()
  {
    return this.rootElement;
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[Root element=");
    buffer.append(this.rootElement);
    buffer.append(", Master schema=");
    buffer.append(this.masterSchema);
    buffer.append(", Imported schema documents=");
    buffer.append(this.importedSchemas);
    
    return buffer.toString();
  }
}
