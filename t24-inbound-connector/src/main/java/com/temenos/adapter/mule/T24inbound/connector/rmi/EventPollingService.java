package com.temenos.adapter.mule.T24inbound.connector.rmi;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.adapter.common.conf.T24InvalidConfigurationException;
import com.temenos.adapter.common.conf.T24RuntimeConfiguration;
import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.TafjServerType;
import com.temenos.adapter.common.runtime.inbound.EventPollingData;
import com.temenos.adapter.common.runtime.inbound.T24Event;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingException;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingService;
import com.temenos.adapter.common.runtime.inbound.T24InboundServiceProvider;
import com.temenos.adapter.common.runtime.inbound.T24InboundServiceProviderFactory;
import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;

public class EventPollingService {
	
    protected final transient Log log = LogFactory.getLog(getClass());
	
	private static EventPollingService instance;
	
	//private EventPollingData data;
	
	private T24RuntimeConfiguration configT24 = null;
	
	//private T24InboundServiceProvider provider = null;
	
	//private T24EventPollingService service = null;
	
	private ConnectorConfig config;

	private String eventType;

	private int eventCount;
	
	
	private EventPollingData data;
	
	
	private EventPollingService(ConnectorConfig config){
		this.config = config;
	       
	}
	
	private EventPollingService(String eventType, int eventCount, ConnectorConfig config) {
		this.eventType = eventType;
		this.eventCount = eventCount;
		this.config = config;
	}
	

	/**
	 * This is singleton
	 * @param eventType 
	 * @param eventCount 
	 * @param config
	 * @return
	 */
	public static EventPollingService getInstance(String eventType, int eventCount, ConnectorConfig config){
		
		if (instance == null) {
			synchronized (EventPollingService.class) {
				if (instance == null) {

					instance = new EventPollingService(eventType, eventCount, config).init();
				}
			}
		}
		return instance;
	}
	
