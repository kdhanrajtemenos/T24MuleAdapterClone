package com.temenos.adapter.mule.T24outbound.datasense;


import java.io.IOException;
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

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.connector.T24OutboundConnector;
import com.temenos.adapter.mule.T24outbound.metadata.extract.T24OutboundDesignTimeMetaDataExtractor;


@MetaDataCategory
public class DataSenseResolver {
	
	/**
     * If you have a service that describes the entities, you may want to use
     * that through the connector. Development kit will inject the connector, after
     * initializing it.
     */
    @Inject
    private T24OutboundConnector connector;
    
    
    private List<MetaDataKey> mdKeys;
    
    
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
    		
    		config.initIntegrationServiceLandscape(config.getServiseURL());
    		
    		
    		if(!config.isConnected()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "302", "Connector is not connected. Can't get MetaDataKeys!");
    		} 
    		
    	
    		T24OutboundDesignTimeMetaDataExtractor extractor = new T24OutboundDesignTimeMetaDataExtractor(config);
    		
    		List<String> serviceNames = null;
    		try{
    			serviceNames = extractor.getServiceNames();
    			
    			
    		}catch(RuntimeException e){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "303", "Connector can't extract service names!");
    		}
    		
    	
    		String xsdDirectory =  config.getSettingsFolder();
    		/*
    		if(xsdDirectory==null || xsdDirectory.isEmpty()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "308", "User directory is null or empty");
    		}
    		*/
			try {
				extractor.saveMetaDataFiles(xsdDirectory);
			} catch (IOException e) {
				//throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "308", "Canot write metadata");
			}finally{
				
			}
			
    		if(serviceNames == null || serviceNames.isEmpty()){
    			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "304", "Connector can't extract enquery names!");
    		}else{
    			for(String service_name_id : serviceNames){
    				String service_friendly_name = service_name_id.toLowerCase();
    				DefaultMetaDataKey mdkey = new DefaultMetaDataKey(service_name_id, service_friendly_name);
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
    

    
    /**
     * Getter and setter for the given connector
     */
	public T24OutboundConnector getConnector() {
		return connector;
	}

	public void setConnector(T24OutboundConnector connector) {
		this.connector = connector;
	}
    
}
