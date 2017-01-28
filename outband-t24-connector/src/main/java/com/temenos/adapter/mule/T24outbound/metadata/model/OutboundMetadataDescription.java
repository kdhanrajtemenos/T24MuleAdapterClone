package com.temenos.adapter.mule.T24outbound.metadata.model;


import java.util.List;

import com.temenos.adapter.common.metadata.AdapterPortMetadata;
import com.temenos.adapter.common.metadata.MetadataDescription;

public abstract interface OutboundMetadataDescription
  extends MetadataDescription
{
  public abstract AdapterPortMetadata getInputPortMetadata();
  
  public abstract AdapterPortMetadata getOutputPortMetadata();
  
  public abstract List<String> getInputSchemaDocuments();
  
  public abstract List<String> getOutputSchemaDocuments();
}