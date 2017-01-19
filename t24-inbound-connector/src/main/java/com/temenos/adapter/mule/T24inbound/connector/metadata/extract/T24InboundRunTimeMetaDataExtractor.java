package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import java.util.Properties;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataModel;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.Metadata;

public class T24InboundRunTimeMetaDataExtractor extends T24BaseInboundMetadataExctractor {

	
	/**
	 * Constructor
	 * @param config - inject the connector configuration
	 */
	public T24InboundRunTimeMetaDataExtractor(ConnectorConfig config){
		super(config);
	}	
	
	public InboundMetadataModel resolveServiceName(String name){
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
		        
				InboundMetadataModel model = getInboundCall().getT24EventArtefacts(matched);
		         String summary = model.verify();
		         if (!summary.isEmpty()) {
		        	  throw new Exception(summary);
		         }
		         System.out.println("RunTime Artifact extraction passed for service : " + matched.getName()  );
				 return model;
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
			setEventsCollection(getInboundCall().getT24Metadata(null));

	    } catch (Throwable e) {
	        throw new  RuntimeException("ERROR retreiveing Servicex collection :" + e.getMessage());
	        
	    }
		List<Metadata> onlyBranches = convertTreeToList(this.getEventsCollection(),  getInboundCall());
		return  onlyBranches;
	}
	
	private List<Metadata> convertTreeToList(Collection<Metadata> servicesCollection, T24InboundAPICall call){
		List<Metadata> out = new ArrayList<Metadata>();
		for (Metadata metadata : servicesCollection){
			 extractSelectable(metadata,  call, out);
		}
		return out;
	}
	
	/**
	 * recursion without XSD extraction and verification 
	 */
	private void extractSelectable(Metadata metadata, T24InboundAPICall call, List<Metadata> out){		
		if (!metadata.isSelectable()) {
			for (Metadata m : metadata.getChildren()){
				extractSelectable(m, call, out);
			}
		} else {
		    out.add(metadata);
		}
	} 
	
	/**
	 * this method reads from file
	 * */
	/*
	public  T24ServiceXmlMetadataImpl getModelArtifactsFromFile(String byName, String location){
		String filename = byName + T24InboundDesignTimeMetaDataExtractor.SCHEMA_FILE_EXT;
		IoResourseUtil ioProcessor = new IoResourseUtil();
		Properties prop = ioProcessor.readResourseFile(filename, location, IoResourseUtil.LOAD_SCHEMA);
		return getModelArtifactsFromProperties(prop);
	}
	*/


}
