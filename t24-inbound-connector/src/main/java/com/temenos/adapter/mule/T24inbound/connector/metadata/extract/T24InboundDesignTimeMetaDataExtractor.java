package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataModel;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.Metadata;
import com.temenos.adapter.mule.T24inbound.connector.utils.IoResourseUtil;


//import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;


public class T24InboundDesignTimeMetaDataExtractor extends T24BaseInboundMetadataExctractor {

	
	/**
	 * Constructor
	 * @param config - inject the connector configuration
	 */
	public T24InboundDesignTimeMetaDataExtractor(ConnectorConfig config){
		super(config);

	}
	
	/**
	 * Constructor
	 * @param config 
	 */
	public T24InboundDesignTimeMetaDataExtractor(){
		super(null);
	}
	

	/**
	 * Gets all selectable operation service names as List
	 */
	public List<String> getEventsNames() throws RuntimeException {
		Collection<Metadata> mdCollection = null;
		T24InboundAPICall apicall = getInboundCall();
		try {
			mdCollection = apicall.getT24Metadata(null);
					

	    } catch (Exception e) {
	        throw new  RuntimeException("ERROR retreiveing Servicex collection :" + e.getMessage());
	        
	    }
		setEventsCollection(mdCollection);
	    for (Metadata metadata : mdCollection) {
	    	extractEventsNames(metadata, apicall);
	    }
	   
		return  getNames();
	}
	
	
	private List<String> getNames(){
		List<String> result = new ArrayList<String>();		
		for(InboundMetadataModel model : getOnlySelectable()){
			result.add(model.getName());
		}		
		return result;
	}

	/**
	 * Gets meta data artifacts for a given operation by its name either from a file or selectable list if it is not empty
	 * @param byName - operation name
	 * @return
	 */
	public  InboundMetadataModel getModelArtifacts(String byName, String location){
		if(!getOnlySelectable().isEmpty()){
			for(InboundMetadataModel selectable : getOnlySelectable()){
				if(selectable.getName().equals(byName)){
					return selectable;
				}
			}
		}
		/*
		else{
			String filename = byName + SCHEMA_FILE_EXT;
			System.out.println(filename);
			IoResourseUtil ioProcessor = new IoResourseUtil();
			Properties prop = ioProcessor.readResourseFile(filename, location, IoResourseUtil.LOAD_SCHEMA);
			return getModelArtifactsFromProperties(prop);
		}
		*/
		
		return null;
	}
	
		
	/**
	 * Meta data tree recursion builder
	 * @param metadata - Meta data
	 * @param call - T24APICall 
	 */
	private void extractEventsNames(Metadata metadata, T24InboundAPICall call){		
		if (!metadata.isSelectable()) {
			for (Metadata m : metadata.getChildren()){
				extractEventsNames(m, call);
			}
		} else {
		    try {	        
		    	InboundMetadataModel model = call.getT24EventArtefacts(metadata);
		         String summary = model.verify();
		         if (!summary.isEmpty()) {
		        	  throw new Exception(summary);
		         }
		         System.out.println("Artifact extraction passed for service : " + metadata.getName()  );
		         getOnlySelectable().add(model);
		     } catch (Throwable e) {
		    	 System.out.println("Artifact extraction failed for : " + metadata.getName() + " " + e.getClass().toString() + " : " + e.getMessage());		               
		     }
		 }
	}
	
	/**
	 * Saves only selectable operations meta data to files in the user directory
	 * @param - directory
	 */
	public void saveSchemaFiles(String directory){
		IoResourseUtil ioProcessor = new IoResourseUtil();
		for(InboundMetadataModel model : this.getOnlySelectable()){
			String fileName = model.getName().trim()+SCHEMA_FILE_EXT;
			
			String requestName = model.getName();
			Properties prop = new Properties();
			prop.put(ROOT_NAME_REQUEST_KEY, requestName);
			/*
			String responseName = model.getRootNameResponse();
			String inSchemaEnc = model.getInputSchemas();
			String outSchemaEnc = model.getOutputSchemas();
			
			String action = model.getOperation().getAction();
			String tasget = model.getOperation().getTarget();
			String name = model.getOperation().getName();
			
			Properties prop = new Properties();
			
			prop.put(ROOT_NAME_REQUEST_KEY, requestName);
			prop.put(ROOT_NAME_RESPONSE_KEY, responseName);
			prop.put(INPUT_SCHEMA_KEY, inSchemaEnc);
			prop.put(OUTPUT_SCHEMA_KEY, outSchemaEnc);
			prop.put(SERVICE_OPERATION_ACTION_KEY, action);
			prop.put(SERVICE_OPERATION_TARGET_KEY, tasget);
			prop.put(SERVICE_OPERATION_NAME_KEY, name);
			*/
			if(directory==null || directory.isEmpty()){
				ioProcessor.writePropertiesToFile(prop, IoResourseUtil.SHEMA_DIR, fileName);
			}else{
				ioProcessor.writePropertiesToFile(prop, directory, fileName);
			}
		}
	}

}
