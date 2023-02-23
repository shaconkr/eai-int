package com.hcis.eai.tad;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.internal.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.common.base.Strings;
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

	public CISTADTransformer(String ifId) {
		super(ifId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected StreamFactory newStreamFactory(String xmlpath) {
    	log.debug("@@@ CISTADTransformer load xml {}", xmlpath);
        StreamFactory factory = StreamFactory.newInstance();
		InputStream is = getClass().getResourceAsStream(xmlpath);
		try {
			if (is.available() > 0) {
				log.debug("@@@   CISTADTransformer   Found O {}", xmlpath);
			} else {
				log.debug("@@@   CISTADTransformer   Not Found O {}", xmlpath);
			}
		} catch (IOException e1) {
			log.debug("@@@   CISTADTransformer   IOException e1 {}", e1.toString());
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
        try {
            factory.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(is);
        }
        return factory;
	}

	@Override
	public String toEDI(String msgType, String jsonString, String encoding, String xml) {
log.debug("sbYi ------------------  CISTADTransformer.toEDI ");
//		return toEDI2(msgType, jsonString, encoding, xml);
		return super.toEDI(msgType, jsonString, encoding, xml);
	}

	public String toEDI2(String msgType, String jsonString, String encoding, String xml) {
        if(!Strings.isNullOrEmpty(xml)) this.factory2 = newStreamFactory(xml);
log.debug("sbYi 0 ----------------  CISTADTransformer.toEDI2    msgType : {}", msgType);
        Marshaller marshaller = factory2.createMarshaller(msgType);
log.debug("sbYi 1 ----------------  CISTADTransformer.toEDI2 jsonString : {}", jsonString);
log.debug("sbYi 1 ----------------  CISTADTransformer.toEDI2   encoding : {}", encoding);
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);
log.debug("sbYi 2 ----------------  CISTADTransformer.toEDI2        map : {}", map);

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
	public String toJSON(String msgType, String ediString, String encoding, String xml) {
		// TODO Auto-generated method stub
		return super.toJSON(msgType, ediString, encoding, xml);
	}

}
