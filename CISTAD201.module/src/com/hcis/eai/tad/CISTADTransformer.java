package com.hcis.eai.tad;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.shacon.format.Transformer;
import kr.shacon.types.MapDeserializer;

public class CISTADTransformer extends Transformer {
    private static final Logger log = LoggerFactory.getLogger(Transformer.class);
    StreamFactory factory2;

    protected Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Map.class, new MapDeserializer())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();

    public CISTADTransformer() {
	}
/*
	public CISTADTransformer_Old(String ifId) {
		super(ifId);
	}
*/
//	@Override
    public StreamFactory newStreamFactory2(String xmlpath) {
log.debug("sbYi 0 -------  CISTADTransformer.newStreamFactory   xmlpath : {} ", xmlpath);
        StreamFactory factory = StreamFactory.newInstance();
log.debug("sbYi 1 -------  CISTADTransformer.newStreamFactory ");
		InputStream is = getClass().getResourceAsStream(xmlpath);
log.debug("sbYi 2 -------  CISTADTransformer.newStreamFactory        is : {} ", is);
		String xmlString = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
log.debug("sbYi 3 -  CISTADTransformer.newStreamFactory START xmlString : {} ", xmlString);
log.debug("sbYi 3 -  CISTADTransformer.newStreamFactory   END xmlString ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
log.debug("sbYi 3 --------------------------------------------------------------------------------------------------------------------------------------------------------------- ");

        factory.load(xmlString);
log.debug("sbYi 4 -------  CISTADTransformer.newStreamFactory ");
        return factory;
    }

	@Override
	public String toEDI(String msgType, String jsonString, String encoding, String xmlpath) {             
    	StreamFactory factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
        Marshaller marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
    }

	public String toEDI2_Old(String msgType, String jsonString, String encoding, String xml) {
log.debug("sbYi   ----------------  CISTADTransformer.toEDI2_Old        xml : {} ", xml);
        if(!Strings.isNullOrEmpty(xml)) this.factory2 = newStreamFactory2(xml);
log.debug("sbYi 0 ----------------  CISTADTransformer.toEDI2_Old    msgType : {} ", msgType);
        Marshaller marshaller = factory2.createMarshaller(msgType);
log.debug("sbYi 1 ----------------  CISTADTransformer.toEDI2_Old jsonString : {} ", jsonString);
log.debug("sbYi 1 ----------------  CISTADTransformer.toEDI2_Old   encoding : {} ", encoding);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
log.debug("sbYi 2 ----------------  CISTADTransformer.toEDI2_Old        map : {}", map);

Marshaller ms = null;
try {
ms = marshaller.marshal(map, encoding);
} catch (Exception e) {
System.out.println("s ------------------------------------------------------------------------------------------------------------------------------------------------------------------");
System.out.println(e);
System.out.println("m ------------------------------------------------------------------------------------------------------------------------------------------------------------------");
System.out.println(e.toString());
System.out.println("e ------------------------------------------------------------------------------------------------------------------------------------------------------------------");
}

String rtn = ms.toString();
        return rtn;
    }

    @Override
    public String toJSON(String msgType, String ediString, String encoding, String xmlpath) {
    	StreamFactory factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
        Object  o = unmarshaller.unmarshal(ediString, encoding);
        return gson.toJson( o, Map.class);
    }
//    public String toJSON(String msgType, String ediString, String encoding, String xmlpath) {
//    	StreamFactory factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
//        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
//        Object  o = unmarshaller.unmarshal(ediString, encoding);
//        return gson.toJson( o, Map.class);
//    }

//	@Override
	public String toJSON_Old(String msgType, String ediString, String encoding, String xml) {
		// TODO Auto-generated method stub
		return super.toJSON(msgType, ediString, encoding, xml);
	}

}
