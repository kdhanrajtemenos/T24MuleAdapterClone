package com.temenos.adapter.mule.T24outbound.metadata.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;



import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.metadata.extract.T24OutboundAPICall;
import com.temenos.adapter.mule.T24outbound.metadata.model.Metadata;
import com.temenos.adapter.mule.T24outbound.metadata.model.ServiceXMLMetadataModel;
import com.temenos.adapter.mule.T24outbound.utils.IoResourceUtil;

import com.temenos.tocf.common.Base64;


public class T24OutboundDesignTimeMetaDataExtractor extends T24BaseOutboundMetadataExctractor {


	/**
	 * Constructor
	 * @param config - inject the connector configuration
	 */
	public T24OutboundDesignTimeMetaDataExtractor(ConnectorConfig config){
		super(config);

	}
	
	/**
	 * Constructor
	 * @param config 
	 */
	public T24OutboundDesignTimeMetaDataExtractor(){
		super(null);
	}
	

	/**
	 * Gets all selectable operation service names as List
	 */
	public List<String> getServiceNames() throws RuntimeException {
		Collection<Metadata> mdCollection = null;
		T24OutboundAPICall apicall = getOutboundCall();
		try {
			mdCollection = apicall.getT24Metadata(null);
					

	    } catch (Exception e) {
	        throw new  RuntimeException("ERROR retreiveing Servicex collection :" + e.getMessage());
	        
	    }
		setServicesCollection(mdCollection);
	    for (Metadata metadata : mdCollection) {
	    	extractServiceNames(metadata, apicall);
	    }
	   
		return  getNames();
	}
	
	
	private List<String> getNames(){
		List<String> result = new ArrayList<String>();		
		for(ServiceXMLMetadataModel model : getOnlySelectable()){
			result.add(model.getName());
		}		
		return result;
	}

	/**
	 * Gets meta data artifacts for a given operation by its name either from a file or selectable list if it is not empty
	 * @param byName - operation name
	 * @return
	 */
	public  T24ServiceXmlMetadataImpl getModelArtifacts(String byName, String location){
		if(!getOnlySelectable().isEmpty()){
			for(ServiceXMLMetadataModel selectable : getOnlySelectable()){
				if(selectable.getName().equals(byName)){
					return convertToT24ServiceXmlMetadataImpl(selectable);
				}
			}
		}else{
			String filename = byName + METADATA_FILE_EXT;
			System.out.println(filename);
			IoResourceUtil ioProcessor = new IoResourceUtil();
			Properties prop = ioProcessor.readResourseFile(filename, location, IoResourceUtil.LOAD_SCHEMA);
			return getModelArtifactsFromProperties(prop);
		}
		
		return null;
	}
	
		
	/**
	 * Meta data tree recursion builder
	 * @param metadata - Meta data
	 * @param call - T24APICall 
	 */
	private void extractServiceNames(Metadata metadata, T24OutboundAPICall call){		
		if (!metadata.isSelectable()) {
			for (Metadata m : metadata.getChildren()){
				   extractServiceNames(m, call);
			}
		} else {
		    try {	        
		         ServiceXMLMetadataModel model = call.getT24ServiceArtefacts(metadata);
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
	public void saveMetaDataFiles(String directory) throws IOException {

		IoResourceUtil ioProcessor = new IoResourceUtil();

		String fullPath = ioProcessor.createDirectory(directory, "Schema");
		//fullPath = ioProcessor.createDirectory(fullPath, "Schema"); 
		fullPath = ioProcessor.createDirectory(fullPath, FOLDER_TYPE); 
		
		String metadataRequestFolder = ioProcessor.createDirectory(fullPath, METADATA_FOLDER_NAME); //api/Schemas/Metadata

		String inSchemaFolder = ioProcessor.createDirectory(fullPath, INPUT_SCHEMA_FOLDER_NAME); //api/Schemas/Input
		
		String outSchemaFolder = ioProcessor.createDirectory(fullPath, OUTPUT_SCHEMA_FOLDER_NAME); //api/Schemas/Output
		
		for(ServiceXMLMetadataModel model : this.getOnlySelectable()){

			Properties prop = new Properties();	
			prop.put(ROOT_NAME_REQUEST_KEY, model.getRootNameRequest());
			prop.put(ROOT_NAME_RESPONSE_KEY,  model.getRootNameResponse());
			prop.put(SERVICE_OPERATION_ACTION_KEY, model.getOperation().getAction());
			prop.put(SERVICE_OPERATION_TARGET_KEY, model.getOperation().getTarget());
			prop.put(SERVICE_OPERATION_NAME_KEY, model.getOperation().getName());
					
			String inSchemaEnc =  new String(Base64.decode(model.getInputSchemas()));
			String outSchemaEnc =  new String(Base64.decode(model.getOutputSchemas()));
				
			String fileName = model.getName() + METADATA_FILE_EXT;
			
			String inSchemaFile = inSchemaFolder +  File.separatorChar + INPUT_FILE_SHEMA_PREFIX +  model.getName() + SCHEMA_FILE_EXT;
			
			String outSchemaFile = outSchemaFolder + File.separatorChar + OUTPUT_FILE_SHEMA_PREFIX + model.getName() + SCHEMA_FILE_EXT;
				
			try {
				ioProcessor.writePropertiesToFile(prop, metadataRequestFolder, fileName);
				ioProcessor.writeSchemas(inSchemaFile, inSchemaEnc);
				ioProcessor.writeSchemas(outSchemaFile, outSchemaEnc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
