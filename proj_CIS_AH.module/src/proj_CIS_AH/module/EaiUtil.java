package proj_CIS_AH.module;

import java.io.UnsupportedEncodingException;

public class EaiUtil {
	
	public EaiUtil() {

	}

	public String getCnvStrEnc(String encStr, String charset) {

		String rtnStr = "";
				
		try {
			rtnStr = new String(encStr.getBytes(charset), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return rtnStr;
	}
	
	public int getByteLength(String lenStr, String charset) {
		
		int rtnLength = 0;
		
		try {
			rtnLength = lenStr.getBytes(charset).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return rtnLength;
	}
}
