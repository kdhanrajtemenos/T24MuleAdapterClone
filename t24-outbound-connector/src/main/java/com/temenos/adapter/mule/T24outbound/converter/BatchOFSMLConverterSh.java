package com.temenos.adapter.mule.T24outbound.converter;

/**
 * Implementation for BATCH_OFSML
 *
 * @author Miroslav Ivanov
 * Date: 1/15/15
 * Time: 11:53 AM
 */
public class BatchOFSMLConverterSh extends BaseBatchOFSMLConverterSh {
    private static final String XML_PI = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
    private static final String XML_PI_REGEX = "<\\?xml[^>?]+\\?>";

    @Override
    public String formatResponse(String response) {
        return tidyXml(response);
    }

    private String tidyXml(String text) {
        return XML_PI + text.replaceAll(XML_PI_REGEX, "");
    }
}
