package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import java.util.Properties;

import com.temenos.adapter.mule.T24inbound.connector.config.AbstractConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24inbound.connector.config.TAFCConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.TAFJConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataModel;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.Metadata;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;
//import com.temenos.adapter.common.metadata.ServiceOperation;
//import com.temenos.adapter.common.metadata.outbound.T24ServiceOperationImpl;
//import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.tocf.common.Base64;

public class T24BaseInboundMetadataExctractor {
	/* Property files keys */
	public static final String ROOT_NAME_REQUEST_KEY = "rootNameRequest";
	public static final String ROOT_NAME_RESPONSE_KEY = "rootNameRespons";
	
	public static final String INPUT_SCHEMA_KEY = "inputSchemas";
	public static final String OUTPUT_SCHEMA_KEY = "outputSchemas";
	
	public static final String SERVICE_OPERATION_ACTION_KEY = "serviceOperationAction";
	public static final String SERVICE_OPERATION_TARGET_KEY = "serviceOperationTarget";
	public static final String SERVICE_OPERATION_NAME_KEY = "serviceOperationName";
	
	public static final String MAIN_FOLDER_NAME = "Schema";
	public static final String ADAPTER_FOLDER_NAME = "Inbound";
	public static final String METADATA_FOLDER_NAME = "Metadata";
	public static final String INPUT_SCHEMA_FOLDER_NAME = "Input";
	public static final String OUTPUT_SCHEMA_FOLDER_NAME = "Output";
	
	public static final String INPUT_FILE_SHEMA_PREFIX = "In ";
	public static final String OUTPUT_FILE_SHEMA_PREFIX = "Out ";
	
	/* Property file extension */
	public static final String METADATA_FILE_EXT = ".md";
	public static final String SCHEMA_FILE_EXT = ".xsd";
	
	/* T24 metadata discovery API*/
	private T24InboundAPICall inboundCall;
	
	/* Injected Connector configuration */
	private AbstractConnectorConfig config;
	
	/* The metadata tree*/
	private Collection<Metadata> eventsCollection;
	
	/* Only selectable operations from the metadata tree */
	private List<InboundMetadataModel> onlySelectable = new ArrayList<InboundMetadataModel>();
	
	/* debug flag */
	private boolean debug;

	public T24BaseInboundMetadataExctractor(AbstractConnectorConfig config2) {
		this.config = config2;
		initMetaDataExtractor();
	}

	public T24InboundAPICall getInboundCall() {
		return inboundCall;
	}

	public void setInboundCall(T24InboundAPICall inboundCall) {
		this.inboundCall = inboundCall;
	}

	public AbstractConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(AbstractConnectorConfig config) {
		this.config = config;
	}

	public Collection<Metadata> getEventsCollection() {
		return eventsCollection;
	}

	public void setEventsCollection(Collection<Metadata> eventsCollection) {
		this.eventsCollection = eventsCollection;
	}

	public List<InboundMetadataModel> getOnlySelectable() {
		return onlySelectable;
	}

	public void setOnlySelectable(List<InboundMetadataModel> onlySelectable) {
		this.onlySelectable = onlySelectable;
	}
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	private void initMetaDataExtractor()  {
		if (RuntimeConfigSelector.TAFC == config.getRunTime()) {
			TAFCConnectorConfig tafcConnectorConfig = (TAFCConnectorConfig) config;
			inboundCall = new TAFCInboundAgent(tafcConnectorConfig.getAgentHost(), tafcConnectorConfig.getAgentPort(), null);
		} else if (RuntimeConfigSelector.TAFJ == config.getRunTime()) {
			TAFJConnectorConfig tafjConnectorConfig = (TAFJConnectorConfig) config;
			IntegrationFlowServiceWSClient client = tafjConnectorConfig.getClient();
			inboundCall = new TAFJInboundAgent(tafjConnectorConfig.getUserWsDeatils().getUser().getValue(),
					tafjConnectorConfig.getUserWsDeatils().getPassword().getValue(), client);
		}


	}
	
	/**
	 * Converts metadata artifacts to T24ServiceXmlMetadataImpl object from existing ServiceXMLMetadataModel including decoding schemas
	 * @param model - ServiceXMLMetadataModel
	 * @return
	 */
	/*
	public  T24ServiceXmlMetadataImpl convertToT24ServiceXmlMetadataImpl(InboundMetadataModel model){
		String requestName = model.getRootNameRequest();
		String responseName = model.getRootNameResponse();
		
		List<String> inputSchemas = decodeSchemaString(model.getInputSchemas());
	
		List<String> outputSchemas = decodeSchemaString(model.getOutputSchemas());
		
		T24ServiceXmlMetadataImpl xmlMetaData   = new T24ServiceXmlMetadataImpl(model.getOperation(), requestName, responseName, inputSchemas, outputSchemas);
		return xmlMetaData;
	}
	*/
	
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
	/*
	public T24ServiceXmlMetadataImpl getModelArtifactsFromProperties(Properties prop){
		String requestName = prop.getProperty(ROOT_NAME_REQUEST_KEY);
		String responseName = prop.getProperty(ROOT_NAME_RESPONSE_KEY);
		String inSchemaEnc =prop.getProperty(INPUT_SCHEMA_KEY);
		String outSchemaEnc =prop.getProperty(OUTPUT_SCHEMA_KEY);
		String operationAction = prop.getProperty(SERVICE_OPERATION_ACTION_KEY);
		String operationTarget =prop.getProperty(SERVICE_OPERATION_TARGET_KEY);
		String operationName = prop.getProperty(SERVICE_OPERATION_NAME_KEY);
		
		List<String> inputSchemas = decodeSchemaString(inSchemaEnc);

		List<String> outputSchemas =decodeSchemaString(outSchemaEnc);
				
		ServiceOperation operation = new T24ServiceOperationImpl(operationName, operationTarget, operationAction);
		T24ServiceXmlMetadataImpl xmlMetaData = new T24ServiceXmlMetadataImpl(operation, requestName, responseName, inputSchemas, outputSchemas);
		return xmlMetaData;
	}
	*/
	
	/**
	 * Prints xmlMetaData contents to console. Useful in UNit testing
	 * @param xmlMetaData - T24ServiceXmlMetadataImpl
	 */
	/*
	public void dumpResult(T24ServiceXmlMetadataImpl xmlMetaData){
		String req = xmlMetaData.getRequestRootElement();
		String resp = xmlMetaData.getResponseRootElement();
		String in_schema = xmlMetaData.getRequestSchemaDocuments().toString();
		String out_schema = xmlMetaData.getResponseSchemaDocuments().toString();
		String target = xmlMetaData.getServiceOperation().getTarget();
		String action = xmlMetaData.getServiceOperation().getAction();
		String name = xmlMetaData.getServiceOperation().getName();
		System.out.println("RequestRootElement: " + req);
		System.out.println("ResponseRootElement: " + resp);
		System.out.println("Request schema: " + in_schema);
		System.out.println("Responce schema: " + out_schema);
		System.out.println("Target: " + target);
		System.out.println("Action: " + action);
		System.out.println("Name: " + name);
	}
	*/
}
