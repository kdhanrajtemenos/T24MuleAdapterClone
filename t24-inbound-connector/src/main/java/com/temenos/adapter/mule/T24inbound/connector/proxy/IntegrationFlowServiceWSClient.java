package com.temenos.adapter.mule.T24inbound.connector.proxy;



import com.temenos.services.integrationflow.data.response.xsd.GetAllFlowNamesResponse;
import com.temenos.services.integrationflow.data.response.xsd.GetAllFlowSchemasResponse;
import com.temenos.services.integrationflow.data.response.xsd.GetFlowSchemaResponse;
import com.temenos.soa.services.data.xsd.T24UserDetails;
import integrationflowservicews.IntegrationFlowServiceWSPortType;
import org.mule.api.ConnectionException;


public class IntegrationFlowServiceWSClient {
	
	private String wsdlLoaction;
	
	private IntegrationFlowServiceWSPortType port;
	
	private ServiceEndPointType endPointType;
	
	public IntegrationFlowServiceWSClient(String wsdlLoaction){
		this.setWsdlLoaction(wsdlLoaction);
	}
	
	public IntegrationFlowServiceWSPortType getPort() {
		return port;
	}

	public void setPort(IntegrationFlowServiceWSPortType port) {
		this.port = port;
	}


	public IntegrationFlowServiceWSPortType clientConnect(ServiceEndPointType endPointType) throws ConnectionException{
		if(port==null){
			setEndPointType(endPointType);
			IntegrationFlowServiceWSPortType _port = new IntegrationFlowServiceWSFactory(this).initialize();
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
	public GetAllFlowNamesResponse getAllFlowNames(T24UserDetails userDetails){
		return port.getAllFlowNames(userDetails);
	}
	

	public GetAllFlowSchemasResponse getAllFlowSchemas(T24UserDetails userDetails){
		return port.getAllFlowSchemas(userDetails);
	}
	
	public GetFlowSchemaResponse getFlowSchema(T24UserDetails userDetails , String flowName){
		return port.getFlowSchema(userDetails, flowName);
	}
	

	
}
