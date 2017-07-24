package com.temenos.adapter.mule.T24inbound.connector.xa;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.internal.XAResourceHolderState;
import bitronix.tm.recovery.RecoveryException;
import bitronix.tm.resource.ResourceObjectFactory;
import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.transaction.xa.XAResource;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dkumar on 18/07/2017.
 */
public class T24XaResourceProducer extends ResourceBean implements XAResourceProducer {
    private static final String T24_XA_RESOURCE_PRODUCER = "T24XaResourceProducer";
	private static final Logger log = LoggerFactory.getLogger(T24XaResourceHolder.class.getName());
    private static final ConcurrentMap<String, T24XaResourceProducer> producers = new ConcurrentHashMap();
    private final ConcurrentMap<Integer, T24XaResourceHolder> xaResourceHolders = new ConcurrentHashMap();
    private final AtomicInteger xaResourceHolderCounter = new AtomicInteger();
    private volatile RecoveryXAResourceHolder recoveryXAResourceHolder;

    private T24XaResourceProducer() {
        this.setApplyTransactionTimeout(true);
    }

    public static void registerXAResource(String uniqueName, XAResource xaResource) {
        T24XaResourceProducer xaResourceProducer = (T24XaResourceProducer)producers.get(uniqueName);
        if(xaResourceProducer == null) {
            xaResourceProducer = new T24XaResourceProducer();
            xaResourceProducer.setUniqueName(uniqueName);
            xaResourceProducer.addXAResource(xaResource);
            T24XaResourceProducer previous = (T24XaResourceProducer)producers.putIfAbsent(uniqueName, xaResourceProducer);
            if(previous == null) {
                xaResourceProducer.init();
            } else {
                previous.addXAResource(xaResource);
            }
        } else {
            xaResourceProducer.addXAResource(xaResource);
        }

    }

    public static void unregisterXAResource(String uniqueName, XAResource xaResource) {
        T24XaResourceProducer xaResourceProducer = (T24XaResourceProducer)producers.get(uniqueName);
        if(xaResourceProducer != null) {
            boolean found = xaResourceProducer.removeXAResource(xaResource);
            if(!found) {
                log.error("no XAResource " + xaResource + " found in XAResourceProducer with name " + uniqueName);
            }

            if(xaResourceProducer.xaResourceHolders.isEmpty()) {
                xaResourceProducer.close();
                producers.remove(uniqueName);
            }
        } else {
            log.error("no XAResourceProducer registered with name " + uniqueName);
        }

    }

    private void addXAResource(XAResource xaResource) {
    	T24XaResourceHolder xaResourceHolder = new T24XaResourceHolder(xaResource, this);
        int key = this.xaResourceHolderCounter.incrementAndGet();
        this.xaResourceHolders.put(Integer.valueOf(key), xaResourceHolder);
    }

    private boolean removeXAResource(XAResource xaResource) {
        Iterator i$ = this.xaResourceHolders.entrySet().iterator();

        Integer key;
        T24XaResourceHolder xaResourceHolder;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            Map.Entry entry = (Map.Entry)i$.next();
            key = (Integer)entry.getKey();
            xaResourceHolder = (T24XaResourceHolder)entry.getValue();
        } while(xaResourceHolder.getXAResource() != xaResource);

        this.xaResourceHolders.remove(key);
        return true;
    }

    public XAResourceHolderState startRecovery() throws RecoveryException {
        if(this.recoveryXAResourceHolder != null) {
            throw new RecoveryException("recovery already in progress on " + this);
        } else if(this.xaResourceHolders.isEmpty()) {
            throw new RecoveryException("no XAResource registered, recovery cannot be done on " + this);
        } else {
            this.recoveryXAResourceHolder = new RecoveryXAResourceHolder((XAResourceHolder)this.xaResourceHolders.values().iterator().next());
            return new XAResourceHolderState(this.recoveryXAResourceHolder, this);
        }
    }

    public void endRecovery() throws RecoveryException {
        this.recoveryXAResourceHolder = null;
    }

    public void setFailed(boolean failed) {
    }

    public XAResourceHolder findXAResourceHolder(XAResource xaResource) {

        Iterator i$ = this.xaResourceHolders.values().iterator();

        T24XaResourceHolder xaResourceHolder;
        do {
            if(!i$.hasNext()) {
                //if resource doesn't exist add it and then return holder
                registerXAResource(T24_XA_RESOURCE_PRODUCER, xaResource);
                log.info("Registering {} as resource producer in Bitronix for T24 Xa Resource.", T24_XA_RESOURCE_PRODUCER);
                return this.producers.get(T24_XA_RESOURCE_PRODUCER).findXAResourceHolder(xaResource); 
            }

            xaResourceHolder = (T24XaResourceHolder)i$.next();
        } while(xaResource != xaResourceHolder.getXAResource());

        return xaResourceHolder;
    }

    public void init() {
        try {
            ResourceRegistrar.register(this);
        } catch (RecoveryException var2) {
            throw new BitronixRuntimeException("error recovering " + this, var2);
        }
    }

    public void close() {
        this.xaResourceHolders.clear();
        this.xaResourceHolderCounter.set(0);
        ResourceRegistrar.unregister(this);
    }

    public XAStatefulHolder createPooledConnection(Object xaFactory, ResourceBean bean) throws Exception {
        throw new UnsupportedOperationException("Ehcache is not connection-oriented");
    }

    public Reference getReference() throws NamingException {
        return new Reference(T24XaResourceProducer.class.getName(), new StringRefAddr("uniqueName", this.getUniqueName()), ResourceObjectFactory.class.getName(), (String)null);
    }

    public String toString() {
        return "a EhCacheXAResourceProducer with uniqueName " + this.getUniqueName();
    }
}