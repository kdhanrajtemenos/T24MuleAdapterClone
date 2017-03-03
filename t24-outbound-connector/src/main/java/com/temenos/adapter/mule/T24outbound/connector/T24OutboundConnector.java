package com.temenos.adapter.mule.T24outbound.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.Config;

import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;

import com.temenos.adapter.common.runtime.outbound.RequestType;
import com.temenos.adapter.common.runtime.outbound.T24ServiceXmlMetadataImpl;
import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.datasense.DataSenseResolver;
import com.temenos.adapter.mule.T24outbound.metadata.extract.T24OutboundRunTimeMetaDataExtractor;

@Connector(name = "t24-outbound", friendlyName = "T24Outbound")
@MetaDataScope(DataSenseResolver.class)
public class T24OutboundConnector {

    protected final transient Log log = LogFactory.getLog(getClass());

    @Config
	ConnectorConfig config;

	public ConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ConnectorConfig config) {
		this.config = config;
	}

	private T24ServiceXmlMetadataImpl selectedXmlMetaData;

	/**
	 * Process T24 Single OFS request
	 * 
	 * @param singleOfsRequest
	 *            - T24 Single OFS request formatted string
	 * @return String - the T24 response
	 */
	@Processor
	@Summary("This processor assumes that input data is formatated as T24 Singel OFS request string")
	public String singleOfs(@Default("#[message.payload]") String singleOfsRequest) {
		log.info("Call singleOfs processor");
		return genericRequestProccessing(singleOfsRequest, RequestType.SINGLE_OFS, null);
	}

	/**
	 * Process T24 Batch OFS request
	 * 
	 * @param singleOfsRequest
	 *            - T24 Batch OFS request formatted string
	 * @return String - the T24 response
	 */
	@Processor
	@Summary("This processor assumes that input data is formatated as T24 Batch OFS request string")
	public String batchOfs(@Default("#[message.payload]") String batchOfsRequest) {
		log.info("Call batchOfs processor");
		return genericRequestProccessing(batchOfsRequest, RequestType.BATCH_OFS, null);
	}

	/**
	 * Process T24 Single OFSML request
	 * 
	 * @param singleOfsRequest
	 *            - T24 Single OFSML request formatted string
	 * @return String - the T24 response
	 */
	@Processor
	@Summary("This processor assumes that input data is formatated as T24 Singel OFSML request string")
	public String singleOfsml(@Default("#[message.payload]") String singleOfsmlRequest) {
		log.info("Call singleOfsml processor");
		return genericRequestProccessing(singleOfsmlRequest, RequestType.SINGLE_OFSML, null);
	}

	/**
	 * Process T24 Batch OFSML request
	 * 
	 * @param singleOfsRequest
	 *            - T24 Batch OFSML request formatted string
	 * @return String - the T24 response
	 */
	@Processor
	@Summary("This processor assumes that input data is formatated as T24 Batch OFSML request string")
	public String batchOfsml(@Default("#[message.payload]") String batchOfsmlRequest) {
		log.info("Call batchOfsml processor");
		return genericRequestProccessing(batchOfsmlRequest, RequestType.BATCH_OFSML, null);
	}

	/**
	 * Process T24 Custom XML request
	 * 
	 * @param singleOfsRequest
	 *            - T24 Custom XML request formatted string
	 * @return String - the T24 response
	 */
	@Processor
	@Summary("This processor assumes that input data is formatated as T24 Custom XML request string")
	public String serviceXml(@MetaDataKeyParam String serviceName, @Default("#[message.payload]") String xmlRequest) {
		log.info("Call serviceXml processor for service name: "+serviceName);
		if (selectedXmlMetaData == null) {
			selectedXmlMetaData = (new T24OutboundRunTimeMetaDataExtractor(config))
					.getModelArtifactsFromFile(serviceName, config.getSettingsFolder());
		}
		return genericRequestProccessing(xmlRequest, RequestType.SERVICE_XML, selectedXmlMetaData);
	}

	/**
	 * This method is generic for all processors
	 * 
	 * @param request
	 *            - String (request data as string)
	 * @param type
	 *            - RequestType (SINGLE_OFS, SINGLE_OFSML, BATCH_OFS,
	 *            BATCH_OFSML or SERVICE_XML)
	 * @param xmlMetaData
	 *            - T24ServiceXmlMetadataImpl (only for SERVICE_XML )
	 * @return java.lang.String - the response as string
	 * @throws ConnectionException
	 */
	private String genericRequestProccessing(String request, RequestType type, T24ServiceXmlMetadataImpl xmlMetaData) {
		String responce = null;
		try {
			log.info("Call genericRequestProccessing in "+this.getClass());
			responce = config.getOutBoundConnection().ejbConnectionRepsonse(request, type, xmlMetaData);
		} catch (ConnectionException ex) {
			ex.printStackTrace();
		}
		return responce;
	}

}