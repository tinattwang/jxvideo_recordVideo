package com.bsht.jxvideo;

public class Jxcodec {
	public Jxcodec() {
		System.loadLibrary("jxcodec");
 
	}

	public native long create(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4, int paramInt5, int paramInt6);

	public native int destroy(long paramLong);

	public native int encode(long paramLong, byte[] paramArrayOfByte1,
			byte[] paramArrayOfByte2, int paramInt);
}

/*
 * Location: D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar Qualified
 * Name: com.bsht.jxvideo.Jxcodec JD-Core Version: 0.6.0
 */