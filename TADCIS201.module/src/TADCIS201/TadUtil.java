package TADCIS201;

import java.io.UnsupportedEncodingException;

public class TadUtil {

	protected String TAD_ENCODING = "EUC-KR";
	
	public TadUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public TadUtil(String _encoding) {
		this.TAD_ENCODING = _encoding;
	}
	
	public String getIFID(byte[] bytes) throws UnsupportedEncodingException{
		byte[] corrBytes = this.readBytes(bytes, 164, 17);//EIMS전문ID, eimsTgrmId
		return new String(corrBytes, TAD_ENCODING);
	}
	
    public byte[] readBytes(byte[] bytes, int offset, int len)  {
		byte[] readBytes = new byte[len];
		System.arraycopy(bytes, offset, readBytes, 0, len);
	    return readBytes;
	}	
}
