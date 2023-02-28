package com.hcis.eai.tad;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.beanio.BeanIOConfigurationException;
import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcis.eai.common.EDITransformer;
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
     * @throws Exception 
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
		StreamFactory factory = StreamFactory.newInstance();
        String headPath   = "/Schemas/" +sysId + "Header.xsd";
        Class<?> ancClass = HCISHelper.class;
        URL location   = ancClass.getProtectionDomain().getCodeSource().getLocation();
        String ancPath = location.getPath();
        String path    = ancPath + headPath;
        TibXsdParser parser = new TibXsdParser();
        InputStream is = null;
        String check   = "";

        List<Map<String, Object>> rtnMap = null;
        try {
        	is = new FileInputStream(new File(path));
        	factory.load(is);
        	rtnMap = parser.parseXsdFromClasspath(is, "header");
        	is.close();
        } catch (FileNotFoundException e) {
        	check = "CLA";
        	log.error("Local Test System Header Not Found  {}", path);
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	if (check.equals("CLA")) {
           		is = getClass().getResourceAsStream(headPath);
        		if (is==null) {
        			log.error("Class System Header Not Found  {}",path);
        		} else {
            		rtnMap = parser.parseXsdFromClasspath(is, "header");
        		}
        	}
        }

        return rtnMap;
	}

	@Override
    public byte[] JsonToEDIbytes(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("@@@@ BeanIO {}", xml);
        String edi = toEDI2(msgType,jsonString ,encoding, xml);
        return edi.getBytes(encoding);
    }

//	@Override
    public String toEDI2(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("@@@@ BeanIO {}", xml);
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId = getInterfaceId();
        String xmlfile = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;        
//        CISTADTransformer trans = new CISTADTransformer();
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("## streamName [{}] xml [{}] msgType [{}] ", streamName, xmlfile, msgType);
//        return toEDI3(streamName, encodedString, encoding, getBeanXmlInputStream(xmlfile));
        String edi = toEDI3(streamName, encodedString, encoding, getBeanXmlInputStream(xmlfile));
log.debug("sbYi     CISTADHelper.toEDI2   edi : {} ", edi);
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
//        return  trans.toEDI(streamName, encodedString, encoding, getBeanXmlInputStream(xmlfile)); 
    }

//	public String toEDI3(String msgType, String jsonString, String encoding, String xmlpath) {
//log.debug("sbYi    CISTADHelper.toEDI3    0");
//    	StreamFactory factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
//        Marshaller marshaller = factory.createMarshaller(msgType);
//        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
//        return marshaller.marshal(map,encoding).toString();
//    }

    public String toEDI3(String msgType, String jsonString, String encoding, InputStream is) {
log.debug("sbYi    CISTADHelper.toEDI3    1");
    	StreamFactory factory = newStreamFactory(is);
        Marshaller marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
    }

/*
	@Override
    public String toEDI_Old(String msgType, String jsonString, String encoding, String xml) throws Exception {
        String encodedString = new String(jsonString.getBytes(encoding), encoding);

        String interfaceId   = getInterfaceId();

        CISTADTransformer trans = new CISTADTransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;

        log.debug("## streamName [{}] xml [{}] msgType [{}] ", streamName, xml, msgType);
        String edi =  trans.toEDI(streamName, encodedString, encoding, xml);
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
    }
*/

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
        return toJSON(msgType, new String(ediBytes, encoding), encoding, xml);
    }

	public String toJSON(String msgType, String ediString, String encoding, String xml) throws Exception {
log.debug("sbYi  0 -----------------  CISTADHelper.toJSON   ");
        String interfaceId = getInterfaceId();
log.debug("sbYi  1 -----------------  CISTADHelper.toJSON  interfaceId : {} ", interfaceId);
        String xmlfile = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;
log.debug("sbYi  2 -----------------  CISTADHelper.toJSON      xmlfile : {} ", xmlfile);
//        CISTADTransformer trans = new CISTADTransformer();
log.debug("sbYi  3 -----------------  CISTADHelper.toJSON    ");
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
log.debug("sbYi  4 -----------------  CISTADHelper.toJSON   streamName : {} ", streamName);
        log.debug("## BeanIO {} , {} , {} ", streamName, xmlfile, msgType);        
        InputStream ism = getBeanXmlInputStream(xmlfile);
log.debug("sbYi  5 -----------------  CISTADHelper.toJSON    ");
        return new String(toJSON3(streamName, ediString, encoding, ism).getBytes(encoding), encoding);
//        return new String(toJSON3(streamName, ediString, encoding, getBeanXmlInputStream(xmlfile)).getBytes(encoding), encoding);
//        return new String(trans.toJSON(streamName, ediString, encoding, getBeanXmlInputStream(xmlfile)).getBytes(encoding), encoding);
    }

