package com.bsht;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.audio.Speex;
import com.bsht.jxvideo.G711;
import com.bsht.jxvideo.Jxaudio;
import com.bsht.jxvideo.Jxcodec;
import com.bsht.net.MyCallback;
import com.bsht.net.TCPClient;
import com.bsht.net.UDPServer;
import com.rb_codec.rb_codec;

@SuppressLint("NewApi")
public class JxVideo {

	private boolean videoOn = true;
	private boolean audioOn = true;

	public static final int UNINIT = 111;



//	@SuppressLint("SdCardPath")
//	public void onCreate(Bundle paramBundle) {
//		super.onCreate(paramBundle);
//		setContentView(R.layout.main);
//
//		CheckNetwork();
//
//		Button settingBtn = (Button) findViewById(R.id.setting);
//		settingBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(JxVideo.this, SettingActivity.class);
//				startActivity(intent);
//			}
//		});
//	}

/*	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ConfirmQuit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		paramMenu.add(0, 4, 0, getString(R.string.preferences));
		return super.onCreateOptionsMenu(paramMenu);
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		boolean bool = true;
		switch (paramMenuItem.getItemId()) {
		case 1:
			if (videoOn) {
				paramMenuItem.setTitle(R.string.videoOn);
				videoOn = false;
			} else {
				paramMenuItem.setTitle(R.string.videoOff);
				videoOn = true;
			}
			break;
		case 2:
			if (audioOn) {
				paramMenuItem.setTitle(R.string.audioOn);
				audioOn = false;
			} else {
				paramMenuItem.setTitle(R.string.audioOff);
				audioOn = true;
			}
			break;
		case 3:
			new AlertDialog.Builder(this)
					.setTitle("UUID")
					.setMessage(getUUID())
					.setNegativeButton(getString(2131034120),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									paramDialogInterface.dismiss();
								}
							}).create().show();
			break;
		case 4:
			startActivity(new Intent(this, SettingActivity.class));
			break;

		}

		return bool;
	}

	@SuppressLint("NewApi")
	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
		this.videoOn = false;
		this.audioOn = false;


		Message msg = new Message();
		msg.arg1 = UNINIT;
		mainHandler.sendMessageDelayed(msg, 1000);
		// rb_codec.GetInstance().UnInitEncode();

	}

	public Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == UNINIT) {
				rb_codec.GetInstance().UnInitEncode();
			}
		}
	};


	private String getUUID() {
		return ((TelephonyManager) getSystemService("phone")).getDeviceId();
	}



	private void ShowDialogNoNetConnect() {
		AlertDialog.Builder builder = new Builder(JxVideo.this);
		builder.setMessage("没有网络连接");
		builder.setTitle("提示");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				JxVideo.this.finish();
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
		AlertDialog.Builder builder = new Builder(JxVideo.this);
		builder.setMessage("没有WIFI网络");
		builder.setTitle("提示");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				JxVideo.this.finish();
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
				JxVideo.this.finish();
			}
		};
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("确定要退出程序吗？").setPositiveButton("是", listener)
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		alertDialog.show();
	}*/
}
