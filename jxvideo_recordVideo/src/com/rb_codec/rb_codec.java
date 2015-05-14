package com.rb_codec;

import android.annotation.SuppressLint;
import android.content.Context;


public class rb_codec 
{
	 private static rb_codec __pInstance = null ;
	 private static boolean bThreadRunFlag = false  ;
	 public static rb_codec GetInstance()
	 {
		if ( __pInstance == null )
		{	 
			__pInstance = new rb_codec() ;
		}
		return __pInstance ;
	 }
	
	 
	static
	{
		System.loadLibrary("DYRNTCSystemBase");	
		System.loadLibrary("DYRNTCRTUdpSendSeviceCore");
		System.loadLibrary("DYRNTCUDPTSSendInterfaceSDK");

		System.loadLibrary("rb_codec");
	}

	public native int InitEncode(int vwidth, int vheight, int vframe, int vbitrate, int vgopsize, int asample, int achannels, int abitrate, String ip, int port,int ID);
	public native int UnInitEncode();
	public native int EncodeYUVToRTP(byte[] in, int insize);
	public native int EncodePCMToRTP(byte[] in, int insize);

	
}
