package com.temenos.adapter.mule.T24outbound.metadata.extract;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.temenos.adapter.common.metadata.MetadataSchema;

import com.temenos.adapter.common.metadata.ServiceOperation;
import com.temenos.adapter.mule.T24outbound.metadata.model.Metadata;
import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataModel;
import com.temenos.adapter.mule.T24outbound.metadata.model.MetadataTree;
import com.temenos.adapter.mule.T24outbound.metadata.model.OutboundMetadataDescription;
import com.temenos.adapter.mule.T24outbound.metadata.model.OutboundMetadataDiscoveryService;
import com.temenos.adapter.mule.T24outbound.metadata.model.ServiceXMLMetadataModel;
import com.temenos.adapter.mule.T24outbound.metadata.model.T24OperationMetadata;
import com.temenos.adapter.mule.T24outbound.proxy.IntegrationLandscapeServiceWSclient;



public abstract class T24OutboundAPICall {


	/**
	 * The SOAP proxy-client
	 */
	private IntegrationLandscapeServiceWSclient client;
	
	
    protected OutboundMetadataDiscoveryService outboundService;

    private final class T24Callable implements Callable<MetadataTree> {
    	
    	public T24Callable(){
    		System.out.println("HELLO");
    	}
        @Override
        public MetadataTree call() throws Exception {
            MetadataTree metaTree = outboundService.getMetadataTree(client);
            return metaTree;
        }
    }

    protected T24OutboundAPICall(IntegrationLandscapeServiceWSclient client) {

        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        
        this.client = client;
    }


    public ServiceXMLMetadataModel getT24ServiceArtefacts(Metadata metadata) throws Exception {
        if (!metadata.isSelectable()) {
            throw new Exception("NOT SELECTABLE");
        }

        String name = metadata.getName();
        T24OperationMetadata t24OperationMetadata = (T24OperationMetadata)metadata;
        ServiceOperation operation = t24OperationMetadata.getOperation();
        OutboundMetadataDescription selectedMetadataDescription;
        selectedMetadataDescription = outboundService.getMetadataDescription(metadata, client);
        MetadataSchema masterInputSchema = selectedMetadataDescription.getInputPortMetadata().getMasterSchema();
        MetadataSchema masterOutputSchema = selectedMetadataDescription.getOutputPortMetadata().getMasterSchema();


        List<String> inputSchemas = selectedMetadataDescription.getInputSchemaDocuments();
        List<String> outputSchemas = selectedMetadataDescription.getOutputSchemaDocuments();
        Map<String, MetadataSchema> importedInputSchemas =  selectedMetadataDescription.getInputPortMetadata().getImportedSchemas();
        Map<String, MetadataSchema> importedOutputSchemas = selectedMetadataDescription.getOutputPortMetadata().getImportedSchemas();
        String rootNameRequest = selectedMetadataDescription.getInputPortMetadata().getRootElement();
        String rootNameResponse = selectedMetadataDescription.getOutputPortMetadata().getRootElement();

        ServiceXMLMetadataModel model = new ServiceXMLMetadataModel(name, masterInputSchema,
                                    masterOutputSchema, inputSchemas,
                                    outputSchemas, importedInputSchemas,
                                    importedOutputSchemas, operation,
                                    rootNameRequest, rootNameResponse);
        return model;
    }
    
    public MetadataModel getT24Artefacts(Metadata metadata) throws Exception {
        return getT24ServiceArtefacts(metadata);
    }

    public Collection<Metadata> getT24Metadata(Metadata node) throws Exception {
        if (node == null) {
        	
            // get all the metadata in a tree structure
            MetadataTree metaTree = outboundService.getMetadataTree(client);
            
            Collection<Metadata> metaCollection = metaTree.getAllMetadata();
            return metaCollection;
        } else {        	
            return node.getChildren();
        }
    }



    public boolean isConnectionValid(int timeoutSec) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        T24Callable testCallable = new T24Callable();
        Future<MetadataTree> futureResult = service.submit(testCallable);
        try {
            futureResult.get(timeoutSec, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("No response after specified timoeout");
            futureResult.cancel(true);
            return false;
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
            futureResult.cancel(true);
            return false;
        } catch (ExecutionException e) {
            System.out.println("Thread execution failed: " + e.getMessage());
            futureResult.cancel(true);
            return false;
        }
        return true;
    }
}
