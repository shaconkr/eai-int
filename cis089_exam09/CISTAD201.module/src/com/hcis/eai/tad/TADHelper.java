package com.hcis.eai.tad;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcis.eai.common.HCISHelper;

public class TADHelper extends HCISHelper {
	private static final Logger log = LoggerFactory.getLogger(TADHelper.class);

	public TADHelper() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 해당 시스템에 필요한 메소드 추가영역
	 */
	
	
    /**
     * 전제전문길이(8)  필요한 전문 - TAD
     *
     * @param projPath
     * @param processName
     * @param msgType
     * @param jsonString
     * @param encoding
     * @param xml
     * @return
     * @throws Exception
     */
    public byte[] JsonToEDIbytesLen(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("## InterfaceID [{}] moduleName [{}] packageName [{}]  processName [{}]  ", interfaceId, moduleName, packageName, processName);
        String edi = toEDI(msgType,jsonString,encoding,xml);
        byte[] bytes = edi.getBytes(encoding);
        byte[] totalLength = String.format("%08d", bytes.length).getBytes(encoding);
        System.arraycopy(totalLength, 0, bytes, 0, totalLength.length);
        return bytes;
    }


    /**
     * 이하 메소드는  Override 해서 사용 영역
     */    


	@Override
	public String getModuleProperty(String key) {
		// TODO Auto-generated method stub
		return super.getModuleProperty(key);
	}


	@Override
	public void logModuleProperties() {
		// TODO Auto-generated method stub
		super.logModuleProperties();
	}



	@Override
	public void setProcessName(String processName) {
		// TODO Auto-generated method stub
		super.setProcessName(processName);
	}

	@Override
	public byte[] JsonToEDIbytes(String msgType, String jsonString, String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.JsonToEDIbytes(msgType, jsonString, encoding, xml);
	}



	@Override
	public String EDIbytesToJson(String msgType, byte[] ediBytes, String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.EDIbytesToJson(msgType, ediBytes, encoding, xml);
	}



	@Override
	public String toEDI(String msgType, String jsonString, String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.toEDI(msgType, jsonString, encoding, xml);
	}



	@Override
	public String baseMapping(String targetSystemId, String jsonString) {
		// TODO Auto-generated method stub
		return super.baseMapping(targetSystemId, jsonString);
	}



	@Override
	public List<Map<String, Object>> loadSystemHeader(String sysId) {
		// TODO Auto-generated method stub
		return super.loadSystemHeader(sysId);
	}



	@Override
	public byte[] concatBytes(byte[] bytes1, byte[] bytes2) {
		// TODO Auto-generated method stub
		return super.concatBytes(bytes1, bytes2);
	}



	@Override
	public String toJSON(String msgType, String ediString, String encoding, String xml) throws Exception {
		// TODO Auto-generated method stub
		return super.toJSON(msgType, ediString, encoding, xml);
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
	public String envelopHcisReqHdr(String dataJson, String reqJson) {
		// TODO Auto-generated method stub
		return super.envelopHcisReqHdr(dataJson, reqJson);
	}



	@Override
	public String developHcis(String cisJson) {
		// TODO Auto-generated method stub
		return super.developHcis(cisJson);
	}



	@Override
	public String getEimsJson() {
		// TODO Auto-generated method stub
		return super.getEimsJson();
	}



	@Override
	public Map<String, Object> getEimsMap() {
		// TODO Auto-generated method stub
		return super.getEimsMap();
	}



	@Override
	public Map<String, Object> getHcisStdHeader(Map<String, Object> inf) {
		// TODO Auto-generated method stub
		return super.getHcisStdHeader(inf);
	}



	@Override
	public void dumpString(String str, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		super.dumpString(str, encoding);
	}



	@Override
	public void dumpBytes(byte[] bytes, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		super.dumpBytes(bytes, encoding);
	}



	@Override
	public byte[] getBytes(String data, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.getBytes(data, encoding);
	}



	@Override
	public String bytesToHexString(byte[] bytes) {
		// TODO Auto-generated method stub
		return super.bytesToHexString(bytes);
	}



	@Override
	public byte[] hexToBytes(String hex) {
		// TODO Auto-generated method stub
		return super.hexToBytes(hex);
	}



	@Override
	public String hexStringToAsciiString(String hex, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.hexStringToAsciiString(hex, encoding);
	}



	@Override
	public String asciiStringToHexString(String data, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.asciiStringToHexString(data, encoding);
	}



	@Override
	public String bytesToBase64String(byte[] bytes) {
		// TODO Auto-generated method stub
		return super.bytesToBase64String(bytes);
	}



	@Override
	public String asciiStringToBase64String(String data, String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.asciiStringToBase64String(data, encoding);
	}



	@Override
	public void unCompressGz(String source, String target) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		super.unCompressGz(source, target);
	}



	@Override
	public byte[] setTotalLength(byte[] bytes, int len, int offset, boolean flag, String encoding)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.setTotalLength(bytes, len, offset, flag, encoding);
	}



	@Override
	public ArrayList<String> stringArrayToList(String rString) {
		// TODO Auto-generated method stub
		return super.stringArrayToList(rString);
	}
    
    
    
}
