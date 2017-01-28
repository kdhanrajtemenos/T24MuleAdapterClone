package com.temenos.adapter.mule.T24outbound.rmi;

import com.temenos.adapter.common.conf.T24RuntimeConfiguration;
import com.temenos.adapter.common.runtime.T24RuntimeException;
import com.temenos.adapter.common.runtime.outbound.T24OutboundRequestConverter;
import com.temenos.adapter.common.runtime.outbound.T24OutboundRequestExecutor;
import com.temenos.adapter.common.runtime.outbound.T24OutboundResponseConverter;
import com.temenos.adapter.common.runtime.outbound.T24OutboundServiceProvider;
import com.temenos.adapter.common.runtime.outbound.T24OutboundServiceProviderFactory;
import com.temenos.adapter.common.runtime.outbound.T24RequestConverterException;
import com.temenos.adapter.common.runtime.outbound.T24ResponseConverterException;
//import com.temenos.adapter.oracle.outbound.request.exception.T24RequestProcessingException;
import com.temenos.soa.services.data.CFConstants;

public class OutboundRequestExecutor implements T24OutboundProcessor {

	private T24OutboundServiceProvider outboundServiceProvider;
	
	T24RuntimeConfiguration runtimeConfiguration;

	public OutboundRequestExecutor(T24RuntimeConfiguration runtimeConfiguration, T24RequestSpec requestSpec){
		this.runtimeConfiguration =  runtimeConfiguration;
		outboundServiceProvider = T24OutboundServiceProviderFactory.getServiceProvider( requestSpec.getT24RequestType(),runtimeConfiguration,requestSpec.getT24ServiceMetadata());
	}
	
	public T24OutboundRequestExecutor getT24OutboundRequestExecutor() {
		try {
			return outboundServiceProvider.getRequestExecutor();
		} catch (T24RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public ResponseRecord processRequest(String request, T24RequestSpec t24RequestSpec) throws RuntimeException {
		ResponseRecord record =  new ResponseRecord();
		T24OutboundRequestConverter requestConverter = outboundServiceProvider.getRequestConverter();
		T24OutboundResponseConverter responseConverter = outboundServiceProvider.getResponseConverter();
		String response = null,err_msg="";
		boolean err_catch_up = false;
		T24OutboundRequestExecutor executor =  getT24OutboundRequestExecutor();
		

		
		
		try {
			//executor.begin();
			String ofsRequest = requestConverter.convertRequest(request);

			String rawOfsResponse = executor.execute(ofsRequest);
			response = responseConverter.convertResponse(rawOfsResponse);
			//executor.commit();
		} catch (T24RequestConverterException e) {
			/* thrown by requestConverter  */
			err_msg = e.getMessage();
			err_catch_up = true;	
		} catch (T24ResponseConverterException e) {
			/* thrown either by responseConverter or executor */
			err_msg = e.getMessage();
			err_catch_up = true;	
		} catch (T24RuntimeException e) {
			/* thrown either by begin or commit */
			err_msg = e.getMessage();
			err_catch_up = true;
		}
		finally{
			if(err_catch_up){
				/*
				try {
					executor.rollback();
				} catch (T24RuntimeException e) {
					// thrown by rollback (Should not reach here) //
					err_msg = e.getMessage();
				}
				*/
				record.setResponse(err_msg);
				record.setReturnCode(CFConstants.RESPONSE_TYPE_FATAL_ERROR);
				throw new RuntimeException(err_msg);
			}
			if(response != null && !response.isEmpty()){
				record.setResponse(response);
				record.setReturnCode(CFConstants.RETURN_CODE_SUCCESS);
			}else{
				record.setResponse(null);
				record.setReturnCode(CFConstants.RETURN_CODE_FAILURE);
			}
		}
		
		return record;
	}



	

}
