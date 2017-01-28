package com.temenos.adapter.mule.T24outbound.converter;

/**
 * Implementation for OFSML
 *
 * @author Miroslav Ivanov
 * Date: 1/15/15
 * Time: 11:50 AM
 */
public class OFSMLConverterSh extends BaseBatchOFSMLConverterSh {

    private static final String BATCH_TAG_NAME = "T24Batch";

    @Override
    public String formatResponse(String response) {
        return getOFSMLResponse(response);
    }

    private String getOFSMLResponse(String batchOfsml) {
        return batchOfsml.
                replaceAll("<" + BATCH_TAG_NAME + ">", "").
                replaceAll("</" + BATCH_TAG_NAME + ">", "");
    }
}
