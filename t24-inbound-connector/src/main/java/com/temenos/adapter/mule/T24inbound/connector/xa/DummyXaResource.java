package com.temenos.adapter.mule.T24inbound.connector.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Created by dkumar on 18/07/2017.
 */
public class DummyXaResource implements XAResource {

    public void commit(Xid xid, boolean b) throws XAException {

    }

    public void end(Xid xid, int i) throws XAException {

    }

    public void forget(Xid xid) throws XAException {

    }

    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    public boolean isSameRM(XAResource xaResource) throws XAException {
        return false;
    }

    public int prepare(Xid xid) throws XAException {
        return 0;
    }

    public Xid[] recover(int i) throws XAException {
        return new Xid[0];
    }

    public void rollback(Xid xid) throws XAException {

    }

    public boolean setTransactionTimeout(int i) throws XAException {
        return false;
    }

    public void start(Xid xid, int i) throws XAException {

    }
}
