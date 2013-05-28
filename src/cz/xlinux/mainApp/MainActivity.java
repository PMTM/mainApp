package cz.xlinux.mainApp;

import aidl.core.API.OnCouponChange;
import aidl.core.API.OnNewHistoryItem;
import aidl.core.API.OnNewReceipt;
import aidl.core.API.OnTicketChange;
import aidl.core.API.SecurityWatchdog;
import aidl.self.API.Interconnect;
import aidl.sp.API.Ticket;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cz.xlinux.libAPI.libFce.APIConnectionLocal;
import cz.xlinux.libAPI.libFce.CBOnSvcChangeLocal;

public class MainActivity extends Activity implements OnClickListener,
		CBOnSvcChangeLocal {
	private static final String LOG_TAG = "MainActivity";
	// private MainActivity mThis;
	private TextView mTvLog;
	private BroadcastReceiver mReceiver;
	private ChangeHandler mHandler;
	private Interconnect apiService;
	private APIConnectionLocal conn;
	private boolean isBound;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// mThis = this;
		// Prepare items on screen
		mTvLog = (TextView) findViewById(R.id.tvLog);
		// mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());

		Button clr;
		clr = (Button) findViewById(R.id.btTestCert);
		clr.setOnClickListener(this);
		clr = (Button) findViewById(R.id.btTestSvc);
		clr.setOnClickListener(this);
		clr = (Button) findViewById(R.id.btTestScan);
		clr.setOnClickListener(this);

		// dynamic filter does not work across process boundaries
		IntentFilter intentFilter = new IntentFilter("core.API.SECOND");
		mHandler = new ChangeHandler(this);
		mReceiver = new ChangeReceiver(mHandler);
		registerReceiver(mReceiver, intentFilter);
		MyApplication.shareAct = this;

		bindLocalService();
	}

	private void bindLocalService() {
		conn = new APIConnectionLocal(this);

		Intent intent = new Intent("core.API.BindLocal");
		intent.putExtra("version", "1.0");
		Log.d(LOG_TAG, "intent = " + intent);

		isBound = bindService(intent, conn, Context.BIND_AUTO_CREATE);
		Log.d(LOG_TAG, "bindService = " + isBound);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			if (isBound) {
				unbindService(conn);
			}
		} catch (Throwable t) {
		}
	}

	@Override
	public void onClick(View v) {
		mTvLog.setText("...");
		switch (v.getId()) {
		}
	}

	public void addText(String txt) {
		mTvLog.append(txt);
	}

	@Override
	public void setService(Interconnect apiService) {
		Log.d(LOG_TAG, "setService apiService = " + apiService);
		this.apiService = apiService;
		if (apiService != null) {
			try {
				this.apiService.registerMessenger(new Messenger(mHandler));
				this.apiService.registerCallBack(mEntryPointImpl);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private aidl.core.API.EntryPoint.Stub mEntryPointImpl = new aidl.core.API.EntryPoint.Stub() {

		@Override
		public OnCouponChange getCouponCB() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OnNewHistoryItem getHistoryCB() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OnNewReceipt getReceiptCB() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SecurityWatchdog getSecurityWatchdog() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OnTicketChange getTicketCB() throws RemoteException {
			return mOnTicketChangeImpl;
		}

	};

	OnTicketChange.Stub mOnTicketChangeImpl = new OnTicketChange.Stub() {

		@Override
		public void addTicket(final Ticket ticket) throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					mTvLog.append("ticket.add:\n" + ticket.toString() + "\n");
				}
			});
		}

		@Override
		public void removeTicket(final Ticket ticket) throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					mTvLog.append("ticket.remove:\n" + ticket.toString() + "\n");
				}
			});
		}

	};
}
