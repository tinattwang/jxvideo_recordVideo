package com.bsht;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bsht.AudioRecoder;
import com.bsht.net.MyCallback;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.rb_codec.rb_codec;

public class VideoRecord extends Activity implements Callback, MyCallback{

	private static final String TAG = "VideoCamera";
	AudioRecoder audio = null;
	VideoData	videoData = null;
	public final static String IP = "ip";
	public final static String PORT = "port";
	public final static String CODERATE = "codeRate";
	public final static String FRAMERATE = "frameRate";
	public final static String FBL = "fbl";
	public final static String SCREEN = "screen";

	private int bitRate;
	private int frameRate;
	private String gateway;
	private String port;
	private int ID;
	int screen = 4;	

	private MediaRecorder mMediaRecorder = null;
	private int videoWidth = 1280;
	private int videoHeight = 720;
	private int videoRate = 30;
	private int videobps = 2000 * 1000;
	private Camera camera;
	private String fd = "/sdcard/videotest.3gp";

	private final int MAXFRAMEBUFFER = 50000;//50K
	private byte[] h264frame = new byte[MAXFRAMEBUFFER];
	private final byte[] head = new byte[]{0x00,0x00,0x00,0x01};
	private RandomAccessFile file_test;

	public static final int UNINIT = 111;

	private static final boolean VERBOSE = false;
	private static final boolean DEBUG_SAVE_FILE = true;

