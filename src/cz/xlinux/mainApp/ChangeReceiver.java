package cz.xlinux.mainApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ChangeReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = "ChangeReceiver";
	private Handler handler;

	public ChangeReceiver() {
		super();
	}

	public ChangeReceiver(Handler handler) {
		super();
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive: " + intent);
		if (handler != null) {
			Log.d(LOG_TAG,"onReceive HAVE handler");
			Message msg = new Message();
			handler.handleMessage(msg);
		} else {
			Log.d(LOG_TAG,"onReceive without handler");
		}
	}

}
