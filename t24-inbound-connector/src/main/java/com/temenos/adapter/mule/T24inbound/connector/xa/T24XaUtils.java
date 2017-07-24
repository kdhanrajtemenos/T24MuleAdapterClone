package com.temenos.adapter.mule.T24inbound.connector.xa;

import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.ejb.client.EJBClientTransactionContext;
import org.mule.api.MuleContext;

import bitronix.tm.TransactionManagerServices;

/**
 * TODO: Document me!
 *
 * @author ponmanikandanb
 *
 */
public class T24XaUtils {

    public static void registerXaResource(MuleContext muleContext) {
        // todo move this to a new util class in xa package
        EJBClientTransactionContext txContext = EJBClientTransactionContext.create(muleContext.getTransactionManager(),
                getSynchronizationRegistry());
        EJBClientTransactionContext.setGlobalContext(txContext);
        T24XaResourceProducer.registerXAResource("dummyResource", new DummyXaResource());
    }

    /**
     * @return
     */
    private static TransactionSynchronizationRegistry getSynchronizationRegistry() {
        return TransactionManagerServices.getTransactionSynchronizationRegistry();
    }

    /**
     * @param muleContext
     * @return
     */
    public static boolean isBitronix(MuleContext muleContext) {

        return muleContext.getTransactionManager() instanceof com.mulesoft.mule.bti.transaction.TransactionManagerWrapper;
    }
}
