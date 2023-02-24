package com.hcis.eai.tad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcis.eai.common.HCISHelper;
import com.hcis.eai.common.types.GsonMapDeserializer;

import kr.shacon.format.TibXsdParser;
import kr.shacon.util.CastUtils;


public class CISTADHelper<N> extends HCISHelper<N> {
	
    private static final Logger log = LoggerFactory.getLogger(CISTADHelper.class);
    private final Gson  gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Map.class, new GsonMapDeserializer())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

    /**
     * target 전문으로 변경 ( target header )
     * 
     * Inbound to Outbound Message Mapping
     *
     * Json Layout ( header - data )
     * header mapping ( target 기준 존재 하는 항목만 , 진행번호증가, 헤더길이)
     * data bypass
     *
     * @return
     */
	@Override
    @SuppressWarnings("unchecked")
    public String baseMapping(String targetSystemId, String jsonString) {
        Map<String,Object> sourceMessage= gson.fromJson(jsonString, Map.class);
        Map<String,Object> sourceHeader = CastUtils.cast((Map<?, ?>) sourceMessage.get("header"));
        Map<String,Object> sourceBody   = CastUtils.cast((Map<?, ?>) sourceMessage.get("data"));
        Map<String,Object> targetHeader = new LinkedHashMap<>();

        List<Map<String, Object>> targetHeaderLayout = null;
        try {
        	targetHeaderLayout = loadSystemHeader(targetSystemId);
        } catch(Exception e) {
        	e.getMessage();
        } finally {
            if (targetHeaderLayout == null) {
            	targetHeaderLayout = localLoadSystemHeader(targetSystemId);
            }
        }
//        List<Map<String, Object>> targetHeaderLayout = loadSystemHeader(targetSystemId);
//        if (targetHeaderLayout.isEmpty()) {
//        	targetHeaderLayout = localLoadSystemHeader(targetSystemId);
//        }
        int headerLength = 0;
        for (Map<String, Object> m : targetHeaderLayout) {
            int colLen = Integer.parseInt(String.valueOf(m.get("length")));
            headerLength += colLen;
            String key = String.valueOf(m.get("name"));
            targetHeader.put(key, Objects.isNull(sourceHeader.get(key))?new String():sourceHeader.get(key));
        }
        targetHeader.put("prgrNo", Integer.parseInt(String.valueOf(targetHeader.get("prgrNo"))) + 1); // 진행번호증가
        targetHeader.put("hdrLen", headerLength); // system 별 header 길이 게산

        return gson.toJson(ImmutableMap.of("header", targetHeader, "data", sourceBody));
    }

	public List<Map<String, Object>> localLoadSystemHeader(String sysId) {
        String headPath   = "Schemas/" +sysId + "Header.xsd";
        Class<?> ancClass = HCISHelper.class;
        URL location   = ancClass.getProtectionDomain().getCodeSource().getLocation();
        String ancPath = location.getPath();
        String path    = ancPath + headPath;

        List<Map<String, Object>> rtnMap = null;
        try {
        	InputStream is = new FileInputStream(new File(path));
        	TibXsdParser parser = new TibXsdParser();
        	rtnMap = parser.parseXsdFromClasspath(is, "header");
        	is.close();
        } catch (FileNotFoundException e) {
        	log.error("Local Test System Header Not Found  {}", path);
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return rtnMap;
	}

//    /**
//     * System 별 Header XSD Loading
//     *
//     * @param sysId
//     * @return
//     */
//    public List<Map<String, Object>> ancLoadSystemHeader(String pPath) {
//        InputStream is = getClass().getResourceAsStream(pPath);
//        if (is == null) {
//        	log.error("Ancestor System Header Not Found  {}",pPath);
//        }
//
//        return new TibXsdParser().parseXsdFromClasspath(is, "header");        
//    }

	@Override
    public String EDIbytesToJson(String msgType, byte[] ediBytes, String encoding, String xml) throws Exception {
log.debug("sbYi  0 -----------------  CISTADHelper.EDIbytesToJson ");
        return toJSON(msgType, new String(ediBytes, encoding), encoding, xml );
    }

	@Override
    public String toEDI(String msgType, String jsonString, String encoding, String xml) throws Exception {
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId   = getInterfaceId();
        CISTADTransformer trans = new CISTADTransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("## streamName [{}] xml [{}] msgType [{}] ", streamName, xml, msgType);
        String edi =  trans.toEDI(streamName, encodedString, encoding, xml);
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
    }
	
	@Override
    public String toJSON(String msgType, String ediString, String encoding, String xml) throws Exception {
        String interfaceId = getInterfaceId();
        CISTADTransformer trans = new CISTADTransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
log.debug("sbYi  3 -----------------  CISTADHelper.toJSON        streamName : {} ", streamName);
        return new String(trans.toJSON(streamName, ediString, encoding, xml).getBytes(encoding), encoding);
//        return new String(trans.toJSON(streamName, ediString, encoding, null).getBytes(encoding), encoding);
    }
	
	@Override
	public String logEDIBytes(byte[] bytes, String xsdpath) {
		InputStream is = getClass().getResourceAsStream(xsdpath);
		try {
			if(is.available() > 0 ) {
				log.debug("@@@   CISTADHelper   Found O {}", xsdpath);
			}else {
				log.debug("@@@   CISTADHelper   Not Found O {}", xsdpath);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		TibXsdParser parser = new TibXsdParser();
    	List<Map<String, Object>> layout = parser.parseXsdFromClasspath(is, "root");
    	StringBuffer sb = new StringBuffer();
    	int offset = 0;
    	logEDIItem(sb, layout, bytes, offset);    	
    	return sb.toString();
    }
}
