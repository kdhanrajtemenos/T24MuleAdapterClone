package com.temenos.adapter.mule.T24outbound.metadata.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.metadata.model.Metadata;
import com.temenos.adapter.mule.T24outbound.metadata.model.ServiceXMLMetadataModel;


public class T24OutboundRunTimeMetaDataExtractor extends T24BaseOutboundMetadataExctractor {

	
	/**
	 * Constructor
	 * @param config - inject the connector configuration
	 */
	public T24OutboundRunTimeMetaDataExtractor(ConnectorConfig config){
		super(config);
	}	
	
	public T24ServiceXmlMetadataImpl resolveServiceName(String name){
		/* Get all possible metadata with the names */
		List<Metadata> selectable = getSelectableServicesMetaData();
		
		/* Search for the name */
		Metadata matched = null;
		for(Metadata m : selectable){
			if(m.getName().equals(name)){
				matched = m;
				break;
			}
		}
		if(matched != null){
			try {
		        
		         ServiceXMLMetadataModel model = getOutboundCall().getT24ServiceArtefacts(matched);
		         String summary = model.verify();
		         if (!summary.isEmpty()) {
		        	  throw new Exception(summary);
		         }
		         System.out.println("RunTime Artifact extraction passed for service : " + matched.getName()  );
				 return convertToT24ServiceXmlMetadataImpl(model);
		     } catch (Throwable e) {
		    	   System.out.println("Artifact extraction failed for : " + matched.getName() + " " + e.getClass().toString() + " : " + e.getMessage());		               
		     }
		}
		return null;
	}

	/**
	 * Gets all selectable operation service names as List
	 */
	private List<Metadata> getSelectableServicesMetaData() throws RuntimeException {
		
		try {
			setServicesCollection(getOutboundCall().getT24Metadata(null));

	    } catch (Throwable e) {
	        throw new  RuntimeException("ERROR retreiveing Servicex collection :" + e.getMessage());
	        
	    }
		List<Metadata> onlyBranches = convertTreeToList(this.getServicesCollection(),  getOutboundCall());
		return  onlyBranches;
	}
	
	private List<Metadata> convertTreeToList(Collection<Metadata> servicesCollection, T24OutboundAPICall call){
		List<Metadata> out = new ArrayList<Metadata>();
		for (Metadata metadata : servicesCollection){
			 extractSelectable(metadata,  call, out);
		}
		return out;
	}
	
	/**
	 * recursion without XSD extraction and verification 
	 */
	private void extractSelectable(Metadata metadata, T24OutboundAPICall call, List<Metadata> out){		
		if (!metadata.isSelectable()) {
			for (Metadata m : metadata.getChildren()){
				extractSelectable(m, call, out);
			}
		} else {
		    out.add(metadata);
		}
	} 
	



}
