package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

import java.util.List;

import java.util.ArrayList;


public class T24InboundMetadata
  implements Metadata
{
  private String name;
  
  public T24InboundMetadata(String name)
  {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name is null or empty");
    }
    this.name = name;
  }
  
  public List<Metadata> getChildren()
  {
    return new ArrayList<Metadata>();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Metadata getParent()
  {
    return null;
  }
  
  public boolean isSelectable()
  {
    return true;
  }
  
  public Metadata getChild(String childName)
  {
    return null;
  }
}
