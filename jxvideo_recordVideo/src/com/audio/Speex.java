package com.audio;

public class Speex {

	private static final int DEFAULT_COMPRESSION = 9;

	public Speex() {
	}

	public void init() {
		load();
		open(DEFAULT_COMPRESSION);
	}

	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public native int open(int compression);

	public native int getFrameSize();

	public native int decode(byte encoded[], short lin[], int size);

	public native int encode(byte[] lin, byte[] encoded);

	public native void close();

}
