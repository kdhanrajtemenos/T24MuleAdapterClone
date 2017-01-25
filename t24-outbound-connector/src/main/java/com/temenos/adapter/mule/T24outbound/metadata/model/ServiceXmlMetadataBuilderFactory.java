package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.List;

import com.temenos.adapter.common.metadata.outbound.ServiceXmlInputMetadataBuilder;
import com.temenos.adapter.common.metadata.outbound.ServiceXmlMetadataBuilder;
import com.temenos.adapter.common.metadata.outbound.ServiceXmlOutputMetadataBuilder;
import com.temenos.adapter.common.metadata.outbound.ServiceXmlSchemaProcessor;

public class ServiceXmlMetadataBuilderFactory
{
  private T24ServiceOperationImpl serviceOperation;
  private List<String> discoveredSchemas;
  private ServiceXmlSchemaProcessor schemaProcessor;
  
  public ServiceXmlMetadataBuilderFactory(T24ServiceOperationImpl serviceOperation, List<String> retrievedSchemas)
  {
    this.serviceOperation = serviceOperation;
    this.discoveredSchemas = retrievedSchemas;
    this.schemaProcessor = new ServiceXmlSchemaProcessor(serviceOperation.getTarget(), this.discoveredSchemas);
  }
  
  public ServiceXmlMetadataBuilder getInputMetadataBuilder()
  {
    return new ServiceXmlInputMetadataBuilder(this.schemaProcessor.getSchemaName(), this.schemaProcessor.getInputSchemas(), this.serviceOperation.getActionType());
  }
  
  public ServiceXmlMetadataBuilder getOutputMetadataBuilder()
  {
    return new ServiceXmlOutputMetadataBuilder(this.schemaProcessor.getSchemaName(), this.schemaProcessor.getOutputSchemas());
  }
}
