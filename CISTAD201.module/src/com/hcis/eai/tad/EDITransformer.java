package com.hcis.eai.tad;

import java.io.IOException;
import java.io.InputStream;

import org.beanio.StreamFactory;
import org.beanio.internal.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.shacon.format.Transformer;

public class EDITransformer extends Transformer {
    private static final Logger log = LoggerFactory.getLogger(Transformer.class);
	
	public EDITransformer(String ifId) {
		super(ifId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected StreamFactory newStreamFactory(String xmlpath) {
    	log.debug("@@@@@@@@@@@@ load xml {}", xmlpath);
        StreamFactory factory = StreamFactory.newInstance();
		InputStream is = getClass().getResourceAsStream(xmlpath);
		try {
			if(is.available() > 0 ) {
				log.debug("@@@@@@@@@@@@@@@@@ OKOKOIOIOI");
			}else {
				log.debug("@@@@@@@@@@@@@@@@@ 바바보보보보보보OKOKOIOIOI");
			}
		} catch (IOException e1) {
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
		// TODO Auto-generated method stub
		return super.toEDI(msgType, jsonString, encoding, xml);
	}

	@Override
	public String toJSON(String msgType, String ediString, String encoding, String xml) {
		// TODO Auto-generated method stub
		return super.toJSON(msgType, ediString, encoding, xml);
	}

}
