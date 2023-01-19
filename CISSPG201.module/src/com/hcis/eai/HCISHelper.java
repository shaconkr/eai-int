package com.hcis.eai;

import java.util.Map;
import java.io.UnsupportedEncodingException;
import com.hcis.eai.common.HCISHelperAnc;

public class HCISHelper extends HCISHelperAnc {

	public HCISHelper(String eimsPath, String projPath, String moduleName) {
		super(eimsPath, projPath, moduleName);
	}

	@Override
	public void setProcessName(String processName) {
		super.setProcessName(processName);
	}

	@Override
	public String getInterfaceInfo(String reqIfId, String resIfId) throws Exception {
		return super.getInterfaceInfo(reqIfId, resIfId);
	}

	@Override
	public void prepare(String reqIfId, String resIfId) throws Exception {
		super.prepare(reqIfId, resIfId);
	}

	@Override
	public byte[] JsonToEDIbytes(String projPath, String processName, String msgType, String jsonString, String encoding, String xml) throws Exception {
		return super.JsonToEDIbytes(projPath, processName, msgType, jsonString, encoding, xml);
	}

	@Override
	public byte[] JsonToEDIbytesLen(String projPath, String processName, String msgType, String jsonString, String encoding, String xml) throws Exception {
		return super.JsonToEDIbytesLen(projPath, processName, msgType, jsonString, encoding, xml);
	}

	@Override
	public String EDIbytesToJson(String projPath, String processName, String msgType, byte[] ediBytes, String encoding, String xml) throws Exception {
		return super.EDIbytesToJson(projPath, processName, msgType, ediBytes, encoding, xml);
	}

	@Override
	public String toEDI(String projPath, String processName, String msgType, String jsonString, String encoding, String xml) throws Exception {
	       String ret;
	        String encodedString = new String(jsonString.getBytes(encoding), encoding);
	        Transformer trans = new Transformer(schemaPath, ifId);
	        String streamName = (msgType.startsWith("_")) ? ifId + msgType : msgType;
	        ret = trans.toEDI(streamName, encodedString, encoding, null);
	        return ret;
	}

	@Override
	public String baseMapping(String targetSystemId, String jsonString) {
		return super.baseMapping(targetSystemId, jsonString);
	}

	@Override
	public byte[] concatBytes(byte[] bytes1, byte[] bytes2) {
		return super.concatBytes(bytes1, bytes2);
	}

	@Override
	public String toJSON(String projPath, String processName, String msgType, String ediString, String encoding, String xml) throws Exception {
        String ret;
        Transformer trans = new Transformer(schemaPath, ifId);
        String streamName = (msgType.startsWith("_")) ? ifId + msgType : msgType;
        ret = new String(trans.toJSON(streamName, ediString, encoding, null).getBytes(encoding), encoding);
        return ret;
	}

	@Override
	public byte[] toBytes(String str, String encoding) throws UnsupportedEncodingException {
		return super.toBytes(str, encoding);
	}

	@Override
	public String toString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
		return super.toString(bytes, encoding);
	}

	@Override
	public String encodeCharset(String data, String encoding) throws UnsupportedEncodingException {
		return super.encodeCharset(data, encoding);
	}

	@Override
	public String envelopHcis(String dataJson) {
		return super.envelopHcis(dataJson);
	}

	@Override
	public String developHcis(String cisJson) {
		return super.developHcis(cisJson);
	}

	@Override
	public Map<String, Object> getHcisStdHeader(Map<String, Object> inf) {
		return super.getHcisStdHeader(inf);
	}
}

