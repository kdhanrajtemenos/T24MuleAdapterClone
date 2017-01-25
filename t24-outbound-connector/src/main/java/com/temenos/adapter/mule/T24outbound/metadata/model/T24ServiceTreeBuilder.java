package com.temenos.adapter.mule.T24outbound.metadata.model;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.temenos.adapter.common.metadata.T24MetadataException;
import com.temenos.adapter.common.util.T24XmlProcessingException;
import com.temenos.adapter.common.util.XmlUtil;

public class T24ServiceTreeBuilder {
	
	  @SuppressWarnings("unused")
	  private static final String T24SERVICE_NAME = "name";
	  @SuppressWarnings("unused")
	  private static final String T24SERVICE_OPERATION = "operation";
	  @SuppressWarnings("unused")
	  private static final String T24OPERATION_NAME = "name";
	  @SuppressWarnings("unused")
	  private static final String T24OPERATION_TARGET = "target";
	  @SuppressWarnings("unused")
	  private static final String T24OPERATION_ACTION = "action";
	  
	  public T24MetadataTree build(String serviceXml)  throws T24MetadataException
	  {
		    validateServiceXml(serviceXml);
		    Document document = null;
		    try
		    {
		    	document = XmlUtil.parseString(serviceXml);
		    }
		    catch (T24XmlProcessingException e)
		    {
		    	throw new T24MetadataException(e.getMessage(), e);
		    }
		    NodeList servicesNode = document.getElementsByTagName("service");
		    int servicesSize = servicesNode.getLength();
		    T24MetadataTree serviceTree = new T24MetadataTree();
		    for (int serviceCnt = 0; serviceCnt < servicesSize; serviceCnt++)
		    {
			      Node serviceNode = servicesNode.item(serviceCnt);
			      T24OutboundMetadata serviceMetadata = getServiceMetadata(serviceNode);
			      if (serviceMetadata != null)
			      {
			        serviceMetadata = addOperationsMetadata(serviceNode, serviceMetadata);
			        serviceTree.addMetadata(serviceMetadata);
			      }
		    }
		    return serviceTree;
	  }
	  
	  private T24OutboundMetadata getServiceMetadata(Node serviceNode)
	  {
		    NodeList childNodes = serviceNode.getChildNodes();
		    int childrenSize = childNodes.getLength();
		    for (int childCnt = 0; childCnt < childrenSize; childCnt++)
		    {
			      Node node = childNodes.item(childCnt);
			      if ("name".equals(node.getNodeName()))
			      {
				        String name = node.getTextContent();
				        T24OutboundMetadata serviceMetadata = new T24OutboundMetadata(name);
				        return serviceMetadata;
			      }
		    }
		    return null;
	  }
	  
	  private T24OutboundMetadata addOperationsMetadata(Node serviceNode, T24OutboundMetadata serviceMetadata)
	  {
	    NodeList childNodes = serviceNode.getChildNodes();
	    int childrenSize = childNodes.getLength();
	    for (int childCnt = 0; childCnt < childrenSize; childCnt++)
	    {
		      Node node = childNodes.item(childCnt);
		      if ("operation".equals(node.getNodeName()))
		      {
			        T24ServiceOperationImpl operation = getOperation(node);
			        T24OperationMetadata operationMetadata = new T24OperationMetadata(operation, serviceMetadata);
			        serviceMetadata.addOperationMetadata(operationMetadata);
		      }
	    }
	    return serviceMetadata;
	  }
	  
	  private T24ServiceOperationImpl getOperation(Node operationNode)
	  {
		    NamedNodeMap attributes = operationNode.getAttributes();
		    Node nameNode = attributes.getNamedItem("name");
		    String name = nameNode.getTextContent();
		    Node targetNode = attributes.getNamedItem("target");
		    String target = targetNode.getTextContent();
		    Node actionNode = attributes.getNamedItem("action");
		    String action = actionNode.getTextContent();
		    T24ServiceOperationImpl operation = new T24ServiceOperationImpl(name, target, action);
		    return operation;
	  }
	  
	  private void validateServiceXml(String serviceXml) throws T24MetadataException
	  {
		    if (serviceXml == null || serviceXml.isEmpty()) {
		    	throw new T24MetadataException("Service XML is invalid [" + serviceXml + "]");
		    }
	  }
}
