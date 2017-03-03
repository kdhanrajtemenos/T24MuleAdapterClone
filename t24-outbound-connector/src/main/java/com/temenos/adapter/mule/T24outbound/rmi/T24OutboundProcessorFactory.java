package com.temenos.adapter.mule.T24outbound.rmi;

import javax.resource.ResourceException;

import com.temenos.adapter.mule.T24outbound.config.T24OutboundConfig;

/**
 * This factory returns different {@link com.temenos.adapter.tibco.t24.outbound.T24OutboundProcessor} instances
 * depending of the server type (TAFC/TAFJ)
 */
public class T24OutboundProcessorFactory {

    public static T24OutboundProcessor createT24OutboundExecutor(T24ConnectionType connectionType, T24RequestSpec requestSpec) throws ResourceException {
    	
    	
    	T24OutboundConfig config = T24OutboundConfig.getInstance();
    	
        if (connectionType == T24ConnectionType.TAFC) {
        	        	
            return new TafcOutboundProcessor(config.getTafcRuntimeConfiguration(), requestSpec);
            
        } else if (connectionType == T24ConnectionType.TAFJ) {
            
        	return new TafjOutboundProcessor(config.getTafjRuntimeConfiguration(), requestSpec);
        }
        throw new ResourceException("Unknown connection type " + connectionType); 
    }
}
