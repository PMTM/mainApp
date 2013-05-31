package cz.xlinux.mainApp;

import self.API.InterconnectImpl;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import core.API.EntryPoint;
import core.API.EntryPointImpl;

public class MyEntryPoint extends EntryPoint {
	protected static final String TAG = "MyEntryPoint";

	@Override
	public IBinder onBind(Intent intent) {
		final String version = intent.getExtras().getString("version");

		String action = intent.getAction();
		Log.d(LOG_TAG, "onBind: ver = " + version + ", act = " + action);

		if (action.equals("core.API.BindLocal")) {
			return new InterconnectImpl(this);
		} else {
			// IntentFilter intentFilter = new IntentFilter("core.API.SECOND");
			// ChangeHandler mHandler = new ChangeHandler();
			// ChangeReceiver mReceiver = new ChangeReceiver(mHandler);
			// registerReceiver(mReceiver, intentFilter);
			return new EntryPointImpl(this);
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// All clients have unbound with unbindService()
		Toast.makeText(this, "Service Unbinding", Toast.LENGTH_SHORT).show();
		Log.d(LOG_TAG, "onUnbind()");
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		// A client is binding to the service with bindService(),
		// after onUnbind() has already been called
		Log.d(LOG_TAG, "onRebind(intent=" + intent + ")");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Done", Toast.LENGTH_SHORT).show();
		Log.d(LOG_TAG, "onDestroy()");
	}
}
