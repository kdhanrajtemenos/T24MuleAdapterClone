package com.temenos.adapter.mule.T24inbound.connector.t24xa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleRuntimeException;
import org.mule.api.transaction.Transaction;
import org.mule.config.i18n.CoreMessages;
import org.mule.transaction.TransactionCoordination;

import com.temenos.adapter.mule.T24inbound.connector.config.AbstractConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.rmi.EventPollingService;

public class T24xaUtils {
	
	private static final Log logger = LogFactory.getLog(T24xaUtils.class);
	
	public static final String TRANSACTION_RSOURSE_TYPE = "jta.inject";
	
	
	@SuppressWarnings("unchecked")
	static public <T> T getTransactionalResource(T24xaResourse jtaResourse, AbstractConnectorConfig config){
		
		Transaction currentTx = TransactionCoordination.getInstance().getTransaction();
		
		if(currentTx != null){
			
			if (logger.isDebugEnabled()) {
				logger.debug("Transaction in scope: " + currentTx);
			}
			
			if (currentTx.hasResource(jtaResourse)){
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction already bound to "+ jtaResourse.toString());
				}
				return (T) currentTx.getResource(jtaResourse);
			} else {
				
				
				EventPollingService eventpolling = EventPollingService.getInstance(config);
				
				Object connectionResource = new T24xaResourse(eventpolling.getService(eventpolling.getEventType(), eventpolling.getEventCount()), eventpolling.getData());
				
				
				
				/*
				try {
					if ((new JTAUtils()).supports(currentTx, TRANSACTION_RSOURSE_TYPE, connectionResource)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Binding new JTAResourse " + connectionResource + " to transaction " + currentTx);
						}
						currentTx.bindResource(TRANSACTION_RSOURSE_TYPE, connectionResource);
					} else {
						throw new TransactionException(CoreMessages.createStaticMessage("Endpoint is transactional but transaction does not support it"));
					}
				} catch (MuleException ex) {
					throw new JTAException(ex);
				}
				*/
				return (T) connectionResource;
				
			}
			
		}else{
			EventPollingService eventpolling = EventPollingService.getInstance(config);
			
			Object connectionResource = new T24xaResourse(eventpolling.getService(eventpolling.getEventType(), eventpolling.getEventCount()), eventpolling.getData());
			
			if (logger.isDebugEnabled()) {
				logger.debug("Returning non-transactional entityManager " + connectionResource.toString());
			}
			return (T) connectionResource;
		}
	
	}
	
	
    public boolean supports(Transaction currentTx, Object key, Object resource)
    {
    	//boolean conditionOne  = (getKeyType().isAssignableFrom(key.getClass()) && getResourceType().isAssignableFrom(resource.getClass()));
    	
    	Object one = currentTx.getResource(key);
    	
    	boolean conditionTwo  = (key != null && (one == resource));
    	
        return conditionTwo;
    }

    protected Class<?> getResourceType()
    {
        throw new MuleRuntimeException(CoreMessages.createStaticMessage("Transaction type: " + this.getClass().getName() + " doesn't support supports(..) method"));
    }

    protected Class<?> getKeyType()
    {
        throw new MuleRuntimeException(CoreMessages.createStaticMessage("Transaction type: " + this.getClass().getName() + " doesn't support supports(..) method"));
    }
	
	
}
