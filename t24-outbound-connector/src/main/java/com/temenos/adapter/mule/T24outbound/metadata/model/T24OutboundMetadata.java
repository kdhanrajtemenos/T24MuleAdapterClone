package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class T24OutboundMetadata implements Metadata
{
	private String name;
	private Map<String, Metadata> children;
	private String toString;
	
	public T24OutboundMetadata(String name)
	{
		  if (name == null || name.isEmpty()) {
		    throw new IllegalArgumentException("Name is null or empty");
		  }
		  this.name = name;
		  this.children = new HashMap<String, Metadata>();
	}
	
	public void addOperationMetadata(T24OperationMetadata metadata)
	{
		this.children.put(metadata.getName(), metadata);
	}
	

	public List<Metadata> getChildren()
	{
		return Collections.unmodifiableList(new ArrayList<Metadata>(this.children.values()));
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
		return false;
	}
	
	public Metadata getChild(String childName)
	{
		return this.children.get(childName);
	}
	
	public String toString()
	{
		  if (this.toString == null)
		  {
			    StringBuffer toStringBuffer = new StringBuffer();
			    toStringBuffer.append("Name=");
			    toStringBuffer.append(getName());
			    toStringBuffer.append(",");
			    toStringBuffer.append("isSelectable=");
			    toStringBuffer.append(isSelectable());
			    this.toString = toStringBuffer.toString();
		  }
		  return this.toString;
	}
}
