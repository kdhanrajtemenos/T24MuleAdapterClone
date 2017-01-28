package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.List;

import com.temenos.adapter.common.metadata.AdapterPortMetadata;

public class T24OutboundDescription implements OutboundMetadataDescription {

	private String name;
	  private AdapterPortMetadata inputPortData;
	  private AdapterPortMetadata outputPortData;
	  private List<String> inputSchemas;
	  private List<String> outputSchemas;
	  
	  public T24OutboundDescription(String name, AdapterPortMetadata inputPortData, AdapterPortMetadata outputPortData, List<String> inputSchemas, List<String> outputSchemas)
	  {
		    if ((inputPortData == null) || (outputPortData == null)) {
		    	throw new NullPointerException("Input or output port data is null");
		    }
		    this.name = name;
		    this.inputPortData = inputPortData;
		    this.outputPortData = outputPortData;
		    this.inputSchemas = inputSchemas;
		    this.outputSchemas = outputSchemas;
	  }
	  
	  public String getName()
	  {
		  return this.name;
	  }
	  
	  public AdapterPortMetadata getInputPortMetadata()
	  {
		  return this.inputPortData;
	  }
	  
	  public AdapterPortMetadata getOutputPortMetadata()
	  {
		  return this.outputPortData;
	  }
	  
	  public List<String> getInputSchemaDocuments()
	  {
		  return this.inputSchemas;
	  }
	  
	  public List<String> getOutputSchemaDocuments()
	  {
		  return this.outputSchemas;
	  }

}
