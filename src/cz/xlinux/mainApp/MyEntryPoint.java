package cz.xlinux.mainApp;

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

		Log.d(TAG, "onBind: version requested: " + version);
		
		IntentFilter intentFilter = new IntentFilter("core.API.SECOND");
		ChangeHandler mHandler = new ChangeHandler();
		ChangeReceiver mReceiver = new ChangeReceiver(mHandler);
		registerReceiver(mReceiver, intentFilter);

		return new EntryPointImpl(this);
	}
}
