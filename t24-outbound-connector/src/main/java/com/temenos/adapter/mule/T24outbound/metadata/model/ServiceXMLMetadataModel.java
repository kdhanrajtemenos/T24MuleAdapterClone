package com.temenos.adapter.mule.T24outbound.metadata.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.BASE64Encoder;

import com.temenos.adapter.common.metadata.MetadataSchema;
import com.temenos.adapter.common.metadata.ServiceOperation;


public class ServiceXMLMetadataModel implements MetadataModel {
	
    public static enum Property {
        SERVICE_OPERATION_NAME("serviceOperationName"),
        SERVICE_OPERATION_ACTION("serviceOperationAction"),
        SERVICE_OPERATION_TARGET("serviceOperationTarget"),
        ROOT_NAME_REQUEST("rootNameRequest"),
        ROOT_NAME_RESPONSE("rootNameResponse"),
        INPUT_SCHEMAS("inputSchemas"),
        OUTPUT_SHEMAS("outputSchemas");
        private final String property;

        private Property(String s) {
            property = s;
        }

        @Override
        public String toString() {
            return property;
        }
    }

    private String name;
    private MetadataSchema masterInputSchema;
    private MetadataSchema masterOutputSchema;
    private List<String> inputSchemas;
    private List<String> outputSchemas;
    private Map<String, MetadataSchema> importedInputSchemas;
    private Map<String, MetadataSchema> importedOutputSchemas;
    private ServiceOperation operation;
    private String rootNameRequest;
    private String rootNameResponse;

    public ServiceXMLMetadataModel(String name, MetadataSchema masterInputSchema,
                               MetadataSchema masterOutputSchema,
                               List<String> inputSchemas,
                               List<String> outputSchemas,
                               Map<String, MetadataSchema> importedInputSchemas,
                               Map<String, MetadataSchema> importedOutputSchemas,
                               ServiceOperation operation,
                               String rootNameRequest,
                               String rootNameResponse) {
        this.name = name;
        this.masterInputSchema = masterInputSchema;
        this.masterOutputSchema = masterOutputSchema;
        this.inputSchemas = inputSchemas;
        this.outputSchemas = outputSchemas;
        this.importedInputSchemas = importedInputSchemas;
        this.importedOutputSchemas = importedOutputSchemas;
        this.operation = operation;
        this.rootNameRequest = rootNameRequest;
        this.rootNameResponse = rootNameResponse;
    }

    public String getName() {
        return name;
    }

    public MetadataSchema getMasterInputSchema() {
        return masterInputSchema;
    }

    public MetadataSchema getMasterOutputSchema() {
        return masterOutputSchema;
    }

    public Map<String, MetadataSchema> getImportedInputSchemas() {
        return importedInputSchemas;
    }

    public Map<String, MetadataSchema> getImportedOutputSchemas() {
        return importedOutputSchemas;
    }

    public ServiceOperation getOperation() {
        return operation;
    }

    public String getRootNameRequest() {
        return rootNameRequest;
    }

    public String getRootNameResponse() {
        return rootNameResponse;
    }

    public String getInputSchemas() {
        StringBuilder buffer = new StringBuilder();
        for (String requestSchema : inputSchemas) {
            buffer.append(requestSchema);
            buffer.append(",");
        }
        String result = new BASE64Encoder().encode(buffer.substring(0, buffer.length() -1).getBytes());
        return result;
    }

    public String getOutputSchemas() {
        StringBuilder buffer = new StringBuilder();
        for (String responseSchema : outputSchemas) {
            buffer.append(responseSchema);
            buffer.append(",");
        }
        String result = new BASE64Encoder().encode(buffer.substring(0, buffer.length() -1).getBytes());
        return result;
    }

