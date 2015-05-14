package com.bsht;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class AudioRecorder extends Thread {

	private MediaRecorder mediarecorder;
	private LocalSocket sender;
	private LocalSocket received;
	private LocalServerSocket lss;
	private static final int BUFFER_SIZE=5000;
	Button sendButton;
	Button receivedButton;
	private InputStream inputStream;
	private BufferedOutputStream bufferedoutstream;
	private RandomAccessFile raf;
	private boolean ISrunning;
	private Thread getAudio;
	
	byte[] buffer=new byte[1024];
	int length=0;
	int len=0;
	int readlength=0;
	boolean mMediaRecorderRecording = false;
	
	
	public AudioRecorder() {
	}
	
	public void KillThread() throws IOException {

		if(mMediaRecorderRecording)
		{
			releaseMediaRecorder();
			try {
				lss.close();
				received.close();
				sender.close();
			} catch (IOException e) {
	
			}
			mMediaRecorderRecording = false;
		}
		
		if (inputStream!=null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inputStream=null;
		}

		this.interrupt();
	}
	
	public void MediaRecoderInit(){
		InitLocalSocket();
		File file=new File("/sdcard/LocalSocket.aac");
		try {
			raf=new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (mediarecorder==null) {
			mediarecorder=new MediaRecorder();
		}
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediarecorder.setOutputFile(sender.getFileDescriptor());
		try {
			mediarecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediarecorder.start();
		mMediaRecorderRecording = true;
	}
	
	//释放MediaRecorder资源
	private void releaseMediaRecorder(){
		if(mediarecorder != null) {
			if(mMediaRecorderRecording) {
				mediarecorder.stop();
				mMediaRecorderRecording = false;
			}
			mediarecorder.reset();
			mediarecorder.release();
			mediarecorder = null;
		}
	}
	
	
	public void run() {
		try {
			while(ISrunning){	
			while(readlength<1024){
					readlength +=inputStream.read(buffer,readlength,1024-readlength);
					Log.v("fds", "readlength--->"+readlength);
				}
			raf.write(buffer,0,readlength);
			readlength=0;
			}				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void InitLocalSocket(){
		received=new LocalSocket();
		
		try {
			lss=new LocalServerSocket("aac");//Ïàµ±ÓÚserversocekt¿ª¶Ë¿Ú
			
			received.connect(new LocalSocketAddress("aac"));//Ïàµ±ÓÚsocket¿Í»§¶Ë
			received.setReceiveBufferSize(BUFFER_SIZE);
			received.setSendBufferSize(BUFFER_SIZE);
			
			sender=lss.accept();//Ïàµ±ÓÚserversocketµÈ´ýÁ¬½Ó£¬³É¹¦·µ»ØÒ»¸ölocalsocket
			sender.setReceiveBufferSize(BUFFER_SIZE);
			sender.setSendBufferSize(BUFFER_SIZE);
			inputStream = received.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
}
