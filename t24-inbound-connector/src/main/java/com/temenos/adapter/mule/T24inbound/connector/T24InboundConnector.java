package com.temenos.adapter.mule.T24inbound.connector;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.SingleResourceTransactionFactoryManager;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceStrategy;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.callback.SourceCallback;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.api.context.notification.TransactionNotificationListener;
import org.mule.context.notification.ExceptionNotification;
import org.mule.context.notification.TransactionNotification;

import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.inbound.T24Event;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingException;
import com.temenos.adapter.mule.T24inbound.connector.config.AbstractConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24inbound.connector.datasense.DataSenseResolver;
import com.temenos.adapter.mule.T24inbound.connector.rmi.EventPollingService;
import com.temenos.adapter.mule.T24inbound.connector.t24xa.T24xaResourse;
import com.temenos.adapter.mule.T24inbound.connector.t24xa.T24xaTransactionFactory;
import com.temenos.adapter.mule.T24inbound.connector.transaction.TransactonNotifiactionWrapper;



@Connector(name="t24-inbound", friendlyName="T24Inbound")
@MetaDataScope( DataSenseResolver.class )
public class T24InboundConnector {

    protected final Log log = LogFactory.getLog(getClass());
	
	
    @Config
    AbstractConnectorConfig config;

    public AbstractConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(AbstractConnectorConfig config) {
        this.config = config;
    }
    
    private EventPollingService pollService = null;

    T24xaResourse t24xaResource = null;

	@Inject
    private MuleContext muleContext;

    
    @PostConstruct
    public void init() throws Exception{
    	
    	//**
       	muleContext.getTransactionFactoryManager().registerTransactionFactory(T24xaResourse.class, new T24xaTransactionFactory());
       	
    	try {

    		pollService = EventPollingService.getInstance(config);
    		
        } catch (Exception re) {
        	log.warn("XA context injections processor already registered.");
        }
        
    }
    
   
    public void setMuleContext(final MuleContext muleContext) { 
    	
    	this.muleContext = muleContext; 

    	try {
    		//If this is null we need wrapper object
    		final TransactonNotifiactionWrapper transactonNotifiactionService = new TransactonNotifiactionWrapper();
    		
    		TransactionNotificationListener<TransactionNotification> transactonListener = 
    				new TransactionNotificationListener<TransactionNotification>() 

    		{
				
				// It seems the first notification comes after first invocation of processor method //

				@Override
				public void onNotification(TransactionNotification notif) { // TransactionNotification / ServerNotification
					
					if(pollService == null){
						log.info("Can't use pooling service transcation within notification listener");
						return;
					}
					
					if(transactonNotifiactionService.getService() == null){
						transactonNotifiactionService.setService(pollService);
					}
					
					String actionName = notif.getActionName();
					String transactionId = notif.getTransactionStringId();
					
					log.debug("	TransactionNotificationListener TransactionNotification: " + actionName);						
			    	log.debug("	TransactionNotificationListener TransactionStringId: " + transactionId);						
			    	log.debug("	TransactionNotificationListener Timestamp: " + new Date(notif.getTimestamp()));
				}

		    };
		    
		    
		    
		    ExceptionNotificationListener<ExceptionNotification> exceptionListner = new ExceptionNotificationListener<ExceptionNotification>(){

				@Override
				public void onNotification(ExceptionNotification notification) {
					if(pollService ==null){
						log.info("Cant'use pooling service within exception notification listener!");
						return;
					}
					
					if(transactonNotifiactionService.getService() == null){
						transactonNotifiactionService.setService(pollService);
					}
					
					int actionId =  notification.getAction();
					String actionName  = notification.getActionName();
					
					
					log.info("	ExceptionNotificationListener ExceptionNotification: " + actionName);						
					log.info("	ExceptionNotificationListener ExceptionStringId: " + actionId);						
			    	log.info("	ExceptionNotificationListener Timestamp: " + new Date(notification.getTimestamp()));
			    	
				}
		    	
		    };
		    
			muleContext.registerListener(transactonListener);  
			muleContext.registerListener(exceptionListner);
			
    		
		} catch (Exception e) {
			e.printStackTrace();
		}    		

    }
    
    
    public static final String SOURCE_MESSAGE_PROCESSOR_PROPERTY_NAME = MuleProperties.ENDPOINT_PROPERTY_PREFIX + "sourceMessageProcessor";
    
    @Processor
    @Summary("This method extracts messages from T24 server")
    public List<T24Event> eventPool(MuleEvent event, @MetaDataKeyParam String eventType, @Default("1") int batchSize) throws ConnectionException {
    	
    	List<T24Event> events;

	    log.info("Enter @Processor method eventPool for event name: " + eventType + " and batchSize: "+ batchSize);

        	
		pollService = EventPollingService.getInstance(config);
			
		try {
			events = pollService.getT24Events(eventType, batchSize);

			log.info("T24 extracted messages size: " + events.size());
		    for (T24Event item : events) {
			    log.info("Process event with ID: " + item.getId());
			    log.info("Process event with content: " + item.getData());			    
		    }
		    showTransaction(muleContext);
		    

		} catch (RuntimeException | T24RuntimeException | T24EventPollingException e) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111",e.getMessage());
		} 
    	
	    log.info("Exit @Processor method eventPool");

