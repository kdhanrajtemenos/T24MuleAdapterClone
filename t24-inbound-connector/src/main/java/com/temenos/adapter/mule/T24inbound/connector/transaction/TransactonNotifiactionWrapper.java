package com.temenos.adapter.mule.T24inbound.connector.transaction;

import com.temenos.adapter.mule.T24inbound.connector.rmi.EventPollingService;

public class TransactonNotifiactionWrapper {
	private EventPollingService service;

	public EventPollingService getService() {
		return service;
	}

	public void setService(EventPollingService service) {
		this.service = service;
	}
}
