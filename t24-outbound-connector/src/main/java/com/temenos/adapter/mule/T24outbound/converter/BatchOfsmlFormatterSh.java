package com.temenos.adapter.mule.T24outbound.converter;


import com.temenos.adapter.common.runtime.outbound.MessageFormatter;
import com.temenos.adapter.common.runtime.outbound.T24RequestConverterException;
import com.temenos.adapter.common.runtime.outbound.T24ResponseConverterException;
import com.temenos.adapter.common.util.T24XmlProcessingException;
import com.temenos.adapter.common.util.XmlUtil;
import com.temenos.tocf.ofsml.OfsmlException;
import com.temenos.tocf.ofsml.OfsmlFormatter;
import com.temenos.tocf.ofsml.OfsmlRequest;
import com.temenos.tocf.ofsml.OfsmlResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BatchOfsmlFormatterSh implements MessageFormatter
{
	  private List<OfsmlRequest> requests;
	  private long timeRequestSent;
	  private long timeResponseReceived;
	  private boolean isSingleRequest = false;
	  private static final Logger logger = Logger.getLogger(BatchOfsmlFormatterSh.class.getName());
	  
	  public BatchOfsmlFormatterSh()
	  {
		  this.requests = new ArrayList<OfsmlRequest>();
	  }
	  
	  public String buildFromOfsResponse(String batchOfsResponse) throws T24ResponseConverterException
	  {
		    logger.fine("Building OFSML response from OFS response [" + batchOfsResponse + "]");
		    Document document = null;
		    if ("".equals(batchOfsResponse)) {
		    	throw new T24ResponseConverterException("Empty response received from T24");
		    }
		    batchOfsResponse = checkAndFormatForSingleResponse(batchOfsResponse);
		    try
		    {
		    	document = XmlUtil.parseString(batchOfsResponse);
		    }
		    catch (T24XmlProcessingException e)
		    {
		    	throw new T24ResponseConverterException(e.getMessage(), e);
		    }
		    NodeList requestNodes = document.getElementsByTagName("request");
		    int size = this.requests.size();
		    StringBuffer ofsmlBuffer = new StringBuffer();
		    if (!this.isSingleRequest) {
		    	ofsmlBuffer.append("<T24Batch>");
		    }
		    this.timeResponseReceived = System.currentTimeMillis();
		    for (int nodePos = 0; nodePos < size; nodePos++)
		    {
			      if ((this.isSingleRequest) && (nodePos > 0)) {
			        break;
			      }
			      String ofsResponse = requestNodes.item(nodePos).getTextContent();
			      String ofsmlResponseStr = getOfsmlResponse(ofsResponse, (OfsmlRequest)this.requests.get(nodePos));
			      
			      ofsmlResponseStr = XmlUtil.removeXmlDeclaration(ofsmlResponseStr);
			      ofsmlBuffer.append(ofsmlResponseStr);
		    }
		    if (!this.isSingleRequest) {
		    	ofsmlBuffer.append("</T24Batch>");
		    }
		    String ofsmlResponse = ofsmlBuffer.toString();
		    logger.fine("OFSML response built [" + ofsmlResponse + "]");
		    return ofsmlResponse;
	  }
	  
	  public String buildOfsRequest(String batchOfsmlRequest) throws T24RequestConverterException
	  {
		    logger.fine("Building OFS request from OFSML request [" + batchOfsmlRequest + "]");
		    Document document = null;
		    try
		    {
		    	document = XmlUtil.parseString(batchOfsmlRequest);
		    }
		    catch (T24XmlProcessingException e)
		    {
		    	throw new T24RequestConverterException(e.getMessage(), e);
		    }
		    if (batchOfsmlRequest.indexOf("T24Batch") == -1) {
		    	this.isSingleRequest = true;
		    } else {
		    	this.isSingleRequest = false;
		    }
		    StringBuffer ofsBuffer = new StringBuffer();
		    ofsBuffer.append("<requests>");
		    NodeList requestNodes = document.getElementsByTagNameNS("*", "T24");
		    int size = requestNodes.getLength();
		    for (int nodePos = 0; nodePos < size; nodePos++)
		    {
			      Node requestNode = requestNodes.item(nodePos);
			      String ofsmlRequestStr = XmlUtil.transformNodeToString(requestNode, false);
			      OfsmlRequest ofsmlRequest = getOfsmlRequest(ofsmlRequestStr);
			      this.requests.add(ofsmlRequest);
			      String ofsRequest = ofsmlRequest.toOfsString();
			      ofsBuffer.append("<request>");
			      ofsBuffer.append(ofsRequest);
			      ofsBuffer.append("</request>");
		    }
		    ofsBuffer.append("</requests>");
		    this.timeRequestSent = System.currentTimeMillis();
		    String ofsRequest = ofsBuffer.toString();
		    logger.fine("Build OFS request [" + ofsRequest + "]");
		    return ofsRequest;
	  }
	  
	  private OfsmlRequest getOfsmlRequest(String ofsmlRequestStr) throws T24RequestConverterException
	  {
		    OfsmlFormatter ofsml = new OfsmlFormatter(); ///exception on instantiation
		    
		    try
		    {
			      OfsmlRequest ofsmlRequest = ofsml.createOFSMLRequest(ofsmlRequestStr, false);
			      if (!ofsmlRequest.isValid()) {
			        throw new Exception("Error while decoding message: " + ofsmlRequest.getErrorCode() + " " + ofsmlRequest.getErrorMessage());
			      }
			      return ofsmlRequest;
		    }
		    catch (OfsmlException e)
		    {
		    	throw new T24RequestConverterException("Error while decoding OFSML request.", e);
		    }
		    catch (Exception e)
		    {
		    	throw new T24RequestConverterException("Error while decoding OFSML request.", e);
		    }
	  }
	  
	  private String getOfsmlResponse(String ofsResponse, OfsmlRequest ofsmlRequest) throws T24ResponseConverterException
	  {
		    OfsmlFormatter ofsml = new OfsmlFormatter();
		    try
		    {
			      OfsmlResponse response = ofsml.createOfsResponse(ofsResponse.getBytes("UTF-8"), ofsmlRequest, "UTF-8", this.timeRequestSent, this.timeResponseReceived);
			      
			      return response.saveString();
		    }
		    catch (UnsupportedEncodingException e)
		    {
		    	throw new T24ResponseConverterException("Error while encoding OFS response.", e);
		    }
		    catch (OfsmlException e)
		    {
		    	throw new T24ResponseConverterException("Error while encoding OFS response.", e);
		    }
	  }
	  
	  private String checkAndFormatForSingleResponse(String batchOfsResponse)
	  {
		    if (batchOfsResponse.startsWith("<requests>")) {
		      return batchOfsResponse;
		    }
		    StringBuffer modifiedOfsResponse = new StringBuffer();
		    modifiedOfsResponse.append("<requests>");
		    modifiedOfsResponse.append("<request>");
		    modifiedOfsResponse.append(batchOfsResponse);
		    modifiedOfsResponse.append("</request>");
		    modifiedOfsResponse.append("</requests>");
		    
		    return modifiedOfsResponse.toString();
	  }
}