	    return events;
    }


    /**
     *  Source processing of messages to support transaction wrapping and batch processing
     *
     *  @param callback The source callback used to dispatch message to the flow
     *  @throws Exception error produced while processing the payload
     */
    @Source(sourceStrategy = SourceStrategy.POLLING,pollingPeriod=5000)
    @Summary("This method extracts messages from T24 server with build-in pooling in custom transactions scope")
	public void getMessage(SourceCallback callback, String eventType, int batchSize) throws ConnectionException {

		log.info("Enter @Source callback method getMessage for event name: " + eventType + " and batchSize: "
				+ batchSize);
		EventPollingService pollServiceForEvents = null;
		pollServiceForEvents = EventPollingService.getInstance(eventType, batchSize, config);
		List<T24Event> events;
		String response = "";
		
		if (config.getRunTime() == RuntimeConfigSelector.TAFJ) {
		
			T24xaResourse t24xaResourceForPoll;
			T24xaTransactionFactory txFactory = null;

			// fix bad size
			batchSize = (batchSize < 1) ? 1 : batchSize;

			try {
				try {
					pollServiceForEvents.setEventType(eventType);
					pollServiceForEvents.setEventCount(batchSize);

					t24xaResourceForPoll = new T24xaResourse(
							pollServiceForEvents.getService(pollServiceForEvents.getEventType(), batchSize),
							pollServiceForEvents.getData());

					txFactory = (T24xaTransactionFactory) muleContext.getTransactionFactoryManager()
							.getTransactionFactoryFor(T24xaResourse.class);

					events = txFactory.executeTransaction(muleContext, t24xaResourceForPoll);

					response = processEvents(callback, events, response);

					if (null != txFactory) {

						if (log.isDebugEnabled()) {
							log.debug("Commit transaction: " + txFactory.getCurrentTransaction());
						}

						txFactory.commit();
					}

				} catch (T24EventPollingException e) {
					log.error("T24EventPollingException in event processing");
					log.trace("T24EventPollingException message in event processing: " + e.getMessage());
					handleExceptionConsequence(e, txFactory);
					txFactory.rollback();
					// throw new RuntimeException("T24 error: " +
					// e.getMessage());
				} catch (T24RuntimeException e) {
					handleExceptionConsequence(e, txFactory);
					txFactory.rollback();
					// throw new RuntimeException("T24 error: " +
					// e.getMessage());
				}

			} catch (Exception e) {

				log.trace("Result: " + response);
				txFactory.rollback();
				// throw new
				// ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"112",
				// "Source processing exception! Name: "+ e.getMessage());
			}
		} else if (config.getRunTime() == RuntimeConfigSelector.TAFC) {
			try {
				events = pollServiceForEvents.getT24Events(eventType, batchSize);
				processEvents(callback, events, response);
			} catch (RuntimeException runtimeException) {

			} catch (T24EventPollingException t24EventPollingException) {

			} catch (T24RuntimeException t24RuntimeException) {

			} catch (Exception e) {

			}
		}

		log.trace("Result: " + response);
		
		log.info("Exit @Source callback method getMessage");
	}

	private String processEvents(SourceCallback callback, List<T24Event> events, String response) throws Exception {
		log.info("T24 extracted messages size: " + events.size());

		for (T24Event event : events) {
			response += event.getData() + "\r\n";

			log.info("Process event with ID: " + event.getId());
			if (log.isDebugEnabled()) {
				log.debug("Process event with content: " + event.getData());
			}

			callback.process(event.getData());
			log.info("Finish processing event with ID: " + event.getId());

			if (log.isDebugEnabled()) {
				showTransaction(muleContext);
				showTransactionByType(muleContext, T24xaResourse.class);
			}
		}
		return response;
	}
    
    
    private void showTransactionByType(MuleContext context, Class<T24xaResourse> transactionResource) {
    	T24xaTransactionFactory txFactory;
    	
    	SingleResourceTransactionFactoryManager mgr = context.getTransactionFactoryManager();

    	if(null != mgr) {
        	txFactory = (T24xaTransactionFactory) mgr.getTransactionFactoryFor(transactionResource);
    		
    		if(null != txFactory) {

    			log.debug("Current Mule transaction: " + txFactory.toString());

    		} else {
    			log.debug("Current Mule transaction is missing");
    		}
        	
    	} else {
    		log.debug("Current Mule transaction manager is missing");
    	}
    	
    }

    private void showTransaction(MuleContext context) {
    	Transaction transaction = null;
    	TransactionManager mgr = context.getTransactionManager();
    	
    	try {
    	
        	if(null != mgr) {
        		transaction = mgr.getTransaction();
        		
        		if(null != transaction) {
        			
        			if (log.isDebugEnabled()) {
            			log.debug("Current transaction: " + transaction.toString());
            			log.debug("  transaction class: " + transaction.getClass().getName());
            			log.debug("  transaction hcode: " + transaction.hashCode());
            			log.debug("  transaction status: " + transaction.getStatus());
        			}

        		} else {
        			log.debug("Current Mule transaction is missing");
        		}
            	
        	} else {
        		log.debug("Current Mule transaction manager is missing");
        	}
    	} catch (SystemException ex) {
    		log.debug("Cannot get transaction status. SystemException: " + ex.getMessage());
    		
    	}
    	
    }

    private void handleExceptionConsequence(Throwable e, T24xaTransactionFactory inboundProcessor) {
        log.error("Error while processing inbound messages", e);
        try {
            if (inboundProcessor != null) {
                log.debug("Rollback transaction: "+ inboundProcessor.getCurrentTransaction());
                inboundProcessor.rollback();
            }
        } catch (Exception f) {
            log.error("Error trying to rollback the transaction", e);
        }
    }
}