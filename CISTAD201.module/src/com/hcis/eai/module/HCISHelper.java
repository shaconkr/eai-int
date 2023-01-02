package com.hcis.eai.module;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.hcis.eai.common.HCISHelperAnc;

public class HCISHelper extends HCISHelperAnc {

	public HCISHelper(String eimsPath, String projPath, String moduleName) {
		super(eimsPath, projPath, moduleName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setProcessName(String processName) {
		// TODO Auto-generated method stub
		super.setProcessName(processName);
	}

	@Override
	public String getInterfaceInfo(String reqIfId, String resIfId) throws Exception {
		// TODO Auto-generated method stub
		return super.getInterfaceInfo(reqIfId, resIfId);
	}

	@Override
	public void prepare(String reqIfId, String resIfId) throws Exception {
		// TODO Auto-generated method stub
		super.prepare(reqIfId, resIfId);
	}

	@Override
	public byte[] JsonToEDIbytes(String projPath, String processName, String msgType, String jsonString,
			String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.JsonToEDIbytes(projPath, processName, msgType, jsonString, encoding, xml);
	}

	@Override
	public byte[] JsonToEDIbytesLen(String projPath, String processName, String msgType, String jsonString,
			String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.JsonToEDIbytesLen(projPath, processName, msgType, jsonString, encoding, xml);
	}

	@Override
	public String EDIbytesToJson(String projPath, String processName, String msgType, byte[] ediBytes, String encoding,
			String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.EDIbytesToJson(projPath, processName, msgType, ediBytes, encoding, xml);
	}

	@Override
	public String toEDI(String projPath, String processName, String msgType, String jsonString, String encoding,
			String xml) throws Exception {
	       String ret;
	        String encodedString = new String(jsonString.getBytes(encoding), encoding);
	        Transformer trans = new Transformer(schemaPath, ifId);
	        String streamName = (msgType.startsWith("_")) ? ifId + msgType : msgType;
	        ret = trans.toEDI(streamName, encodedString, encoding, null);
	        return ret;
	}

	@Override
	public String baseMapping(String targetSystemId, String jsonString) {
		// TODO Auto-generated method stub
		return super.baseMapping(targetSystemId, jsonString);
	}

	@Override
	public byte[] concatBytes(byte[] bytes1, byte[] bytes2) {
		// TODO Auto-generated method stub
		return super.concatBytes(bytes1, bytes2);
	}

	@Override
	public String toJSON(String projPath, String processName, String msgType, String ediString, String encoding,
			String xml) throws Exception {
        String ret;
        Transformer trans = new Transformer(schemaPath, ifId);
        String streamName = (msgType.startsWith("_")) ? ifId + msgType : msgType;
        ret = new String(trans.toJSON(streamName, ediString, encoding, null).getBytes(encoding), encoding);
        return ret;
	}

	@Override
	public byte[] toBytes(String str, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.toBytes(str, encoding);
	}

	@Override
	public String toString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.toString(bytes, encoding);
	}

	@Override
	public String encodeCharset(String data, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.encodeCharset(data, encoding);
	}

	@Override
	public String envelopHcis(String dataJson) {
		// TODO Auto-generated method stub
		return super.envelopHcis(dataJson);
	}

	@Override
	public String developHcis(String cisJson) {
		// TODO Auto-generated method stub
		return super.developHcis(cisJson);
	}

	@Override
	public Map<String, Object> getHcisStdHeader(Map<String, Object> inf) {
		// TODO Auto-generated method stub
		return super.getHcisStdHeader(inf);
	}

	
	
	
}
