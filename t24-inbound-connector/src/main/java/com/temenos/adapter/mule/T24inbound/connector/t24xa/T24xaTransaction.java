package com.temenos.adapter.mule.T24inbound.connector.t24xa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.transaction.TransactionException;
import org.mule.config.i18n.CoreMessages;
import org.mule.transaction.AbstractSingleResourceTransaction;
import org.mule.transaction.IllegalTransactionStateException;

import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.inbound.EventPollingData;
import com.temenos.adapter.common.runtime.inbound.T24Event;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingException;

public class T24xaTransaction extends AbstractSingleResourceTransaction {

	
	protected transient Log logger = LogFactory.getLog(getClass());
	
	private T24xaResourse t24xaResourse;
	
	
	public T24xaTransaction(MuleContext muleContext) {
		super(muleContext);
	}
	
	public void bindResource(Object key, Object resource) throws TransactionException {
		logger.debug("Binding T24xa transaction: " + super.getId());
		if (!(key instanceof String) || !(resource instanceof T24xaResourse) || !key.equals(T24xaUtils.TRANSACTION_RSOURSE_TYPE)) {
	            throw new IllegalTransactionStateException( CoreMessages.transactionCanOnlyBindToResources("org.mule.modules.t24inbound.jta./org.mule.modules.t24inbound.jta."));
	    }
		t24xaResourse = (T24xaResourse)resource;
		try {
			super.bindResource(key, resource);
			//jtaResourse.getService().begin();
			
		} catch (RuntimeException e) {
			throw new T24xaException(e.getMessage());
		}
		
	}

	@Override
	protected void doBegin() throws TransactionException {
		//NOOP		
	}

	@Override
	protected void doCommit() throws TransactionException {
		logger.debug("Committing T24xa transaction: " + super.getId());
		try {
			t24xaResourse.getService().commit();
		} catch (T24RuntimeException e) {
			throw new T24xaException(e.getMessage());
		}
		
	}

	@Override
	protected void doRollback() throws TransactionException {
		logger.debug("Rolling back JTA transaction: " + super.getId());
		try {
			t24xaResourse.getService().rollback();
		} catch (T24RuntimeException e) {
			throw new T24xaException(e.getMessage());
		}
		
	}
	
	public List<T24Event> execute(T24xaResourse resourse){
		
		
		this.t24xaResourse = (T24xaResourse)resource;
		List<T24Event> events = null;
		try {
			/*
			 this.jtaResourse = resource;
			 logger.debug("Binding JTA transaction: " + super.getId());
			 resourse.getService().begin();
			 super.bindResource(JTAUtils.TRANSACTION_RSOURSE_TYPE, resource);
			 logger.debug("Exceuting  JTA transaction: " + super.getId());
			 */
			 events = t24xaResourse.getService().execute(t24xaResourse.getData());
		} catch (T24EventPollingException e) {

			throw new T24xaException(e.getMessage());
		} 
		return events;
	}

	public T24xaResourse getT24xaResourse() {
		return t24xaResourse;
	}

	public void setT24xaResourse(T24xaResourse t24xaResourse) {
		this.t24xaResourse = t24xaResourse;
	}

}
