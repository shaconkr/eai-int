package com.hcis.eai.tadcistest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.beanio.BeanIOConfigurationException;
import org.beanio.Marshaller;
import org.beanio.StreamFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tibco.bw.palette.shared.java.BWActivityContext;
import com.tibco.bw.runtime.ActivityContext;
import com.tibco.bw.runtime.ModulePropertyNotFoundException;
import com.tibco.bw.runtime.ModulePropertyNotRegisteredException;

//import com.sun.org.slf4j.internal.Logger;
//import com.sun.org.slf4j.internal.LoggerFactory;
//import jdk.internal.joptsimple.internal.Strings;

public class TADCISTest<N> {

//	private static final Logger log = LoggerFactory.getLogger(TADCISTest.class);
	private ActivityContext<N> activityContextLocal;	
    private final Gson  gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Map.class, new GsonMapDeserializer())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

	public TADCISTest() {
	}

    public byte[] toBytes(String str, String encoding) throws UnsupportedEncodingException {
    	return str.getBytes(encoding);
    }

    public String toString(byte[] bytes, String encoding ) throws UnsupportedEncodingException {
    	return new String(bytes, encoding);
    }

    public String jsonToFixedString(String msgType, String strJson, String encoding, String xml) throws Exception {
System.out.println("sbYi 0 --------------------------------------------------------------------------- ");
    	String strRtn = toEDI2(msgType, strJson, encoding, xml);
    	return strRtn;
    }
    
    public byte[] JsonToEDIbytes(String msgType, String jsonString, String encoding, String xml) throws Exception {
//    	log.debug("@@@@ TADCISHelper.JsonToEDIbytes BeanIO {}", xml);
        String edi = toEDI2(msgType, jsonString, encoding, xml);
        return edi.getBytes(encoding);
    }

	public String toEDI2(String msgType, String jsonString, String encoding, String xml) throws Exception {
//    	log.debug("@@@@ TADCISHelper.toEDI2 BeanIO {}", xml);
        String encodedString = new String(jsonString.getBytes(encoding), encoding);
        String interfaceId = getInterfaceIdLocal();
        String xmlfile = xml=="" ? "/Schemas/" + interfaceId.substring(0,10) + "/" + interfaceId + "/" + interfaceId + ".xml" : xml;
        String streamName = (msgType.startsWith("_")) ? interfaceId + msgType : msgType;
//        log.debug("@@@@ TADCISHelper.toEDI2 streamName : {} , xml : {} , msgType : {} ", streamName, xmlfile, msgType);
        String edi = toEDI3(streamName, encodedString, encoding, getBeanXmlInputStreamLocal(xmlfile));
        return new String(setTotalLength(edi.getBytes(encoding), 8, 0, false, encoding),encoding); 
    }

	@SuppressWarnings("unchecked")
	public String toEDI3(String msgType, String jsonString, String encoding, InputStream is) {
//    	log.debug("@@@@ TADCISHelper.toEDI3 Run");
		StreamFactory   factory = newStreamFactoryLocal(is);
        Marshaller   marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
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

    public InputStream getBeanXmlInputStreamLocal(String xmlpath) {
//    	log.debug("load xml {}", xmlpath);
		return getClass().getResourceAsStream(xmlpath);
    }

    @BWActivityContext 
	public void setActivityContext(ActivityContext<N> act) throws ModulePropertyNotFoundException, ModulePropertyNotRegisteredException {
 		this.activityContextLocal = act;
	}
    
	public String getModuleProperty(String key) throws ModulePropertyNotFoundException, ModulePropertyNotRegisteredException {
		this.activityContextLocal.registerModuleProperty(key);		
		return this.activityContextLocal.getModuleProperty(key);
	}

	public String getInterfaceIdLocal() {
		String processName = this.activityContextLocal.getProcessName();
//		log.debug("@@@@ process Name {}", processName);
//		log.debug("@@@@ Interface Id {}", StringUtils.substringBeforeLast(StringUtils.substringAfterLast(processName, "."), "_"));
		return StringUtils.substringBeforeLast(StringUtils.substringAfterLast(processName, "."), "_");	 
	}

	public byte[] setTotalLength(byte[] bytes, int len, int offset, boolean flag, String encoding) throws UnsupportedEncodingException {		
		int byteLen = (flag) ? bytes.length - len : bytes.length;		
        byte[] lengthBytes = String.format("%0"+len +"d", byteLen).getBytes(encoding);
        System.arraycopy(lengthBytes, 0, bytes, offset, lengthBytes.length);	
        return bytes;
	}
}