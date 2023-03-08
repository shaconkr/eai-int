package com.hcis.eai.tadcistest;

import java.io.UnsupportedEncodingException;

public class TADCISTest {
	
	public TADCISTest() {
	}

    public byte[] toBytes(String str, String encoding) throws UnsupportedEncodingException {
    	return str.getBytes(encoding);
    }

    public String toString(byte[] bytes, String encoding ) throws UnsupportedEncodingException {
    	return new String(bytes, encoding);
    }
}