	/**
	 * This is singleton
	 * @param config
	 * @return
	 */
	public static EventPollingService getInstance(ConnectorConfig config){
		
		if (instance == null) {
			synchronized (EventPollingService.class) {
				if (instance == null) {

					instance = new EventPollingService(config).init();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Some initialization of the singleton
	 * @param i 
	 * @param eventType 
	 * @param config
	 * @return
	 * @throws RuntimeException
	 */
	private EventPollingService init() throws RuntimeException {
		
		ArrayList<String> hosts = new ArrayList<String>();
	    ArrayList<Integer> ports = new ArrayList<Integer>();
	    ArrayList<String> nodes = new ArrayList<String>();


	    hosts.add(config.getT24Host());
	    ports.add(new Integer(config.getT24Port())); // 5456 from oracle config
	    nodes.add(config.getNodeName());
       
    	try {
			configT24 = TAFJRuntimeConfigurationBuilder.createTAFJRuntimeConfiguration(
			        TafjServerType.JBOSS_7_2.toString(),
			        hosts, ports, nodes, config.getT24User(), 
			        config.getT24Password(), config.getEjbStateful(), "");
		} catch (T24InvalidConfigurationException e) {
			throw new RuntimeException("T24 configuration error: " + e.getMessage());
		}
        
        return this;
	}
	
	
    T24EventPollingService service = null;
	
    
	@SuppressWarnings("deprecation")
	public List<T24Event> getT24Events(String eventType, int i) throws RuntimeException, T24RuntimeException, T24EventPollingException {
    	log.info("Enter EventPollingService.getT24Events for event " + eventType + " size " + i);
	 
		setEventType(eventType);
		setEventCount(i);
		
//		List<String> comp = new ArrayList<String>();
//		comp.add(config.getUserWsDeatils().getCoCode().getValue());
//		this.data = EventPollingData.newInstance(eventType, i, comp);
		this.data = EventPollingData.newInstance(eventType, i);

	    //T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(configT24);
		// use deprecated method to eliminate company name
	    T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(eventType, i, configT24);
	    
		List<T24Event> result = null;
		
		try {
			
			//service = provider.getService(data);
			// use deprecated method to eliminate company name
			service = provider.getService();
	
			service.begin();
			    
			result =  service.execute();

		    service.commit();

		} catch (T24RuntimeException e) {  //thrown by getService(), begin(), commit()
			if (service != null) {
				service.rollback(); 
			}
			throw new RuntimeException("T24 error: " + e.getMessage());

		} catch (T24EventPollingException e) { //thrown by execute()
			if (service != null) {
				service.rollback(); 
			}
			throw new RuntimeException("T24 error: " + e.getMessage());

		}  finally {
			if (service != null) {
				service.cleanUp(); // channel cleanup
			}
		}

	    return result;
	}
	
	@SuppressWarnings("deprecation")
	public T24EventPollingService getService(String eventType, int i) {
		
		setEventType(eventType);
		setEventCount(i);
//		List<String> comp = new ArrayList<String>();
//		comp.add(config.getUserWsDeatils().getCoCode().getValue());
		this.data = EventPollingData.newInstance(eventType, i);
	    	
	    //T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(configT24);
	    T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(eventType, i, configT24);
	    
	    try {
			//service = provider.getService(data);
			service = provider.getService();
		} catch (T24RuntimeException e) {
			e.printStackTrace();
		}
		
		return service;
	}

	public void setService(T24EventPollingService service) {
		this.service = service;
	}


	List<T24Event> result = null;
	
	public List<T24Event> getResult(){
		if(errorExecution==0){
			return result;
		}
		return null;
	}
	
	private int errorExecution;
	
	private boolean readyToCommit = false;

	@SuppressWarnings("deprecation")
	public void beginExecution(String eventType, int i) {
		
		setEventType(eventType);
		setEventCount(i);
		
//		List<String> comp = new ArrayList<String>();
//		comp.add(config.getUserWsDeatils().getCoCode().getValue());
		this.data = EventPollingData.newInstance(eventType, i);

	    //T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(configT24);
	    T24InboundServiceProvider provider = T24InboundServiceProviderFactory.getServiceProvider(eventType, i, configT24);

	    errorExecution = 0;
	    readyToCommit = false;
		
		try{
			
			//service = provider.getService(data);
			service = provider.getService();
			
			//service.begin();
			
			result =  service.execute(data);
			
			System.out.println("Polling finished: " + result.size());
			readyToCommit = true;
		} catch (T24RuntimeException e) {  //thrown by getService(), begin(), commit()
			errorExecution = 1;
			//throw new RuntimeException("T24 transaction begin error: " + e.getMessage());

		} catch (T24EventPollingException e) {
			errorExecution = 2;
			//throw new RuntimeException("T24 transaction execution error: " + e.getMessage());

		}finally{
			try {
				service.rollback();
			} catch (T24RuntimeException e) {
				errorExecution = 3;
				//throw new RuntimeException("T24 transaction rollback error: " + e.getMessage());
			}
		}
		
	}

	public void commitExecution() {
		if(service != null && readyToCommit){
			try {
				service.commit();
			} catch (T24RuntimeException e) {
				errorExecution = 3;
				System.out.println("T24 transaction commit error: " + e.getMessage());
			}finally{
				try {
					if(errorExecution>0){
						service.rollback();
					}
				} catch (T24RuntimeException e) {
					errorExecution = 4;
					System.out.println("T24 transaction rollback error: " + e.getMessage());
				}
				service.cleanUp(); 
			}
		}
	
	}

	public void rollBackExecution() {
		if(service != null && readyToCommit){
			try {
				service.rollback();
			} catch (T24RuntimeException e) {
				errorExecution = 5;
				System.out.println("T24 transaction rollback error: " + e.getMessage());
			}finally{
				service.cleanUp(); 
			}
		}
		
	}

	public void setResult(List<T24Event> object) {
		result = null;
		
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public EventPollingData getData() {
		if(data==null){
//			List<String> comp = new ArrayList<String>();
//			comp.add(config.getUserWsDeatils().getCoCode().getValue());
			this.data = EventPollingData.newInstance(eventType, eventCount);
		}
		return data;
	}

	public void setData(EventPollingData data) {
		this.data = data;
	}
	
}
