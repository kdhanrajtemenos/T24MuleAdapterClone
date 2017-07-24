package com.temenos.adapter.mule.T24inbound.connector.xa;

import bitronix.tm.resource.common.AbstractXAResourceHolder;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAResourceHolder;

import javax.transaction.xa.XAResource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by dkumar on 18/07/2017.
 */
public class T24XaResourceHolder extends AbstractXAResourceHolder {
    private final XAResource resource;
    private final ResourceBean bean;

    public T24XaResourceHolder(XAResource resource, ResourceBean bean) {
        this.resource = resource;
        this.bean = bean;
    }

    public XAResource getXAResource() {
        return this.resource;
    }

    public ResourceBean getResourceBean() {
        return this.bean;
    }

    public void close() throws Exception {
        throw new UnsupportedOperationException("T24XAResourceHolder cannot be used with an XAPool");
    }

    public Object getConnectionHandle() throws Exception {
        throw new UnsupportedOperationException("T24XAResourceHolder cannot be used with an XAPool");
    }

    public Date getLastReleaseDate() {
        throw new UnsupportedOperationException("T24XAResourceHolder cannot be used with an XAPool");
    }

    public List<XAResourceHolder> getXAResourceHolders() {
        return Arrays.asList(new XAResourceHolder[]{this});
    }
}