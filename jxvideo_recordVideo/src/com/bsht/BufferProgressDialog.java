package com.bsht;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;


	public class BufferProgressDialog {
	Dialog _progress;

	public BufferProgressDialog(final Context context) {

		final Dialog dialog = new Dialog(context, R.style.dialog);

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.bufferprogressbardialog, null);

		dialog.setContentView(v);
		dialog.show();

		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub

				if (keyCode == KeyEvent.KEYCODE_BACK) {

					Activity cActivity = (Activity) context;
					dialog.dismiss();
//					if (!(cActivity instanceof MainActivity)) {						
//						cActivity.finish();
//					} else {
//						MainActivity.getCurrentInstance().inflateIndexView();
//						return true;	
//					}
				}
				return false;
			}
		});

		_progress = dialog;
	}
	    
	public  Boolean destroyProgressDialog(Boolean flag){ 
		Boolean cancleFlag = false;
		if(flag){
			_progress.cancel();
			cancleFlag = true;
		}
		return cancleFlag;
	}

	public Dialog get_progress() {
		return _progress;
	}
}
