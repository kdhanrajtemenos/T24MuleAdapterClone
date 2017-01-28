package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class T24MetadataTree implements MetadataTree
{
	  private Map<String, Metadata> metadataTree = new HashMap<String, Metadata>();
	  private List<String> companies = new ArrayList<String>();
	  
	  public Collection<Metadata> getAllMetadata()
	  {
		  return this.metadataTree.values();
	  }
	  
	  public List<String> getCompanies()
	  {
		  return this.companies;
	  }
	  
	  public void setCompanies(List<String> companies)
	  {
		  this.companies = companies;
	  }
	  
	  public Metadata getMetadata(String name)
	  {
		  return (Metadata)this.metadataTree.get(name);
	  }
	  
	  public Collection<String> getAllNames()
	  {
		  return this.metadataTree.keySet();
	  }
	  
	  public void addMetadata(Metadata metadata)
	  {
		    if (metadata == null) {
		    	throw new NullPointerException("Metadata is null");
		    }
		    this.metadataTree.put(metadata.getName(), metadata);
	  }
}