    public String verify() {
        StringBuilder buffer = new StringBuilder();
        if (masterInputSchema == null ||
            masterInputSchema.getContent() == null) {
            buffer.append("Missing master input schema for service: " + name + "\n");
        }
        if (masterOutputSchema == null ||
            masterOutputSchema.getContent() == null) {
            buffer.append("Missing master output schema for service: " + name + "\n");
        }
        if (inputSchemas == null || inputSchemas.size() == 0) {
            buffer.append("Missing input schemas for service: " + name + "\n");
        } else {
            for (String inputSchema : inputSchemas) {
                if (inputSchema.isEmpty()) {
                    buffer.append("Missing input schema for service: " + name + "\n");
                }
            }
        }
        if (outputSchemas == null || outputSchemas.size() == 0) {
            buffer.append("Missing output schemas for service: " + name + "\n");
        } else {
            for (String outputSchema : outputSchemas) {
                if (outputSchema.isEmpty()) {
                    buffer.append("Missing output schema for service: " + name + "\n");
                }
            }
        }
        if (importedInputSchemas == null || importedInputSchemas.size() == 0) {
            buffer.append("Missing imported input schemas for service: " + name + "\n");
        } else {
            for (MetadataSchema importedInputSchema : importedInputSchemas.values()) {
                if (importedInputSchema == null || importedInputSchema.getContent() == null) {
                    buffer.append("Missing imported input schema for service: " + name + "\n");
                }
            }
        }
        if (importedOutputSchemas == null || importedOutputSchemas.size() == 0) {
            buffer.append("Missing imported output schemas for service: " + name + "\n");
        } else {
            for (MetadataSchema importedOutputSchema : importedOutputSchemas.values()) {
                if (importedOutputSchema == null || importedOutputSchema.getContent() == null) {
                    buffer.append("Missing imported output schema for service: " + name + "\n");
                }
            }
        }
        if (operation == null) {
            buffer.append("Missing operation for service: " + name + "\n");
        } else {
            if (operation.getName().isEmpty()) {
                buffer.append("Missing " + Property.SERVICE_OPERATION_NAME.toString() + " for service: " + name + "\n");
            }
            if (operation.getAction().isEmpty()) {
                buffer.append("Missing " + Property.SERVICE_OPERATION_ACTION.toString() + " for service: " + name + "\n");
            }
            if (operation.getTarget().isEmpty()) {
                buffer.append("Missing " + Property.SERVICE_OPERATION_TARGET.toString() + " for service: " + name + "\n");
            }
        }
        if (rootNameRequest.isEmpty()) {
            buffer.append("Missing " + ServiceXMLMetadataModel.Property.ROOT_NAME_REQUEST.toString() + " for service: " + name + "\n");
        }
        if (rootNameResponse.isEmpty()) {
            buffer.append("Missing " + Property.ROOT_NAME_RESPONSE.toString() + " for service: " + name + "\n");
        }
        return buffer.toString();
    }
    
    /* TUK KE SE MISLI */
    public Map<String, String> getAllSchemas(String serviceName) {
        HashMap<String, String> schemas = new HashMap<String, String>();        
        //String folder = "xsd/" + serviceName + "/";
        String folder = serviceName + "/";
        //String file = folder + getName() + ".xsd";
        String masterInputSchemaPath = folder + getName().replaceAll("\\s","") + "Request.xsd";
        String masterOutputSchemaPath = folder + getName().replaceAll("\\s","") + "Response.xsd";
        schemas.put(masterInputSchemaPath, getMasterInputSchema().getContent());
        schemas.put(masterOutputSchemaPath,getMasterOutputSchema().getContent());         
        for (MetadataSchema importedInputSchema : getImportedInputSchemas().values()) {
           schemas.put(folder + importedInputSchema.getLocation(), importedInputSchema.getContent());            
        }
        for (MetadataSchema importedOutputSchema : getImportedOutputSchemas().values()) {
           schemas.put(folder + importedOutputSchema.getLocation(), importedOutputSchema.getContent());            
        }
        return schemas;
    }
}
