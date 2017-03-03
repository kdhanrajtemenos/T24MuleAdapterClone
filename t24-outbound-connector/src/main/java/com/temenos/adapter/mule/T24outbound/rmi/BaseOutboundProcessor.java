package com.temenos.adapter.mule.T24outbound.rmi;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.temenos.adapter.common.conf.T24RuntimeConfiguration;
import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.outbound.T24OutboundRequestConverter;
import com.temenos.adapter.common.runtime.outbound.T24OutboundRequestExecutor;

import com.temenos.adapter.common.runtime.outbound.T24OutboundResponseConverter;
import com.temenos.adapter.common.runtime.outbound.T24OutboundServiceProvider;
import com.temenos.adapter.common.runtime.outbound.T24OutboundServiceProviderFactory;
import com.temenos.adapter.common.runtime.outbound.T24RequestConverterException;
import com.temenos.adapter.common.runtime.outbound.T24ResponseConverterException;
import com.temenos.adapter.mule.T24outbound.config.T24OutboundConfig;
import com.temenos.soa.services.data.CFConstants;


/* This will be abstract */
public class BaseOutboundProcessor implements T24OutboundProcessor{

    protected final transient Log log = LogFactory.getLog(getClass());

    private T24OutboundServiceProvider outboundServiceProvider;

	public BaseOutboundProcessor(T24RuntimeConfiguration runtimeConfiguration, T24RequestSpec requestSpec) {
		outboundServiceProvider = T24OutboundServiceProviderFactory.getServiceProvider( requestSpec.getT24RequestType(),runtimeConfiguration,requestSpec.getT24ServiceMetadata());
	}

	private T24OutboundRequestConverter buildRequestProcessor() {
		return outboundServiceProvider.getRequestConverter();
	}

	private T24OutboundResponseConverter buildResponseProcessor() {
		return outboundServiceProvider.getResponseConverter();
	}

	private T24OutboundRequestConverter requestConverter;
	private T24OutboundResponseConverter responseConverter;
	
	/**
	 * @param request - String ( request formatted string)
	 * @param t24RequestSpec - T24RequestSpec (request specifications: type, XML meta data
	 * @return wrapped response data
	 * @throws T24RequestProcessingException
	 */
	@Override
	public ResponseRecord processRequest(String request, T24RequestSpec t24RequestSpec) throws RuntimeException {

		if(requestConverter==null){
			requestConverter = buildRequestProcessor();
		}
		
		if(responseConverter == null){
			responseConverter = buildResponseProcessor();
		}
		
		ResponseRecord record = new ResponseRecord();
		T24OutboundRequestExecutor executor = null;
		String response;
		try {

			String  ofsRequest = requestConverter.convertRequest(request);

			log.debug("OFS REQUEST");
			if (T24OutboundConfig.isDevDebug()) {
				log.debug(ofsRequest);
			}
			
			executor = outboundServiceProvider.getRequestExecutor();
			String ofsResponse = executor.execute(ofsRequest);
						
			log.debug("OFS RESPONSE");
			if (T24OutboundConfig.isDevDebug()) {
				log.debug(ofsResponse);
			}

			response = responseConverter.convertResponse(ofsResponse);
			
			/*Ke se puskat finally za da se prihvashtat exception codovete i da se predavata natatuk*/
		} catch (T24RequestConverterException e) {
			throw new RuntimeException(e.getMessage());
		} catch (T24ResponseConverterException e) {
			throw new RuntimeException(e.getMessage());
		} catch (T24RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
		finally {
			if(null != executor) {
				// fix channels out problem
				log.info("CleanUp executor in "+this.getClass());
				executor.cleanUp();
			}
		}
		
		record.setResponse(response);
		record.setReturnCode(CFConstants.RETURN_CODE_SUCCESS); 
		return record;
	}

}
