package com.hcis.eai.module;

import kr.shacon.format.Transformer2;

/**
 * Module 내의 classpath 만 적용됨으로 beanio xml 를 loading 하기위해 가져옴. 
 * 
 * @author javal
 *
 */
public class Transformer extends Transformer2 {

	public Transformer() {
		// TODO Auto-generated constructor stub
	}

	public Transformer(String schemaPath, String ifId) {
		super(schemaPath, ifId);
		// TODO Auto-generated constructor stub
	}

}