	private static Map<Integer, CameraSize> sizeMap;
	static{
		sizeMap = new HashMap<Integer, CameraSize>();
		sizeMap.put(0, new CameraSize(176, 144));
		sizeMap.put(1, new CameraSize(320, 240));
		sizeMap.put(2, new CameraSize(480, 320));
		sizeMap.put(3, new CameraSize(640, 480));
		sizeMap.put(4, new CameraSize(1280, 720));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(1024, 1024);
		getWindow().setFlags(128, 128);
		setContentView(R.layout.main);

		CheckNetwork();
		InitSurfaceView();
		InitMediaSharePreference();
		audio = new AudioRecoder();
		videoData = new VideoData();

		Button settingBtn = (Button) findViewById(R.id.setting);

		settingBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(VideoRecord.this, SettingActivity.class);
				startActivity(intent);
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		paramMenu.add(0, 4, 0, getString(R.string.preferences));
		return super.onCreateOptionsMenu(paramMenu);
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		boolean bool = true;
		switch (paramMenuItem.getItemId()) {
		case 4:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		}
		return bool;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ConfirmQuit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//初始化SurfaceView
	private SurfaceView mSurfaceView;
	private void InitSurfaceView() {
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surface_camera);
		mSurfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
		mSurfaceView.getHolder().addCallback(this);
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//初始化，记录mdat开始位置的参数
	SharedPreferences sharedPreferences;
	private final String mediaShare = "media";
	private void InitMediaSharePreference() {
		sharedPreferences = this.getSharedPreferences(mediaShare, MODE_PRIVATE);		
	}

	private SurfaceHolder mSurfaceHolder;
	private boolean mMediaRecorderRecording = false;

	@SuppressLint("NewApi")
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		ReadPreference();
		try
		{
			camera = Camera.open();
			camera.lock();  
			camera.stopPreview(); 
			Camera.Parameters parameters = camera.getParameters();

			//get camera supported size: width and height
			List<Camera.Size> list = parameters.getSupportedPreviewSizes();
			Camera.Size supportSize = list.get(list.size() - 1);
			Camera.Size size;
			boolean result = false;

			CameraSize cSize = GetCameraSize(this.screen);
			for (int i = 0; i < list.size(); i++) {
				size = list.get(i);
				if (cSize.getWidth() == size.width && cSize.getHeight() == size.height) {
					videoWidth = size.width;
					videoHeight = size.height;
					result = true;
					break;
				}
			}

			if (!result) {
				videoWidth = supportSize.width;
				videoHeight = supportSize.height;
				Toast.makeText(getApplicationContext(), "不支此分辨率持", 1).show();
			}
			parameters.setPreviewSize(videoWidth, videoHeight);

			// 横竖屏镜头自动调整
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
				parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
				camera.setDisplayOrientation(90); // 在2.2以上可以使用
			} else {
				// 如果是横屏
				parameters.set("orientation", "landscape");
				camera.setDisplayOrientation(0); // 在2.2以上可以使用
			}

			camera.setParameters(parameters);
			camera.setPreviewDisplay(mSurfaceHolder);
			camera.startPreview(); 
			camera.unlock();  

			//			int ret = rb_codec.GetInstance().InitEncode(videoWidth, videoHeight, videoRate,
			//					videobps, 25, 44100, 1, 24000, "202.106.149.230",
			//					6666, 1);
			int ret = rb_codec.GetInstance().InitEncode(videoWidth, videoHeight, videoRate,
					videobps, 25, 44100, 1, 24000, gateway, Integer.parseInt(port), ID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceHolder = holder;
		if(!mMediaRecorderRecording) {
			InitLocalSocket();
			getSPSAndPPS();
			InitializeVideo();
			StartRecording();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(mMediaRecorderRecording)
		{
			//释放mediarecorder和audiorecorder资源
			ReleaseMediaRecorder();

			//停止camera预览
			camera.stopPreview();
			camera.release();
			camera = null;

			try {	
				mMediaRecorderRecording = false;
				//结束获取视频数据的线程
				videoData.KillThread();
				videoData = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				lss.close();
				receiver.close();
				sender.close();
			} catch (IOException e) {
				if(VERBOSE){
					Log.e(TAG, e.toString());
				}

			}
		}
	}

	//开始录像，启动线程
	private void StartRecording() {
		if(audio == null){
			audio = new AudioRecoder();			
		}
		audio.start();

		if(videoData == null){
			videoData = new VideoData();
		}
		mMediaRecorderRecording = true;
		videoData.start();
	}

	//初始化MediaRecorder

	@SuppressLint("NewApi")
	private boolean InitializeVideo(){
		if(mSurfaceHolder == null) {
			return false;
		}

		if(mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		} else {
			mMediaRecorder.reset();
		}

		mMediaRecorder.setCamera(camera);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setVideoEncodingBitRate(videobps);
		mMediaRecorder.setVideoSize(videoWidth, videoHeight);
		mMediaRecorder.setVideoFrameRate(videoRate);
		mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
		//		mMediaRecorder.setMaxDuration(0);
		//		mMediaRecorder.setMaxFileSize(0);
		mMediaRecorder.setOnErrorListener(new OnErrorListener() {

			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
				mMediaRecorderRecording = false;
				Toast.makeText(VideoRecord.this, "error", 0).show();
			}
		});
		if(SPS==null)
		{
			if(VERBOSE){
				Log.e(TAG, "==============  SPS  is null!!!!!!!!!!");
			}
			mMediaRecorder.setOutputFile(fd);
		}
		else
		{
			if(VERBOSE){
				Log.e(TAG,"=============== SPS have value!!!!!!!");
			}
			mMediaRecorder.setOutputFile(sender.getFileDescriptor());
		}

		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			ReleaseMediaRecorder();
		}

		return true;
	}

	//释放MediaRecorder资源
	private void ReleaseMediaRecorder(){
		if(mMediaRecorder != null) {
			if(mMediaRecorderRecording) {
				mMediaRecorder.stop();
				mMediaRecorderRecording = false;
				try {
					audio.KillThread();
					audio = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
		Message msg = new Message();
		msg.arg1 = UNINIT;
		mainHandler.sendMessageDelayed(msg, 1000);
	}

	public Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == UNINIT) {
				rb_codec.GetInstance().UnInitEncode();
			}
		}
	};

	//初始化LocalServerSocket LocalSocket
	LocalServerSocket lss;
	LocalSocket receiver, sender;

	private void InitLocalSocket(){
		try {
			lss = new LocalServerSocket("H264");
			receiver = new LocalSocket();

			receiver.connect(new LocalSocketAddress("H264"));
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(50000);

			sender = lss.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(50000);

		} catch (IOException e) {
			if(VERBOSE){
				Log.e(TAG, e.toString());
			}
			this.finish();
			return;
		}

	}

