package com.temenos.adapter.mule.T24inbound.connector.t24xa;

import com.temenos.adapter.common.runtime.inbound.EventPollingData;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingService;

public class T24xaResourse {
	
	
	private T24EventPollingService service;
	private EventPollingData data;
	
	
	public T24xaResourse(T24EventPollingService service, EventPollingData data){
		setService(service);
		setData(data);
	}

	public T24xaResourse() {
		// TODO Auto-generated constructor stub
	}

	public T24EventPollingService getService() {
		return service;
	}

	public void setService(T24EventPollingService service) {
		this.service = service;
	}

	public EventPollingData getData() {
		return data;
	}

	public void setData(EventPollingData data) {
		this.data = data;
	}
	
}
