package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.ArrayList;
import java.util.List;

public class T24OperationMetadata implements Metadata
{
	  private T24ServiceOperationImpl operation;
	  private Metadata parent;
	  private String toString;
	  
	  public T24OperationMetadata(T24ServiceOperationImpl operation, Metadata parent)
	  {
		    if ((operation == null) || (parent == null)) {
		      throw new IllegalArgumentException("T24Operation or parent service is null");
		    }
		    this.operation = operation;
		    this.parent = parent;
	  }
	  
	  public List<Metadata> getChildren()
	  {
		  	return new ArrayList<Metadata>();
	  }
	  
	  public String getName()
	  {
		  	return this.operation.getName();
	  }
	  
	  public Metadata getParent()
	  {
		  	return this.parent;
	  }
	  
	  public boolean isSelectable()
	  {
		  	return true;
	  }
	  
	  public T24ServiceOperationImpl getOperation()
	  {
		  	return this.operation;
	  }
	  
	  public Metadata getChild(String childName)
	  {
		  	return null;
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
