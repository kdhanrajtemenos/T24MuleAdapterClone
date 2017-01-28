package com.temenos.adapter.mule.T24outbound.rmi;

import com.temenos.adapter.common.conf.T24RuntimeConfiguration;


/**
 * T24OperationProcessor implementation for TAFJ Server
 */
public class TafjOutboundProcessor extends BaseOutboundProcessor {

	public TafjOutboundProcessor(T24RuntimeConfiguration runtimeConfiguration,
			T24RequestSpec requestSpec) {
		super(runtimeConfiguration, requestSpec);
	}
}
