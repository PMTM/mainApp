package cz.xlinux.mainApp.core.API;

import android.os.RemoteException;
import android.util.Log;
import cz.xlinux.libAPI.aidl.SecurityWatchdog;

public class SecurityWatchdogImpl extends SecurityWatchdog.Stub {

	private static final String LOG_TAG = "SecurityWatchdogImpl";

	@Override
	public void expireTimerNow() throws RemoteException {
		Log.d(LOG_TAG, "expireTimerNow()");
	}

	@Override
	public void renewTimer() throws RemoteException {
		Log.d(LOG_TAG, "renewTimer()");
	}

}
