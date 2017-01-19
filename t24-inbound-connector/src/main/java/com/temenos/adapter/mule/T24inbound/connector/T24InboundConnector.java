package com.temenos.adapter.mule.T24inbound.connector;



import java.util.ArrayList;
import java.util.Date;

//import java.util.ArrayList;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


//import javax.transaction.TransactionManager;



import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;

//import org.mule.api.MuleEvent;
//import org.mule.api.MuleEvent;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceStrategy;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.callback.SourceCallback;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.notification.TransactionNotificationListener;

import org.mule.api.registry.RegistrationException;
import org.mule.api.transaction.TransactionException;

import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.context.notification.ExceptionNotification;

import org.mule.context.notification.TransactionNotification;

import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.inbound.T24Event;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingException;
//import com.temenos.adapter.common.runtime.inbound.T24EventPollingService;
import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.datasense.DataSenseResolver;
import com.temenos.adapter.mule.T24inbound.connector.rmi.EventPollingService;
import com.temenos.adapter.mule.T24inbound.connector.t24xa.T24xaResourse;
import com.temenos.adapter.mule.T24inbound.connector.t24xa.T24xaTransactionFactory;
import com.temenos.adapter.mule.T24inbound.connector.t24xa.T24xaUtils;
import com.temenos.adapter.mule.T24inbound.connector.transaction.TransactonNotifiactionWrapper;



@Connector(name="t24-inbound", friendlyName="T24Inbound")
@MetaDataScope( DataSenseResolver.class )
public class T24InboundConnector {

    protected final transient Log log = LogFactory.getLog(getClass());
	
	
    @Config
    ConnectorConfig config;

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
   // private InboundMetadataModel dummyModel;
    
    private EventPollingService pollService = null;


	@Inject
    private MuleContext muleContext;

    
    @PostConstruct
    public void init() throws Exception{
    	
    	//**
       	muleContext.getTransactionFactoryManager().registerTransactionFactory(T24xaResourse.class, new T24xaTransactionFactory());
       	
    	try{
    		//pollService = EventPollingService.getInstance(config);
    		
    		//t24xaResource = new T24xaResourse(pollService.getService(eventType, 1), pollService.getData());
    		
    		pollService = EventPollingService.getInstance(config);
    		
    		//t24xaResource = new T24xaResourse();
    		
        	//muleContext.getRegistry().registerObject(T24xaUtils.TRANSACTION_RSOURSE_TYPE, t24xaResource);
        } catch (Exception re) {
        	log.warn("XA context injections processor already registered.");
        }
        
    }
    
   
    public void setMuleContext(final MuleContext muleContext) { 
    	this.muleContext = muleContext; 
    	//MuleContext context = eventM.getMuleContext();

    	try {
    		//If this is null we need wrapper object
    		final TransactonNotifiactionWrapper transactonNotifiactionService = new TransactonNotifiactionWrapper();
    		
    		TransactionNotificationListener<TransactionNotification> transactonListener = new TransactionNotificationListener<TransactionNotification>() 
			{
				
				
				// It seems the first notification comes after first invokation of processor method //

				@Override
				public void onNotification(TransactionNotification notif) { // TransactionNotification / ServerNotification
					
					if(pollService ==null){
						System.out.println("Cant'use pooling service transcation within notification ");
						return;
					}
					
					if(transactonNotifiactionService.getService() == null){
						transactonNotifiactionService.setService(pollService);
					}
					
					String actionName = notif.getActionName();
					String transactionId = notif.getTransactionStringId();
					
			    	System.out.println("	TransactionNotification: " + actionName);						
			    	System.out.println("	TransactionStringId: " + transactionId);						
			    	System.out.println("	Timestamp: " + new Date(notif.getTimestamp()));
			    	
					
			    	
			    	if(actionName.equals("begin")){
			    		//transactonNotifiactionService.getService().beginExecution();
					}else if(actionName.equals("commit")){
						//transactonNotifiactionService.getService().commitExecution();
					}else{ 
						//transactonNotifiactionService.getService().rollBackExecution();
						//if it goes to hear for some bad reason, T24 should roll back
						
					}
		
				}

		    };
		    
		    
		    
		    ExceptionNotificationListener<ExceptionNotification> exceptionListner = new ExceptionNotificationListener<ExceptionNotification>(){

				@Override
				public void onNotification(ExceptionNotification notification) {
					if(pollService ==null){
						System.out.println("Cant'use pooling service within exception notification listener!");
						return;
					}
					
					if(transactonNotifiactionService.getService() == null){
						transactonNotifiactionService.setService(pollService);
					}
					
					int actionId =  notification.getAction();
					String actionName  = notification.getActionName();
					
					
					System.out.println("	ExceptionNotification: " + actionName);						
			    	System.out.println("	ExceptionStringId: " + actionId);						
			    	System.out.println("	Timestamp: " + new Date(notification.getTimestamp()));
			    	
					
					Throwable t =  notification.getException();
					
					//muleContext.getTransactionManager().getTransaction().getStatus();
					if( t instanceof Exception ){
						transactonNotifiactionService.getService().rollBackExecution();
					}
					else if( t instanceof RuntimeException ){
						transactonNotifiactionService.getService().rollBackExecution();
					}
					
				}
		    	
		    };
		    
			muleContext.registerListener(transactonListener);  //only Exceception Listener left
			muleContext.registerListener(exceptionListner);
			
    		
		} catch (Exception e) {
			e.printStackTrace();
		}    		

    }
    
    

    //TransactionManager txMngr = null;
    
    T24xaResourse t24xaResource = null;
    
    public static final String SOURCE_MESSAGE_PROCESSOR_PROPERTY_NAME = MuleProperties.ENDPOINT_PROPERTY_PREFIX + "sourceMessageProcessor";
    
