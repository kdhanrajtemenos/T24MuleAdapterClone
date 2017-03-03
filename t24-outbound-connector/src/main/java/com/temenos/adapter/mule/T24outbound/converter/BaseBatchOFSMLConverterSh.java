package com.temenos.adapter.mule.T24outbound.converter;

import com.temenos.adapter.common.runtime.outbound.*;
import com.temenos.adapter.mule.T24outbound.rmi.T24RequestProcessingException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Base class that contains functionality for conversion of request and response
 *
 * @author Miroslav Ivanov
 * Date: 1/15/15
 * Time: 11:50 AM
 */
public abstract class BaseBatchOFSMLConverterSh implements T24OutboundRequestConverter, T24OutboundResponseConverter {
    private static final Logger log = Logger.getLogger(BaseBatchOFSMLConverterSh.class);

    private static final String SINGLE_RESPONSE_TEMPLATE =
            "<requests><request>%s</request></requests>";
    protected static final String REQUEST_TAG_NAME = "T24";

    private int numberOfRequests;

    
    protected BatchOfsmlFormatterSh formatter;

    public BaseBatchOFSMLConverterSh() {
        formatter = new BatchOfsmlFormatterSh();
    }

    private int countRequests(String batchRequest) throws T24RequestProcessingException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(batchRequest)));
        } catch (Exception e) {
            log.error("Error parsing XML message", e);
            throw new T24RequestProcessingException(e);
        }

        return document.getElementsByTagName(REQUEST_TAG_NAME).getLength();
    }

    @Override
    public String convertRequest(String batchRequest) throws T24RequestConverterException {
        try {
            numberOfRequests = countRequests(batchRequest);
        } catch (T24RequestProcessingException e) {
            throw new T24RequestConverterException(e.getMessage(), e);
        }

        return formatter.buildOfsRequest(batchRequest); 
    }

    @Override
    public String convertResponse(String ofsResponse) throws T24ResponseConverterException {
        if (numberOfRequests == 1) {
            ofsResponse = String.format(SINGLE_RESPONSE_TEMPLATE, ofsResponse);
        }

        ofsResponse = formatter.buildFromOfsResponse(ofsResponse);

        return formatResponse(ofsResponse);
    }

    /**
     * Formats the response, different for OFSML/BATCH_OFSML types of request
     *
     * @param response the response from T24 server
     * @return formatted response
     */
    public abstract String formatResponse(String response);
}
