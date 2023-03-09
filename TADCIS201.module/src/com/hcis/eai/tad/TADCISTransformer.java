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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.shacon.format.Transformer;
import kr.shacon.types.MapDeserializer;

public class TADCISTransformer extends Transformer {
//    private static final Logger log = LoggerFactory.getLogger(Transformer.class);
    StreamFactory factory2;

    protected Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Map.class, new MapDeserializer())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();

    public TADCISTransformer() {}

    @Override
	@SuppressWarnings("unchecked")
	public String toEDI(String msgType, String jsonString, String encoding, String xmlpath) {             
    	StreamFactory   factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
        Marshaller   marshaller = factory.createMarshaller(msgType);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
        return marshaller.marshal(map,encoding).toString();
    }

    @Override
    public String toJSON(String msgType, String ediString, String encoding, String xmlpath) {
    	StreamFactory     factory = newStreamFactory(getBeanXmlInputStream(xmlpath));
        Unmarshaller unmarshaller = factory.createUnmarshaller(msgType);
        Object obj = unmarshaller.unmarshal(ediString, encoding);
        return gson.toJson(obj, Map.class);
    }

}
