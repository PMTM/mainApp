package cz.xlinux.mainApp.core.API;

import android.os.RemoteException;
import cz.xlinux.libAPI.aidl.EntryPoint;
import cz.xlinux.libAPI.aidl.SecurityWatchdog;

public class EntryPointImpl extends EntryPoint.Stub {

	@Override
	public SecurityWatchdog getSecurityWatchdog() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
