package com.temenos.adapter.mule.T24outbound.rmi;


import com.temenos.adapter.common.conf.T24RuntimeConfiguration;


/**
 * T24OperationProcessor implementation for TAFC Server
 */
public class TafcOutboundProcessor extends BaseOutboundProcessor {

	public TafcOutboundProcessor(T24RuntimeConfiguration runtimeConfiguration,
			T24RequestSpec requestSpec) {
		super(runtimeConfiguration, requestSpec);
		
	}
}
