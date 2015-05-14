package com.bsht;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	TextView _ip, _password, _id, _codeRate, _frameRate, _fbl;
	SharedPreferences _lastSp;

	String[] _codeRateArray = { "100", "200", "500", "1000", "2000" };
	String[] _frmaeRateArray = { "5", "10", "15", "20", "25" };
	String[] _fblRateArray = { "176*144", "320*240", "480*320", "640*480",
			"1280*720" };

	public final static String IP = "ip";
	public final static String ID = "id";
	public final static String PASSWORD = "password";
	public final static String CODERATE = "codeRate";
	public final static String FRAMERATE = "frameRate";
	public final static String FBL = "fbl";
	public final static String SCREEN = "screen";

	public final static int MSG_REGISTER = 111;
	public final static int MSG_FAILED = 112;

	private static BufferProgressDialog _bufferProgressDialog = null;

	private Button _register;

	public Handler SettingHandler = new Handler() {
		public void handleMessage(Message msg) {
			_bufferProgressDialog.destroyProgressDialog(true);
			if (msg.arg1 == MSG_REGISTER) {
				Toast.makeText(SettingActivity.this, "注册成功", 1).show();
				// _register.setText("已注册");
			} else if (msg.arg1 == MSG_FAILED) {
				Toast.makeText(SettingActivity.this, "注册失败", 1).show();
				// _register.setText("未注册");
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);

		_lastSp = this.getSharedPreferences("lastSp", MODE_PRIVATE);

		_ip = (TextView) findViewById(R.id.ip_text);
		_id = (TextView) findViewById(R.id.id_text);
		_password = (TextView) findViewById(R.id.password_text);
		_codeRate = (TextView) findViewById(R.id.bitrate);
		_frameRate = (TextView) findViewById(R.id.frameRate);
		_fbl = (TextView) findViewById(R.id.fbl);

		_ip.setText(_lastSp.getString(IP, "202.106.149.228:8080"));
//		_id.setText("10000032");
//		_password.setText("33");
		_id.setText(_lastSp.getString(ID, "10000032"));
		_password.setText(_lastSp.getString(PASSWORD, "33"));
		_codeRate.setText(_lastSp.getString(CODERATE, "2000"));
		_frameRate.setText(_lastSp.getString(FRAMERATE, "25"));
		_fbl.setText(_lastSp.getString(FBL, "1280*720"));

		LinearLayout ip = (LinearLayout) findViewById(R.id.ip_layout);
		LinearLayout password = (LinearLayout) findViewById(R.id.password_layout);
		LinearLayout codeRate = (LinearLayout) findViewById(R.id.bitRate_layout);
		LinearLayout frameRate = (LinearLayout) findViewById(R.id.frameRate_layout);
		LinearLayout fbl = (LinearLayout) findViewById(R.id.fbl_layout);
		LinearLayout about = (LinearLayout) findViewById(R.id.about);

		_register = (Button) findViewById(R.id.register);

		Button back = (Button) findViewById(R.id.back_button);
		Button unicom = (Button) findViewById(R.id.unicon_button);

		_register.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				_bufferProgressDialog = new BufferProgressDialog(
						SettingActivity.this);

				Thread thread = new Thread(new Runnable() {

					public void run() {
						boolean flag = SoapRequest.Register(_lastSp.getString(
								IP, "202.106.149.228:8080"), Integer
								.parseInt(_lastSp.getString(ID, "10000032")),_lastSp.getString(
										PASSWORD, "33"), 
								_lastSp);
						if (flag) {
							Message msg = new Message();
							msg.arg1 = MSG_REGISTER;
							SettingHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.arg1 = MSG_FAILED;
							SettingHandler.sendMessage(msg);
						}
					}
				});
				thread.start();
			}
		});

		ip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customDialog(1);
			}
		});
		password.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customDialog(3);
			}
		});
		_id.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customDialog(2);
			}
		});
		codeRate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customListDialog(1);
			}
		});
		frameRate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customListDialog(2);
			}
		});
		fbl.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				customListDialog(3);
			}
		});
		about.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				aboutDialog();
			}
		});

		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
		unicom.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						UnicomActivity.class);
				startActivity(intent);

			}
		});
	}

	public void customDialog(final int num) {
		final Dialog dialog = new Dialog(SettingActivity.this);

		LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
		View v = inflater.inflate(R.layout.custom_dialog, null);

		dialog.setContentView(v);

		final TextView textView = (TextView) dialog
				.findViewById(R.id.editText1);

		if (num == 1) {
			dialog.setTitle("目标地址");
			textView.setText(_ip.getText());
		} else if (num == 2) {
			dialog.setTitle("ID");
			textView.setText(_id.getText());
		} else if (num == 3) {
			dialog.setTitle("密码");
			textView.setText(_password.getText());
		}

		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		Button save = (Button) dialog.findViewById(R.id.save);

		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Editor editor = _lastSp.edit();
				if (num == 1) {
					if (textView.getText().toString().equals("")) {
						Toast.makeText(SettingActivity.this, "目标地址不能为空", 1)
								.show();
						return;
					}
					_ip.setText(textView.getText());
					editor.putString(IP, textView.getText().toString());
				} else if (num == 3) {
					if (textView.getText().toString().equals("")) {
						Toast.makeText(SettingActivity.this, "密码不能为空", 1)
								.show();
						return;
					}
					_password.setText(textView.getText());
					editor.putString(PASSWORD, textView.getText().toString());
				} else {
					if (textView.getText().toString().equals("")) {
						Toast.makeText(SettingActivity.this, "ID不能为空", 1)
								.show();
						return;
					}
					_id.setText(textView.getText());
					editor.putString(ID, (textView.getText().toString()));
				}

				editor.commit();
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	public void customListDialog(final int num) {
		final Dialog dialog = new Dialog(SettingActivity.this);

		LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
		View v = inflater.inflate(R.layout.list_dialog, null);

		dialog.setContentView(v);

		ArrayAdapter<String> adapter = null;
		if (num == 1) {
			dialog.setTitle("码率");
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice,
					_codeRateArray);
		} else if (num == 2) {
			dialog.setTitle("帧率");
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice,
					_frmaeRateArray);
		} else if (num == 3) {
			dialog.setTitle("分辨率");
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice,
					_fblRateArray);
		}

		final ListView list = (ListView) dialog.findViewById(R.id.listView1);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Editor editor = _lastSp.edit();
				if (num == 1) {
					_codeRate.setText(_codeRateArray[position]);
					editor.putString(CODERATE, _codeRateArray[position]);
				} else if (num == 2) {
					_frameRate.setText(_frmaeRateArray[position]);
					editor.putString(FRAMERATE, _frmaeRateArray[position]);
				} else if (num == 3) {
					_fbl.setText(_fblRateArray[position]);
					editor.putString(FBL, _fblRateArray[position]);
					editor.putInt(SCREEN, position);
				}

				editor.commit();
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	public void aboutDialog() {
		final Dialog dialog = new Dialog(SettingActivity.this);

		LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
		View v = inflater.inflate(R.layout.about_dialog, null);

		dialog.setTitle("关于");
		dialog.setContentView(v);

		dialog.show();

	}
}
