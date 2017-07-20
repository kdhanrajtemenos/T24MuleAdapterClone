package com.temenos.adapter.mule.T24outbound.metadata.extract;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.temenos.adapter.common.metadata.ServiceOperation;
import com.temenos.adapter.common.metadata.outbound.T24ServiceOperationImpl;
import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24outbound.metadata.model.Metadata;
import com.temenos.adapter.mule.T24outbound.metadata.model.ServiceXMLMetadataModel;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;
import com.temenos.adapter.mule.T24outbound.utils.IoResourceUtil;
import com.temenos.tocf.common.Base64;

public class T24BaseOutboundMetadataExctractor {

	/* Property files keys */
	public static final String ROOT_NAME_REQUEST_KEY = "rootNameRequest";
	public static final String ROOT_NAME_RESPONSE_KEY = "rootNameRespons";
	
	public static final String INPUT_SCHEMA_KEY = "inputSchemas";
	public static final String OUTPUT_SCHEMA_KEY = "outputSchemas";
	
	public static final String SERVICE_OPERATION_ACTION_KEY = "serviceOperationAction";
	public static final String SERVICE_OPERATION_TARGET_KEY = "serviceOperationTarget";
	public static final String SERVICE_OPERATION_NAME_KEY = "serviceOperationName";
	
	public static final String FOLDER_TYPE = "Outbound";

	public static final String METADATA_FOLDER_NAME = "Metadata";
	public static final String INPUT_SCHEMA_FOLDER_NAME =  "Input";
	public static final String OUTPUT_SCHEMA_FOLDER_NAME = "Output";
	
	public static final String INPUT_FILE_SHEMA_PREFIX = "In ";
	public static final String OUTPUT_FILE_SHEMA_PREFIX = "Out ";
	
	/* Property file extension */
	public static final String METADATA_FILE_EXT = ".md";
	public static final String SCHEMA_FILE_EXT = ".xsd";
	
	/* T24 metadata discovery API*/
	private T24OutboundAPICall outboundCall;
	
	/* Injected Connector configuration */
	private ConnectorConfig config;
	
	/* The metadata tree*/
	private Collection<Metadata> servicesCollection;
	
	/* Only selectable operations from the metadata tree */
	private List<ServiceXMLMetadataModel> onlySelectable = new ArrayList<ServiceXMLMetadataModel>();
	
	/* debug flag */
	private boolean debug;

	public T24BaseOutboundMetadataExctractor(ConnectorConfig config2) {
		this.config = config2;
		initMetaDataExtractor();
	}

	public T24OutboundAPICall getOutboundCall() {
		return outboundCall;
	}

	public void setOutboundCall(T24OutboundAPICall outboundCall) {
		this.outboundCall = outboundCall;
	}

