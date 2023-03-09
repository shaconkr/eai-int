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
    	log.debug("@@@@ TADCISHelper.JsonToEDIbytes BeanIO {}", xml);
        String edi = toEDI2(msgType,jsonString ,encoding, xml);
        return edi.getBytes(encoding);
    }

	public String toEDI2(String msgType, String jsonString, String encoding, String xml) throws Exception {
    	log.debug("@@@@ TADCISHelper.toEDI2 BeanIO {}", xml);
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId = getInterfaceId();
        String xmlfile = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;        
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("@@@@ TADCISHelper.toEDI2 streamName : {} , xml : {} , msgType : {} ", streamName, xmlfile, msgType);
        String edi = toEDI3(streamName, encodedString, encoding, getBeanXmlInputStream(xmlfile));
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
    }

	@SuppressWarnings("unchecked")
	public String toEDI3(String msgType, String jsonString, String encoding, InputStream is) {
    	log.debug("@@@@ TADCISHelper.toEDI3 Run");
    	StreamFactory   factory = newStreamFactoryLocal(is);
        Marshaller   marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
    }

	@Override
	public String EDIbytesToJson(String msgType, byte[] ediBytes, String encoding, String xml) throws Exception {
        return toJSON(msgType, new String(ediBytes, encoding), encoding, xml);
    }

	public String toJSON(String msgType, String ediString, String encoding, String xml) throws Exception {
        String interfaceId = getInterfaceId();
        String     xmlfile = Strings.isNullOrEmpty(xml) ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;
        String  streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
        log.debug("@@@@ TADCISHelper.toJSON  BeanIO {} , {} , {} ", streamName, xmlfile, msgType);        
        InputStream    ism = getBeanXmlInputStream(xmlfile);
        return new String(toJSON3(streamName, ediString, encoding, ism).getBytes(encoding), encoding);
    }

    public String toJSON3(String msgType, String ediString, String encoding, InputStream is) {
    	log.debug("@@@@ TADCISHelper.toJSON3 Run");
    	StreamFactory     factory = newStreamFactoryLocal(is);
        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
        Object      o = unmarshaller.unmarshal(ediString, encoding);
        String strRtn = gson.toJson(o, Map.class);
        return strRtn;
    }
	
	@Override
	public String logEDIBytes(byte[] bytes, String xsdpath) {
		InputStream is = getClass().getResourceAsStream(xsdpath);
		try {
			if (is.available() > 0) {
				log.debug("@@@ TADCISHelper.logEDIBytes Found O {}", xsdpath);
			} else {
				log.debug("@@@ TADCISHelper.logEDIBytes Not Found O {}", xsdpath);
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
    public StreamFactory newStreamFactoryLocal(InputStream is) {
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