	//得到序列参数集SPS和图像参数集PPS,如果已经存储在本地
	private void getSPSAndPPS(){
		StartMdatPlace = sharedPreferences.getInt(
				String.format("mdata_%d%d.mdat", videoWidth, videoHeight), -1);

		if(StartMdatPlace != -1) {
			byte[] temp = new byte[100];
			try {
				FileInputStream file_in = VideoRecord.this.openFileInput(
						String.format("%d%d.sps", videoWidth,videoHeight));

				int index = 0;
				int read=0;
				while(true)
				{
					read = file_in.read(temp,index,10);
					if(read==-1) break;
					else index += read;
				}
				if(VERBOSE){
					Log.d(TAG, "=====get sps length:"+index);
				}

				SPS = new byte[index];
				System.arraycopy(temp, 0, SPS, 0, index);

				file_in.close();

				index =0;
				//read PPS
				file_in = VideoRecord.this.openFileInput(
						String.format("%d%d.pps", videoWidth,videoHeight));
				while(true)
				{
					read = file_in.read(temp,index,10);
					if(read==-1) break;
					else index+=read;
				}
				if(VERBOSE){
					Log.d(TAG, "==========get pps length:"+index);
				}

				PPS = new byte[index];
				System.arraycopy(temp, 0, PPS, 0, index);
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
				if(VERBOSE){
					Log.e(TAG, e.toString());
				}

			} catch (IOException e) {
				//e.printStackTrace();
				if(VERBOSE){
					Log.e(TAG, e.toString());
				}

			}
		} else {
			if(VERBOSE){
				Log.d(TAG,"==============StartMdatPlace = -1");
			}

			SPS = null;
			PPS = null;
		}
	}

	private void ReadSize(int h264length,DataInputStream dataInput) throws IOException, InterruptedException{
		int read = 0;
		int temp = 0;
		while(read < h264length)
		{
			temp = dataInput.read(h264frame, read, h264length-read);
			if(VERBOSE){
				Log.e(TAG, String.format("h264frame %d,%d,%d", h264length,read,h264length-read));
			}

			if(temp==-1)
			{
				if(VERBOSE){
					Log.e(TAG, "no data get wait for data coming.....");
				}

				Thread.sleep(2000);
				continue;
			}
			read += temp;
		}
	}

	//从 fd文件中找到SPS And PPS
	private byte[] SPS;
	private byte[] PPS;
	private int StartMdatPlace = 0;
	private void FindSPSAndPPS() throws Exception{
		File file = new File(fd);
		FileInputStream fileInput = new FileInputStream(file);

		int length = (int)file.length();
		byte[] data = new byte[length];

		fileInput.read(data);

		final byte[] mdat = new byte[]{0x6D,0x64,0x61,0x74};
		final byte[] avcc = new byte[]{0x61,0x76,0x63,0x43};

		for(int i=0 ; i<length; i++){
			if(data[i] == mdat[0] && data[i+1] == mdat[1] && data[i+2] == mdat[2] && data[i+3] == mdat[3]){
				StartMdatPlace = i + 4;//find mdat
				break;
			}
		}
		if(VERBOSE){
			Log.d(TAG, "StartMdatPlace:" + StartMdatPlace);
		}

		//记录到xml文件里
		String mdatStr = String.format("mdata_%d%d.mdat",videoWidth,videoHeight);
		Editor editor = sharedPreferences.edit();
		editor.putInt(mdatStr, StartMdatPlace);
		editor.commit();

		for(int i=0 ; i<length; i++){
			if(data[i] == avcc[0] && data[i+1] == avcc[1] && data[i+2] == avcc[2] && data[i+3] == avcc[3]){
				int sps_start = i+3+7;//其中i+3指到avcc的c，再加7跳过6位AVCDecoderConfigurationRecord参数

				//sps length and sps data
				byte[] sps_3gp = new byte[2];//sps length
				sps_3gp[1] = data[sps_start];
				sps_3gp[0] = data[sps_start + 1];
				int sps_length = bytes2short(sps_3gp);
				if(VERBOSE){
					Log.d(TAG, "sps_length :" + sps_length);
				}


				sps_start += 2;//skip length
				SPS = new byte[sps_length];
				System.arraycopy(data, sps_start, SPS, 0, sps_length);
				for(int si=0;si<sps_length;si++)
					if(VERBOSE){
						Log.d(TAG, "==========SPS :" + si + SPS[si]);
					}

				//save sps
				FileOutputStream file_out = VideoRecord.this.openFileOutput(
						String.format("%d%d.sps",videoWidth,videoHeight), 
						Context.MODE_PRIVATE);
				file_out.write(SPS);
				file_out.close();

				//pps length and pps data
				int pps_start = sps_start + sps_length + 1;
				byte[] pps_3gp =new byte[2];
				pps_3gp[1] = data[pps_start];
				pps_3gp[0] =data[pps_start+1];
				int pps_length = bytes2short(pps_3gp);
				if(VERBOSE){
					Log.d(TAG, "PPS LENGTH:"+pps_length);
				}


				pps_start+=2;

				PPS = new byte[pps_length];
				System.arraycopy(data, pps_start, PPS,0,pps_length);
				for (int pi =0;pi<pps_length;pi++)
					if(VERBOSE){
						Log.d(TAG, "==========PPS :" +pi + PPS[pi]);
					}


				//Save PPS
				file_out = VideoRecord.this.openFileOutput(
						String.format("%d%d.pps",videoWidth,videoHeight),
						Context.MODE_PRIVATE);
				file_out.write(PPS);
				file_out.close();
				if(VERBOSE){
					Log.d(TAG, "==========SPS :" + SPS+ ",  PPS :" +PPS);
				}

				break;
			}
		}

	}

