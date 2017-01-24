package com.temenos.adapter.mule.T24inbound.connector.t24xa;

import java.util.List;

///import javax.transaction.Transaction;

import org.mule.api.MuleContext;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionException;

import org.mule.api.transaction.UniversalTransactionFactory;
import org.mule.transaction.TransactionCoordination;

import com.temenos.adapter.common.runtime.inbound.EventPollingData;
import com.temenos.adapter.common.runtime.inbound.T24Event;

public class T24xaTransactionFactory implements UniversalTransactionFactory  {
	
	public T24xaTransactionFactory() {
    }

    public T24xaTransaction beginTransaction(MuleContext muleContext) throws TransactionException {
    	T24xaTransaction tx = new T24xaTransaction(muleContext);
    	tx.begin();
    	return tx;
    }
    
    public List<T24Event> executeTransaction(MuleContext muleContext, T24xaResourse resource) throws TransactionException {
    	T24xaTransaction tx = new T24xaTransaction(muleContext);
    	
    	
    	tx.begin();
    	tx.bindResource(T24xaUtils.TRANSACTION_RSOURSE_TYPE, resource);
    	
    
    	List<T24Event> result  = tx.execute(resource);
    	return result;
    }

    public boolean isTransacted() {
        return true;
    }
    
    public void commit() {
    	Transaction currentTx = TransactionCoordination.getInstance().getTransaction();
    	if(currentTx != null){
    		
    		T24xaResourse xaResourse  = (T24xaResourse)currentTx.getResource(T24xaUtils.TRANSACTION_RSOURSE_TYPE);
    		
    		try {
				currentTx.commit();
				if(xaResourse != null){
					xaResourse.getService().cleanUp();
				}
			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public void rollback() {
    	Transaction currentTx = TransactionCoordination.getInstance().getTransaction();
    	if(currentTx != null){
    		
    		T24xaResourse xaResourse  = (T24xaResourse)currentTx.getResource(T24xaUtils.TRANSACTION_RSOURSE_TYPE);
    		
    		try {
				currentTx.rollback();
				if(xaResourse != null){
					xaResourse.getService().cleanUp();
				}
			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public T24xaTransaction createUnboundTransaction(MuleContext muleContext) throws TransactionException {
        return new T24xaTransaction(muleContext);
    }
}