    @Processor
    @Summary("This method extracts messages from T24 server")
    public List<T24Event> eventPool(MuleEvent event, @MetaDataKeyParam String eventType, int batchSize) throws ConnectionException {
    	/*
    	//if(t24xaResource == null){
	    	muleContext.getTransactionFactoryManager().registerTransactionFactory(T24xaResourse.class, new T24xaTransactionFactory());
	    	try{
	    		pollService = EventPollingService.getInstance(config);
	    		
	    		t24xaResource = new T24xaResourse(pollService.getService(eventType, 1), pollService.getData());
	    		
	        	muleContext.getRegistry().registerObject(T24xaUtils.TRANSACTION_RSOURSE_TYPE, t24xaResource);
	        } catch (RegistrationException re) {
	        	logger.warn("T24xa context injections processor already registered.");
	        }
    	//}
    	*/
    	
    	//T24xaResourse T24xaResourse = T24xaUtils.getTransactionalResource(t24xaResource, config);
    	
    	
    	
    	List<T24Event> events = new ArrayList<T24Event>();


        	
        	try {
        		
        		pollService = EventPollingService.getInstance(config);
				
        		T24xaResourse t24xaResource = new T24xaResourse(pollService.getService(pollService.getEventType(), pollService.getEventCount()), pollService.getData());
        		
        		T24xaTransactionFactory txFactory = (T24xaTransactionFactory) muleContext.getTransactionFactoryManager().getTransactionFactoryFor(T24xaResourse.class);
        		
        		events = txFactory.executeTransaction(muleContext, t24xaResource);
        		
        		txFactory.commit();
        
			}  catch (TransactionException e) {
				throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH,"111",e.getMessage());
			}
        
    	
        return events;
    }

    /**
     *  Custom Message Source
     *
     *  @param callback The source callback used to dispatch message to the flow
     *  @throws Exception error produced while processing the payload
     */
    @Source(sourceStrategy = SourceStrategy.POLLING,pollingPeriod=5000)
    @Summary("This method extracts messages from T24 server with build in pooling")
    public void getMessage(SourceCallback callback,  String eventType, int batchSize) throws ConnectionException {


	    log.info("Enter @Source callback method getMessage for event name: " + eventType);
	    EventPollingService pollService = null;
	    String response = "";
	    List<T24Event> events;
	    T24xaResourse t24xaResource;
	    T24xaTransactionFactory txFactory = null;
    	
    	try {

	    	pollService = EventPollingService.getInstance(config);
	    	
	    	
	    	try {
	    	
	    		batchSize = (batchSize < 1) ? 1 : batchSize;

        		t24xaResource = new T24xaResourse(pollService.getService(pollService.getEventType(), batchSize), pollService.getData());
        		
        		txFactory = (T24xaTransactionFactory) muleContext.getTransactionFactoryManager().getTransactionFactoryFor(T24xaResourse.class);
        		
        		events = txFactory.executeTransaction(muleContext, t24xaResource);
        			    				
		    	log.info("T24 extracted messages batch size: " + events.size());
		    	
			    for (T24Event event : events) {
			    	response += event.getData()+"\r\n";
			    	
				    log.info("Process event with ID: " + event.getId());
				    if (log.isDebugEnabled()) {
					    log.debug("Process event with content: " + event.getData());
				    }
				    
		    		callback.process(event.getData());
				    log.info("Finish processing event with ID: " + event.getId());
			    }
			    
			    if (null != txFactory) {
	        		txFactory.commit();
			    }
			    
			    System.out.println("Result: " + response);

			} catch (T24EventPollingException e) {
			    log.error("T24EventPollingException in event processing");
			    log.trace("T24EventPollingException message in event processing: " + e.getMessage());
			    handleExceptionConsequence(e, txFactory);
				throw new RuntimeException("T24 error: " + e.getMessage());
			} catch (T24RuntimeException e) {
			    handleExceptionConsequence(e, txFactory);
				throw new RuntimeException("T24 error: " + e.getMessage());
			}
			
			

        	Transaction transaction = null;
        	MuleContext context = muleContext;
        	TransactionManager mgr = context.getTransactionManager();
        	
        	try {
        	
	        	if(null != mgr) {
	        		transaction = mgr.getTransaction();
	        		
	        		if(null != transaction) {
	        			System.out.println("Current transaction: " + transaction.toString());
	        			System.out.println("  transaction class: " + transaction.getClass().getName());
	        			System.out.println("  transaction hcode: " + transaction.hashCode());
	    				System.out.println("  transaction status: " + transaction.getStatus());
	        			//activeTransaction = transaction;
	        			
	        			// try to rollback the transaction        			
//	    				System.out.println(" before transaction status: " + transaction.getStatus());
//	    				transaction.rollback();
//	    				System.out.println(" after transaction status: " + transaction.getStatus());
	        			
	        		} else {
	        			System.out.println("Current transaction is null");
	        		}
	            	
	        	} else {
	    			System.out.println("Current transaction manager is null");
	        	}
        	} catch (SystemException ex) {
    			System.out.println("Cannot get transaction status. SystemException: " + ex.getMessage());
        		
        	}

    	
    	}catch(Exception e){
    		
    		
    		throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST,"112", "Source processing exception! Name: "+ e.getMessage());
    	}
        
    }

    private void handleExceptionConsequence(Throwable e, T24xaTransactionFactory inboundProcessor) {
        log.error("Error while processing inbound messages", e);
        try {
            if (inboundProcessor != null) {
                inboundProcessor.rollback();
            }
        } catch (Exception f) {
            log.error("Error trying to rollback the transaction", e);
        }
    }
}