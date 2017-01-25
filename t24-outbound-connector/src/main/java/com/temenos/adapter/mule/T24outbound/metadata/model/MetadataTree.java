package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.Collection;

public abstract interface MetadataTree
{
  public abstract Metadata getMetadata(String paramString);
  
  public abstract Collection<String> getAllNames();
  
  public abstract Collection<Metadata> getAllMetadata();
}

