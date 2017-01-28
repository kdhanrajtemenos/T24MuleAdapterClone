package com.temenos.adapter.mule.T24outbound.metadata.model;

import java.util.Map;

public interface MetadataModel {
    public String verify();
    public Map<String, String> getAllSchemas(String serviceName);
}