	public ConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ConnectorConfig config) {
		this.config = config;
	}

	public Collection<Metadata> getServicesCollection() {
		return servicesCollection;
	}

	public void setServicesCollection(Collection<Metadata> servicesCollection) {
		this.servicesCollection = servicesCollection;
	}

	public List<ServiceXMLMetadataModel> getOnlySelectable() {
		return onlySelectable;
	}

	public void setOnlySelectable(List<ServiceXMLMetadataModel> onlySelectable) {
		this.onlySelectable = onlySelectable;
	}
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	private void initMetaDataExtractor()  {
		IntegrationLandscapeServiceWSclient client = config.getClient();
		
		if(config.getT24RunTime().equals(RuntimeConfigSelector.TAFC)) {
		   outboundCall = new TAFCOutboundAgent(config.getAgentHost(), Integer.parseInt(config.getAgentPort()), client);
	    }else{
	       outboundCall = new TAFJOutboundAgent( config.getUserWsDeatils().getUser().getValue(), config.getUserWsDeatils().getPassword().getValue(), client);
	    }
	}
	
	/**
	 * Converts metadata artifacts to T24ServiceXmlMetadataImpl object from existing ServiceXMLMetadataModel including decoding schemas
	 * @param model - ServiceXMLMetadataModel
	 * @return
	 */
	public  T24ServiceXmlMetadataImpl convertToT24ServiceXmlMetadataImpl(ServiceXMLMetadataModel model){
		String requestName = model.getRootNameRequest();
		String responseName = model.getRootNameResponse();
		
		List<String> inputSchemas = decodeSchemaString(model.getInputSchemas());
	
		List<String> outputSchemas = decodeSchemaString(model.getOutputSchemas());
		
		T24ServiceXmlMetadataImpl xmlMetaData   = new T24ServiceXmlMetadataImpl(model.getOperation(), requestName, responseName, inputSchemas, outputSchemas);
		return xmlMetaData;
	}
	
	/**
	 * Decodes schema and put it into a List object at position 0
	 * @param schemaString - base64 encoded schema 
	 */
	protected static List<String> decodeSchemaString(String schemaString){
		List<String> schema = new ArrayList<String>();
		String decoded =  new String(Base64.decode(schemaString.getBytes()));
		schema.add(decoded);
		return schema;
	}
	
	/**
	 * Reads T24ServiceXmlMetadataImpl object from existing properties object
	 * @param prop - Properties
	 * @return
	 */
	public T24ServiceXmlMetadataImpl getModelArtifactsFromProperties(Properties prop){
		String requestName = prop.getProperty(ROOT_NAME_REQUEST_KEY);
		String responseName = prop.getProperty(ROOT_NAME_RESPONSE_KEY);
		String inSchema =prop.getProperty(INPUT_SCHEMA_KEY);
		String outSchema=prop.getProperty(OUTPUT_SCHEMA_KEY);
		String operationAction = prop.getProperty(SERVICE_OPERATION_ACTION_KEY);
		String operationTarget =prop.getProperty(SERVICE_OPERATION_TARGET_KEY);
		String operationName = prop.getProperty(SERVICE_OPERATION_NAME_KEY);
		
		List<String> inputSchemas = new ArrayList<String>();
		inputSchemas.add(inSchema);
		
		List<String> outputSchemas = new ArrayList<String>();
		outputSchemas.add(outSchema);
		
		ServiceOperation operation = new T24ServiceOperationImpl(operationName, operationTarget, operationAction);
		T24ServiceXmlMetadataImpl xmlMetaData = new T24ServiceXmlMetadataImpl(operation, requestName, responseName, inputSchemas, outputSchemas);
		return xmlMetaData;
	}
	
	/**
	 * this method reads from file
	 * @param String Service name
	 * @param String Main folder location
	 * */
	public  T24ServiceXmlMetadataImpl getModelArtifactsFromFile(String serviceName, String location){
		
		location += File.separatorChar + "Schema" + File.separatorChar + FOLDER_TYPE + File.separatorChar ;
		

		String metadataRequestFolder = location + METADATA_FOLDER_NAME + File.separatorChar; //api/Schemas/Metadata

		String inSchemaFolder =  location + INPUT_SCHEMA_FOLDER_NAME +  File.separatorChar; //api/Schemas/Input
		
		String outSchemaFolder = location +  OUTPUT_SCHEMA_FOLDER_NAME + File.separatorChar; //api/Schemas/Output
		
		
		String metaDataFile = serviceName + T24BaseOutboundMetadataExctractor.METADATA_FILE_EXT;
		
		String schemaFile = serviceName + T24BaseOutboundMetadataExctractor.SCHEMA_FILE_EXT;
		
		IoResourceUtil ioProcessor = new IoResourceUtil();
		Properties prop = ioProcessor.readResourseFile(metaDataFile, metadataRequestFolder, IoResourceUtil.LOAD_SCHEMA);
		
		String inputSchema =  ioProcessor.readSchemaFile(INPUT_FILE_SHEMA_PREFIX + schemaFile, inSchemaFolder);
		
		String outputSchema =  ioProcessor.readSchemaFile(OUTPUT_FILE_SHEMA_PREFIX + schemaFile, outSchemaFolder);
		
		prop.put(INPUT_SCHEMA_KEY, inputSchema);
		prop.put(OUTPUT_SCHEMA_KEY, outputSchema);
		
		System.out.println("METADATA RESOLVED");
		return getModelArtifactsFromProperties(prop); //additiona parameters will  be radded
	}


}
