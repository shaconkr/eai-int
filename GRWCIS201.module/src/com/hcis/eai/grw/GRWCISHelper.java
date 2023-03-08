package com.hcis.eai.grw;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hcis.eai.common.HCISHelper;

public class GRWCISHelper<N> extends HCISHelper<N> {
	private static final Logger log = LoggerFactory.getLogger(GRWCISHelper.class);	
	
	@Override
    public InputStream getBeanXmlInputStream(String xmlpath) {
    	log.debug("load xml {}", xmlpath);
		return getClass().getResourceAsStream(xmlpath);
    }
}