//	public String toJSON3(String msgType, String ediString, String encoding, String xmlpath) {
//log.debug("sbYi  0 0 -----------------  CISTADHelper.toJSON3   ");
//    	StreamFactory     factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
//log.debug("sbYi  1 0 -----------------  CISTADHelper.toJSON3   ");
//        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
//log.debug("sbYi  2 0 -----------------  CISTADHelper.toJSON3   ");
//        Object o = unmarshaller.unmarshal(ediString, encoding);
//log.debug("sbYi  3 0 -----------------  CISTADHelper.toJSON3   ");
//        return gson.toJson(o, Map.class);
//    }
    
    public String toJSON3(String msgType, String ediString, String encoding, InputStream is) {
log.debug("sbYi  0 1 -----------------  CISTADHelper.toJSON3    ediString : {}", ediString);
    	StreamFactory     factory = newStreamFactory(is);
log.debug("sbYi  1 1 -----------------  CISTADHelper.toJSON3     encoding : {}", encoding);
        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
log.debug("sbYi  2 1 -----------------  CISTADHelper.toJSON3      msgType : {}", msgType);
        Object o = unmarshaller.unmarshal(ediString, encoding);
log.debug("sbYi  3 1 -----------------  CISTADHelper.toJSON3    ");
        return gson.toJson(o, Map.class);
    }

//    public String EDIbytesToJson(String msgType, byte[] ediBytes, String encoding, String xml) throws Exception {
//        return toJSON2(msgType, new String(ediBytes, encoding), encoding, xml );
//    }

//	@Override
//    public String toJSON2(String msgType, String ediString, String encoding, String xml) throws Exception {
//        String interfaceId = getInterfaceId();
//        String xmlfile     = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;
//        CISTADTransformer trans = new CISTADTransformer();
//        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;       
//        log.debug("## BeanIO {} {} {} ", streamName, xmlfile, msgType);        
//        return new String(trans.toJSON(streamName, ediString, encoding, getBeanXmlInputStream(xmlfile)).getBytes(encoding), encoding);
//    }
/*
	@Override
    public String toJSON_Old(String msgType, String ediString, String encoding, String xml) throws Exception {
        String interfaceId = getInterfaceId();
        CISTADTransformer trans = new CISTADTransformer(interfaceId);
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
log.debug("sbYi  3 -----------------  CISTADHelper.toJSON        streamName : {} ", streamName);
        return new String(trans.toJSON(streamName, ediString, encoding, xml).getBytes(encoding), encoding);
//        return new String(trans.toJSON(streamName, ediString, encoding, null).getBytes(encoding), encoding);
    }
*/	
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

	@Override    
    public String toString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString -------------------------------------------------");
log.debug("sbYi      CISTADHelper.toString     bytes : {} ", bytes);
log.debug("sbYi      CISTADHelper.toString  encoding : {} ", encoding);
		String abc = new String(bytes, encoding);
log.debug("sbYi      CISTADHelper.toString       abc : {} ", abc);
    	return new String(bytes, encoding);
    }
    /**
     * 고정길이전문 정의 XML문자열로 EDI Factory 생성
     */
    public StreamFactory newStreamFactory(InputStream is) {
        StreamFactory factory = StreamFactory.newInstance();
        try {
			factory.load(is);
		} catch (BeanIOConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return factory;
    }
}
