package com.bsht.jxvideo;

public class Jxaudio {

//	public Jxaudio()
//	  {
//	    System.loadLibrary("jxcodec");
//	  }
//	
	public native long create(int paramInt);

	public native int decode(long paramLong, byte[] paramArrayOfByte1,
			int paramInt1, byte[] paramArrayOfByte2, int paramInt2);

	public native int destroy(long paramLong);
}

/*
 * Location: D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar Qualified
 * Name: com.bsht.jxvideo.Jxaudio JD-Core Version: 0.6.0
 */