	//计算长度
	public short bytes2short(byte[] b)
	{
		short mask=0xff;
		short temp=0;
		short res=0;
		for(int i=0;i<2;i++)
		{
			res<<=8;
			temp=(short)(b[1-i]&mask);
			res|=temp;
		}
		return res;
	}

	private void ReadPreference() {
		SharedPreferences lastSp = this.getSharedPreferences("lastSp",
				MODE_PRIVATE);

		this.gateway = lastSp.getString("play_url", "192.168.12.62");
		this.port = lastSp.getString("play_port", "3064");
		this.ID = Integer.parseInt(lastSp.getString("id", "123456"));
		this.bitRate = (1000 * Integer.parseInt(lastSp.getString(CODERATE,
				"200")));
		Integer.parseInt(lastSp.getString(FRAMERATE, "30"));
		this.screen = lastSp.getInt(SCREEN, 4);

	}

	public static class CameraSize{
		private int width;
		private int height;

		public CameraSize(int width, int height){
			this.width = width;
			this.height = height;
		}

		public int getWidth(){
			return width;
		}

		public int getHeight(){
			return height;
		}
	}

	public CameraSize GetCameraSize(int screen)
	{
		return sizeMap.get(screen);
	}

	private void ShowDialogNoNetConnect() {
		AlertDialog.Builder builder = new Builder(VideoRecord.this);
		builder.setMessage("没有网络连接");
		builder.setTitle("提示");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				VideoRecord.this.finish();
			}
		});
		builder.setNegativeButton("设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				Intent intent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		});
		builder.create().show();
	}

	private void ShowDialogNoWifi() {
		AlertDialog.Builder builder = new Builder(VideoRecord.this);
		builder.setMessage("没有WIFI网络");
		builder.setTitle("提示");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				VideoRecord.this.finish();
			}
		});

		builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void CheckNetwork() {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean mobileNetworkConnected = false;
			boolean wifiConnected = false;
			NetworkInfo wifiNetwork = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetwork != null && wifiNetwork.isAvailable()
					&& wifiNetwork.isConnectedOrConnecting()) {
				wifiConnected = true;
			}

			NetworkInfo mobileNetwork = connectivityManager
					.getActiveNetworkInfo();
			// NetworkInfo mobileNetwork = connectivityManager
			// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetwork != null && mobileNetwork.isAvailable()
					&& mobileNetwork.isConnectedOrConnecting()) {
				mobileNetworkConnected = true;
			}

			if (!mobileNetworkConnected && !wifiConnected) {// 没有网络
				ShowDialogNoNetConnect();
			} else {
				if (!wifiConnected) {// 没有WIFI
					ShowDialogNoWifi();
				}
			}

		} catch (Exception e) {
		}
	}

	private void ConfirmQuit() {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				VideoRecord.this.finish();
			}
		};
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("确定要退出程序吗？").setPositiveButton("是", listener)
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		alertDialog.show();
	}

	@Override
	public void log(Object paramObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseCommand(byte[] paramArrayOfByte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAudio(byte[] paramArrayOfByte, int paramInt) {
		// TODO Auto-generated method stub

	}

	class VideoData extends Thread {
		public VideoData() {

		}

		@SuppressLint({ "NewApi", "SdCardPath" })
		public void KillThread() throws IOException {
			this.interrupt();
		}

		public void run() {
			if(DEBUG_SAVE_FILE){
				try {
					File file = new File("/sdcard/encoder.h264");
					if (file.exists())
						file.delete();
					file_test = new RandomAccessFile(file, "rw");
				} catch (Exception ex) {
					if(VERBOSE){
						Log.v("System.out", ex.toString());
					}

				}
			}


			//			while(!Thread.currentThread().isInterrupted()){
			try {
				if(SPS == null) {
					Log.e(TAG, "Rlease MediaRecorder and get SPS and PPS");
					Thread.sleep(1000);
					//释放MediaRecorder资源
					ReleaseMediaRecorder();
					//从已采集的视频数据中获取SPS和PPS
					FindSPSAndPPS();
					//找到后重新初始化MediaRecorder
					InitializeVideo();
					StartRecording();
				}			

				DataInputStream dataInput = new DataInputStream(receiver.getInputStream());
				//先读取ftpy box and mdat box, 目的是skip ftpy and mdat data,(decisbe by phone)
				if(VERBOSE){
					Log.d(TAG,"=============StartMdatPlace :" + StartMdatPlace);
				}

				dataInput.read(h264frame, 0, StartMdatPlace);
//				dataInput.skipBytes(8);

				//			file_test.write(head);
				//			file_test.write(SPS);//write sps
				//
				//			file_test.write(head);
				//			file_test.write(PPS);//write pps

				int h264length =0;

				while(mMediaRecorderRecording) {
					h264length = dataInput.readInt();
					if(h264length > MAXFRAMEBUFFER)
					{
						if(VERBOSE){
							Log.e(TAG, "h264 length is too big !  h264length = " + h264length );
						}

						break;
					}
					if(h264length == 0){
						if(VERBOSE){
							Log.e(TAG, "h264 length is too small !  h264length = " + h264length );
						}

						continue;
					}
					if(VERBOSE){
						Log.e(TAG, "h264 length :" + h264length);
					}

					ReadSize(h264length, dataInput);

					byte[] h264 = new byte[h264length];
					byte[] out = new byte[h264length + head.length * 3 + SPS.length + PPS.length];
					int startPos = 0 ;

					System.arraycopy(h264frame, 0, h264, 0, h264length);

					int type = h264[0] & 0x1f;
					if(type == 5)
					{
						System.arraycopy(head, 0, out, 0, head.length);
						System.arraycopy(SPS, 0, out, head.length, SPS.length);
						System.arraycopy(head, 0, out, head.length + SPS.length, head.length);
						System.arraycopy(PPS, 0, out, head.length + SPS.length + head.length, PPS.length);

						startPos = head.length + SPS.length + head.length + PPS.length;
						if(VERBOSE){
							Log.e(TAG, "get IDR selice!");
						}
						if(DEBUG_SAVE_FILE){
							file_test.write(head);
							file_test.write(SPS);//write sps
							//
							file_test.write(head);
							file_test.write(PPS);//write pps
						}

					}
					System.arraycopy(head, 0, out, startPos, head.length);
					System.arraycopy(h264, 0, out, startPos + head.length, h264length);
					int ret = rb_codec.GetInstance()
							.EncodeYUVToRTP(out, startPos + head.length + h264length);
					//				file_test.write(out);
					if(DEBUG_SAVE_FILE){
						file_test.write(head);
						file_test.write(h264);//write selice
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			//			}
			if(DEBUG_SAVE_FILE){
				try {
					file_test.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if(VERBOSE){
				Log.e(TAG, "video data thread is end ! ------------------");
			}

		}

	}
}

