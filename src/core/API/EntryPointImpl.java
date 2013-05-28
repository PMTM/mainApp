package core.API;

import android.os.RemoteException;
import android.util.Log;
import cz.xlinux.libAPI.aidl.EntryPoint;
import cz.xlinux.libAPI.aidl.SecurityWatchdog;

public class EntryPointImpl extends EntryPoint.Stub {

	private static final String LOG_TAG = "EntryPointImpl";
	private static SecurityWatchdog wd;

	@Override
	public SecurityWatchdog getSecurityWatchdog() throws RemoteException {
		Log.d(LOG_TAG,"getSecurityWatchDog called");
		if (wd == null)
			wd = new SecurityWatchdogImpl();
		return wd;
	}
}
