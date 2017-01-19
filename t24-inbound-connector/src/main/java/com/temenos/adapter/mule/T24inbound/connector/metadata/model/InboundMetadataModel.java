package com.temenos.adapter.mule.T24inbound.connector.metadata.model;

//import com.temenos.adapter.common.metadata.MetadataSchema;


import java.util.HashMap;
import java.util.Map;

//import oracle.ide.net.URLFactory;
//import oracle.ide.net.URLFileSystem;

public class InboundMetadataModel implements MetadataModel {
    
	private String name;
    private MetadataSchema masterSchema;
    private Map<String, MetadataSchema> importedSchemas;

    public InboundMetadataModel(String name, MetadataSchema masterSchema,
                                Map<String, MetadataSchema> importedSchemas) {
        this.name = name;
        this.masterSchema = masterSchema;
        this.importedSchemas = importedSchemas;
    }

    public String getName() {
        return name;
    }

    public MetadataSchema getMasterSchema() {
        return masterSchema;
    }

    public Map<String, MetadataSchema> getImportedSchemas() {
        return importedSchemas;
    }

    public String verify() {
        StringBuilder buffer = new StringBuilder();
        if (masterSchema == null || masterSchema.getContent() == null) {
            buffer.append("Missing master schema for event: " + name + "\n");
        }
        if (importedSchemas == null || importedSchemas.size() == 0) {
            buffer.append("Missing imported schemas for event: " + name + "\n");
        } else {
            for (MetadataSchema importedSchema : importedSchemas.values()) {
                if (importedSchema == null ||
                    importedSchema.getContent() == null) {
                    buffer.append("Missing imported schema for event: " +
                                  name + "\n");
                }
            }
        }
        return buffer.toString();
    }

    
    /*Tova ke se opravq*/
    public Map<String, String> getAllSchemas(String serviceName) {
        HashMap<String, String> schemas = new HashMap<String, String>();        
        String folder = "xsd/" + serviceName + "/";
        String file = folder + getName() + ".xsd";    
        schemas.put(file, getMasterSchema().getContent());  
        for (MetadataSchema importedSchema : getImportedSchemas().values()) {                         
            schemas.put(folder + importedSchema.getLocation(), importedSchema.getContent());            
        }
        return schemas;
    }
}
