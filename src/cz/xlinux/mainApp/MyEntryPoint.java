package cz.xlinux.mainApp;

import self.API.InterconnectImpl;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
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
			IntentFilter intentFilter = new IntentFilter("core.API.SECOND");
			ChangeHandler mHandler = new ChangeHandler();
			ChangeReceiver mReceiver = new ChangeReceiver(mHandler);
			registerReceiver(mReceiver, intentFilter);
			return new EntryPointImpl(this);
		}
	}
}
