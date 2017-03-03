package com.temenos.adapter.mule.T24outbound.proxy;

import org.mule.api.ConnectionException;
import com.temenos.adapter.mule.T24outbound.definition.GetEnquiriesResponse;
import com.temenos.adapter.mule.T24outbound.definition.GetEnquirySchemaResponse;
import com.temenos.adapter.mule.T24outbound.definition.GetEnquirySchemaTypedResponse;
import com.temenos.adapter.mule.T24outbound.definition.GetVersionsResponse;
import com.temenos.adapter.mule.T24outbound.definition.IntegrationLandscapeServiceWSPortType;
import com.temenos.adapter.mule.T24outbound.definition.T24UserDetails;

public class IntegrationLandscapeServiceWSclient {
	
	private String wsdlLoaction;
	
	private IntegrationLandscapeServiceWSPortType port;
	
	private ServiceEndPointType endPointType;
	
	public IntegrationLandscapeServiceWSclient(){
		/* default location*/
		this.setWsdlLoaction("wsdl/IntegrationLandscapeServiceWS.wsdl");
	}
	
	public IntegrationLandscapeServiceWSclient(String wsdlLoaction){
		this.setWsdlLoaction(wsdlLoaction);
	}
	
	public IntegrationLandscapeServiceWSPortType getPort() {
		return port;
	}

	public void setPort(IntegrationLandscapeServiceWSPortType port) {
		this.port = port;
	}


	public IntegrationLandscapeServiceWSPortType clientConnect(ServiceEndPointType endPointType) throws ConnectionException{
		if(port==null){
			setEndPointType(endPointType);
			IntegrationLandscapeServiceWSPortType _port = new IntegrationLandscapeServiceWSfactory(this).initialize();
			setPort(_port);
		}
		return port;
	}

	public String getWsdlLoaction() {
		return wsdlLoaction;
	}

	public void setWsdlLoaction(String wsdlLoaction) {
		this.wsdlLoaction = wsdlLoaction;
	}
	


	public ServiceEndPointType getEndPointType() {
		return endPointType;
	}

	public void setEndPointType(ServiceEndPointType endPointType) {
		this.endPointType = endPointType;
	}
	
	/* Web methods: (Request-Response)  */
	/* Each web-method invoked require T24UserDetails, and some other parameter */
	/* For now just getVersions will be exposed..just for the test */
	public GetVersionsResponse getVersions(T24UserDetails userDetails){
		return port.getVersions(userDetails);
	}
	
	public GetEnquiriesResponse getEnquiries(T24UserDetails userDetails){
		return port.getEnquiries(userDetails);
	}
	
	public GetEnquirySchemaResponse getEnquiriesSchema(T24UserDetails userDetails, String enquiryName){
		return port.getEnquirySchema(userDetails, enquiryName);
	}
	
	public GetEnquirySchemaTypedResponse getEnquiriesSchemaTyped(T24UserDetails userDetails, String enquiryName){
		return port.getEnquirySchemaTyped(userDetails, enquiryName);
	}
}
