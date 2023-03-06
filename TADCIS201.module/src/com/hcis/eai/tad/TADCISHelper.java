package com.hcis.eai.tad;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.beanio.BeanIOConfigurationException;
import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcis.eai.common.HCISHelper;
import com.hcis.eai.common.types.GsonMapDeserializer;

import kr.shacon.format.TibXsdParser;


public class TADCISHelper<N> extends HCISHelper<N> {
	
    private static final Logger log = LoggerFactory.getLogger(TADCISHelper.class);
    private final Gson  gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Map.class, new GsonMapDeserializer())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

	@Override
    public byte[] JsonToEDIbytes(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("@@@@ BeanIO {}", xml);
        String edi = toEDI2(msgType,jsonString ,encoding, xml);
        return edi.getBytes(encoding);
    }

	public String toEDI2(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("@@@@ BeanIO {}", xml);
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId = getInterfaceId();
        String xmlfile = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;        
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("## streamName [{}] xml [{}] msgType [{}] ", streamName, xmlfile, msgType);
        String edi = toEDI3(streamName, encodedString, encoding, getBeanXmlInputStream(xmlfile));
log.debug("sbYi     CISTADHelper.toEDI2   edi : {} ", edi);
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
    }

	public String toEDI3(String msgType, String jsonString, String encoding, InputStream is) {
log.debug("sbYi    CISTADHelper.toEDI3    1");
    	StreamFactory factory = newStreamFactory(is);
        Marshaller marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
    }

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
	
	@Override
	public String logEDIBytes(byte[] bytes, String xsdpath) {
		InputStream is = getClass().getResourceAsStream(xsdpath);
		try {
			if(is.available() > 0 ) {
				log.debug("@@@ Found O {}", xsdpath);
			}else {
				log.debug("@@@ Not Found O {}", xsdpath);
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
