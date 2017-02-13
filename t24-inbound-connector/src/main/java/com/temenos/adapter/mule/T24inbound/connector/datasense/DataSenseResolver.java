package com.temenos.adapter.mule.T24inbound.connector.datasense;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.MetaDataKeyRetriever;
import org.mule.api.annotations.MetaDataRetriever;
import org.mule.api.annotations.components.MetaDataCategory;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.DynamicObjectBuilder;
import org.mule.common.metadata.datatype.DataType;

import com.temenos.adapter.mule.T24inbound.connector.T24InboundConnector;
import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.metadata.extract.T24InboundDesignTimeMetaDataExtractor;

@MetaDataCategory
public class DataSenseResolver {
	
	@Inject
	private T24InboundConnector connector;
	
	
	private List<MetaDataKey> mdKeys;


	/**
     * Retrieves the list of keys. Should retrieve List of operations/enquiries/Schemas
     * and use them as keys...
     */
	/**
     * Retrieves the list of keys. Should retrieve List of operations/enquiries/Schemas
     * and use them as keys...
     */
    @MetaDataKeyRetriever
    public List<MetaDataKey> getMetaDataKeys() throws ConnectionException{
    	mdKeys = new ArrayList<MetaDataKey>();
    	
    	/* Check the connector state */
    	if(connector != null){
    		ConnectorConfig config = connector.getConfig();
    		if(config==null ){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "301", "Connector config is unavailable!");
    		}
    		
    		config.initIntegrationServiceFlow(config.getServiseURL());
    		
    		
    		if(!config.isConnected()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "302", "Connector is not connected. Can't get MetaDataKeys!");
    		} 
    		
    		//DirectoryPickUp directoryPicker =  new DirectoryPickUp();
    		
    		
    	
    		T24InboundDesignTimeMetaDataExtractor extractor = new T24InboundDesignTimeMetaDataExtractor(config);
    		
    		List<String> serviceNames = null;
    		try{
    			serviceNames = extractor.getEventsNames();
    			
    			
    		}catch(RuntimeException e){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "303", "Connector can't extract service names!");
    		}
    		
    	
    		String xsdDirectory =  config.getSettingsFolder();
    		if(xsdDirectory==null || xsdDirectory.isEmpty()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "308", "User directory is null or empty");
    		}
    		
    		//String xsdDirectory = DirectoryPickUp.getDir();
			extractor.saveSchemaFiles(xsdDirectory); 
			
    		if(serviceNames == null || serviceNames.isEmpty()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "304", "Connector can't extract enquery names!");
    		}else{
    			for(String service_name_id : serviceNames){
    				DefaultMetaDataKey mdkey = new DefaultMetaDataKey(service_name_id, service_name_id);
    				mdKeys.add(mdkey);
    			}
    		}
    		
    		
    		
    	}else{
    		throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "305", "Connector unavailable");
    	}
    	
    	return mdKeys;
    }
    
    /**
     * Get MetaData given the Key the user selects
     * 
     * @param key The key selected from the list of valid keys
     * @return The MetaData model of that corresponds to the key
     * @throws Exception If anything fails
     */
    @MetaDataRetriever
    public MetaData getMetaData(MetaDataKey key) throws Exception {
    	DefaultMetaDataBuilder builder = new DefaultMetaDataBuilder();
    	DynamicObjectBuilder<?> dynamicObject = builder.createDynamicObject(key.getId());
    	
    	if(mdKeys != null && mdKeys.contains(key)){
    		String keyId =  key.getId();
    		dynamicObject.addSimpleField(keyId, DataType.STRING);
    	}

        MetaDataModel model = builder.build();
        MetaData metaData = new DefaultMetaData(model);
    	return metaData;
    }
    
	public T24InboundConnector getConnector() {
		return connector;
	}

	public void setConnector(T24InboundConnector connector) {
		this.connector = connector;
	}
}
