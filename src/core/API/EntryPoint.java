package core.API;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EntryPoint extends Service {

	protected static final String TAG = "EntryPoint";

	@Override
	public IBinder onBind(Intent intent) {
		final String version = intent.getExtras().getString("version");

		Log.d(TAG, "onBind: version requested: " + version);

		return new EntryPointImpl();
	}

}