package com.temenos.adapter.mule.T24outbound.metadata.model;

import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.common.metadata.outbound.InputPortDefinitionBuilder;
import com.temenos.adapter.common.metadata.outbound.OutputPortDefinitionBuilder;
import com.temenos.adapter.common.metadata.outbound.PortDefinitionBuilder;
import com.temenos.adapter.common.metadata.outbound.ServiceXmlMetadataBuilder;
import com.temenos.soa.services.data.xsd.Response;
import com.temenos.soa.services.data.xsd.ResponseDetails;

import java.util.List;


public class MetadataDiscoveryServiceHelper {

    public static OutboundMetadataDescription buildDescription(T24ServiceOperationImpl serviceOperation, String name, List<String> schemasList) throws T24MetadataException {
        ServiceXmlMetadataBuilderFactory metadataBuilderFactory = new ServiceXmlMetadataBuilderFactory(serviceOperation, schemasList);

        ServiceXmlMetadataBuilder inputMetadataBuilder = metadataBuilderFactory.getInputMetadataBuilder();
        ServiceXmlMetadataBuilder outputMetadataBuilder = metadataBuilderFactory.getOutputMetadataBuilder();
        PortDefinitionBuilder inputDefinitionBuilder = new InputPortDefinitionBuilder(serviceOperation.getTarget().replaceAll("[-+.^:,]", ""), inputMetadataBuilder.buildStructures());

        PortDefinitionBuilder outputDefinitionBuilder = new OutputPortDefinitionBuilder(serviceOperation.getTarget().replaceAll("[-+.^:,]", ""), outputMetadataBuilder.buildStructures());

        return new T24OutboundDescription(name, inputDefinitionBuilder.buildPortDefinition(), outputDefinitionBuilder.buildPortDefinition(), inputMetadataBuilder.getSchemas(), outputMetadataBuilder.getSchemas());
    }


    public static String buildErrorMessage(ResponseDetails errorResponseDetails) {
        if (errorResponseDetails == null) {
            throw new NullPointerException("Response details is null");
        }
        List<Response> responses = errorResponseDetails.getResponses();
        StringBuffer errorMessage = new StringBuffer();
        for (Response response : responses) {
            errorMessage.append("[Code: ");
            errorMessage.append((String) response.getResponseCode().getValue());
            errorMessage.append(", Type: ");
            errorMessage.append((String) response.getResponseType().getValue());
            errorMessage.append(", Text: ");
            errorMessage.append((String) response.getResponseText().getValue());
            errorMessage.append(", Info: ");
            errorMessage.append((String) response.getResponseInfo().getValue());
            errorMessage.append("],");
        }
        return errorMessage.toString();
    }
}
