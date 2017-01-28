package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.List;

public abstract interface Metadata
{
  public abstract String getName();
  
  public abstract Metadata getParent();
  
  public abstract List<Metadata> getChildren();
  
  public abstract boolean isSelectable();
  
  public abstract Metadata getChild(String paramString);
}
