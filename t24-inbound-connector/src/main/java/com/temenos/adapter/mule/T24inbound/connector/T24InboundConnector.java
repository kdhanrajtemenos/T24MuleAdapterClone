package com.temenos.adapter.mule.T24inbound.connector;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceStrategy;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.callback.SourceCallback;
import org.mule.api.config.MuleProperties;

import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.inbound.T24Event;
import com.temenos.adapter.common.runtime.inbound.T24EventPollingException;
import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigServerSelector;
import com.temenos.adapter.mule.T24inbound.connector.datasense.DataSenseResolver;
import com.temenos.adapter.mule.T24inbound.connector.rmi.EventPollingService;
import com.temenos.adapter.mule.T24inbound.connector.xa.T24XaUtils;

@Connector(name = "t24-inbound", friendlyName = "T24Inbound")
@MetaDataScope(DataSenseResolver.class)
public class T24InboundConnector {

    protected final transient Log log = LogFactory.getLog(getClass());
    private static AtomicBoolean setEjbXaTransactionContext = new AtomicBoolean(true);

    @Config
    ConnectorConfig config;

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

    private EventPollingService pollService = null;

    @Inject
    private MuleContext muleContext;

    @PostConstruct
    public void init() throws Exception {

        try {

            pollService = EventPollingService.getInstance(config);

        } catch (Exception re) {
            log.warn("Event service registered");
        }

    }

    public void setMuleContext(final MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    public static final String SOURCE_MESSAGE_PROCESSOR_PROPERTY_NAME = MuleProperties.ENDPOINT_PROPERTY_PREFIX
            + "sourceMessageProcessor";

    @Processor
    @Summary("This method extracts messages from T24 server")
    public List<T24Event> eventPool(MuleEvent event, @MetaDataKeyParam String eventType, @Default("1") int batchSize)
            throws ConnectionException {
        List<T24Event> events;
        log.info("Enter @Processor method eventPool for event name: " + eventType + " and batchSize: " + batchSize);
        try {
            // TODO find out why poll service is needed here
            events = pollService.getT24Events(eventType, batchSize);
            log.info("T24 extracted messages size: " + events.size());
            for (T24Event item : events) {
                log.info("Process event with ID: " + item.getId());
                log.info("Process event with content: " + item.getData());
                if (log.isDebugEnabled()) {
                    log.debug("Process event with content: " + item.getData());
                }
            }
        } catch (RuntimeException | T24RuntimeException | T24EventPollingException e) {
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "111", e.getMessage());
        }
        log.info("Exit @Processor method eventPool");
        return events;
    }

    /**
     * Source processing of messages to support transaction wrapping and batch
     * processing
     *
     * @param callback
     *            The source callback used to dispatch message to the flow
     * @throws Exception
     *             error produced while processing the payload
     */
    @Source(sourceStrategy = SourceStrategy.POLLING, pollingPeriod = 5000)
    @Summary("This method extracts messages from T24 server with build-in pooling in custom transactions scope")
    public void getMessage(SourceCallback callback, String eventType, int batchSize) throws ConnectionException {
        log.info("Enter @Source callback method getMessage for event name: " + eventType + " and batchSize: "
                + batchSize);
        String response = "";
        List<T24Event> events;
        batchSize = (batchSize < 1) ? 1 : batchSize;
        try {

            boolean useBitronix = T24XaUtils.isBitronix(muleContext);
            // TODO only do this when we are using jboss client and running
            // standalone transaction manager
            if (setEjbXaTransactionContext.compareAndSet(true, false)) {
                try {
                    if (RuntimeConfigServerSelector.JBOSS72.equals(config.getT24RunTime()) && useBitronix) {
                        T24XaUtils.registerXaResource(muleContext);
                    }
                } catch (Exception e) {
                    log.debug("Result: " + e.getMessage());
                    setEjbXaTransactionContext.set(true);
                }
            }
            muleContext.getTransactionManager().begin();
            try {
                pollService.setEventType(eventType);
                pollService.setEventCount(batchSize);
                events = pollService.getService(pollService.getEventType(), batchSize).execute();
                log.info("T24 extracted messages size: " + events.size());

                for (T24Event event : events) {
                    response += event.getData() + "\r\n";
                    log.info("Process event with ID: " + event.getId());
                    if (log.isDebugEnabled()) {
                        log.debug("Process event with content: " + event.getData());
                    }
                    callback.process(event.getData());
                    log.info("Finish processing event with ID: " + event.getId());
                }
                muleContext.getTransactionManager().commit();
            } catch (T24EventPollingException e) {
                muleContext.getTransactionManager().rollback();
                log.error("T24EventPollingException in event processing");
            } catch (T24RuntimeException e) {
                muleContext.getTransactionManager().rollback();
                log.error("T24 error: " + e.getMessage());
            } catch (Exception ex) {
                muleContext.getTransactionManager().rollback();
                log.error("T24 error: " + ex.getMessage());
            }
        } catch (Exception e) {
            log.trace("Result: " + response);
            log.error(e.getMessage());
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "112",
                    "Source processing exception! Name: " + e.getMessage());
        }
        log.trace("Result: " + response);
        log.info("Exit @Source callback method getMessage");
    }

}