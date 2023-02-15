package com.hcis.eai.tad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcis.eai.common.HCISHelper;


public class TADHelper<N> extends HCISHelper<N> {
	
    private static final Logger log = LoggerFactory.getLogger(TADHelper.class);	
	
	@Override
    public String toEDI(String msgType, String jsonString, String encoding, String xml) throws Exception {
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId = getInterfaceId();
        EDITransformer trans = new EDITransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("## streamName [{}] xml [{}] msgType [{}] ", streamName, xml, msgType);
        return  trans.toEDI(streamName, encodedString, encoding, xml);
    }
	
	@Override	
    public String toJSON(String msgType, String ediString, String encoding, String xml) throws Exception {
        String interfaceId = getInterfaceId();
        EDITransformer trans = new EDITransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        return new String(trans.toJSON(streamName, ediString, encoding, null).getBytes(encoding), encoding);
    }
}
