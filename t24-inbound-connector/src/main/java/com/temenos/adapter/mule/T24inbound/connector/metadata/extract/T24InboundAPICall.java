package com.temenos.adapter.mule.T24inbound.connector.metadata.extract;


import java.util.Collection;
//import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataDescription;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataDiscoveryService;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.InboundMetadataModel;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.Metadata;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.MetadataModel;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.MetadataSchema;
import com.temenos.adapter.mule.T24inbound.connector.metadata.model.MetadataTree;
import com.temenos.adapter.mule.T24inbound.connector.proxy.IntegrationFlowServiceWSClient;


public abstract class T24InboundAPICall {


	/**
	 * The SOAP proxy-client
	 */
	private IntegrationFlowServiceWSClient client;
	
	
    protected InboundMetadataDiscoveryService inboundService;

    
    private final class T24Callable implements Callable<MetadataTree> {
    	
    	public T24Callable(){
    		System.out.println("HELLO");
    	}
        @Override
        public MetadataTree call() throws Exception {
           MetadataTree metaTree = inboundService.getMetadataTree(client);
        	//MetadataTree metaTree = inboundService.getMetadataTree();
            return metaTree;
        }
    }
    
    protected T24InboundAPICall(IntegrationFlowServiceWSClient client) {

        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        
        this.client = client;
    }

    public MetadataModel getT24Artefacts(Metadata metadata) throws Exception {
        return getT24EventArtefacts(metadata);
    }
    
    public InboundMetadataModel getT24EventArtefacts(Metadata metadata) throws Exception {
        if (!metadata.isSelectable()) {
            throw new Exception("NOT SELECTABLE");
        }
        String name = metadata.getName();
        InboundMetadataDescription selectedMetadataDescription;
        selectedMetadataDescription = inboundService.getMetadataDescription(metadata, client);
        MetadataSchema masterOutputSchema =
        selectedMetadataDescription.getOutputPortMetadata().getMasterSchema();
        Map<String, MetadataSchema> importedOutputSchemas = selectedMetadataDescription.getOutputPortMetadata().getImportedSchemas();
        InboundMetadataModel model = new InboundMetadataModel(name, masterOutputSchema, importedOutputSchemas);
        return model;
    }

    public Collection<Metadata> getT24Metadata(Metadata node) throws Exception {
        if (node == null) {
        	
            // get all the metadata in a tree structure
            MetadataTree metaTree = inboundService.getMetadataTree(client);
        	//MetadataTree metaTree = inboundService.getMetadataTree();
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
