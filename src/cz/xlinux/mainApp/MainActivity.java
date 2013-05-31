package cz.xlinux.mainApp;

import java.util.List;
import java.util.Random;

import aidl.core.API.OnCouponChange;
import aidl.core.API.OnNewHistoryItem;
import aidl.core.API.OnNewReceipt;
import aidl.core.API.OnTicketChange;
import aidl.core.API.SecurityWatchdog;
import aidl.self.API.Interconnect;
import aidl.sp.API.Ticket;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import cz.xlinux.db.Comment;
import cz.xlinux.db.CommentsDataSource;
import cz.xlinux.libAPI.libFce.APIConnectionLocal;
import cz.xlinux.libAPI.libFce.CBOnSvcChangeLocal;

public class MainActivity extends ListActivity implements OnClickListener,
		CBOnSvcChangeLocal {
	private static final String LOG_TAG = "MainActivity";
	// private MainActivity mThis;
	private static TextView mTvLog;
	private Interconnect apiService;
	private APIConnectionLocal conn;
	private boolean isBound;
	private TextView mTitle;
	private CommentsDataSource datasource;
	private static Context ctx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		datasource = new CommentsDataSource(this);
		datasource.open();

		List<Comment> values = datasource.getAllComments();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		// mThis = this;
		// Prepare items on screen
		mTvLog = (TextView) findViewById(R.id.tvLog);
		// mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
		mTitle = (TextView) findViewById(R.id.tvTitle);
		mTitle.setText("pid=" + android.os.Process.myPid() + " tid="
				+ android.os.Process.myTid());

		Button clr;
		clr = (Button) findViewById(R.id.btAddItem);
		clr.setOnClickListener(this);
		clr = (Button) findViewById(R.id.btDelItem);
		clr.setOnClickListener(this);
		clr = (Button) findViewById(R.id.btTestScan);
		clr.setOnClickListener(this);

		// dynamic filter does not work across process boundaries
		// IntentFilter intentFilter = new IntentFilter("core.API.SECOND");
		// mHandler = new ChangeHandler(this);
		// mReceiver = new ChangeReceiver(mHandler);
		// registerReceiver(mReceiver, intentFilter);
		// MyApplication.shareAct = this;

		ctx = this;

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
				apiService.registerCallBack(null);
				unbindService(conn);
			}
		} catch (Throwable t) {
		}
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		mTvLog.setText(".clicked.");
		@SuppressWarnings("unchecked")
		ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
		Comment comment = null;
		switch (v.getId()) {
		case R.id.btAddItem:
			String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
			int nextInt = new Random().nextInt(3);
			// Save the new comment to the database
			comment = datasource.createComment(comments[nextInt]);
			adapter.add(comment);
			break;
		case R.id.btDelItem:
			if (getListAdapter().getCount() > 0) {
				comment = (Comment) getListAdapter().getItem(0);
				datasource.deleteComment(comment);
				adapter.remove(comment);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	public void addText(String txt) {
		mTvLog.append(txt);
		sendNotificationInstance(LOG_TAG, txt);
		Log.d(LOG_TAG, "logging:" + txt);

		datasource.close();
		datasource.open();
		List<Comment> values = datasource.getAllComments();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	public void msgAlert(String topic, String result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(topic);
		builder.setMessage(result);
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	private static final int MY_NOTIF_ID = 0;
	private static final int PI_REQ_CODE = 1;

	public static void sendNotificationStatic(String ticker, String txt) {
		Intent notificationIntent = new Intent(ctx, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		sendNotification(notificationIntent, ticker, txt);
	}

	public void sendNotificationInstance(String ticker, String txt) {
		Intent notificationIntent = new Intent(ctx, this.getClass());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		sendNotification(notificationIntent, ticker, txt);
	}

	public static void sendNotification(Intent notificationIntent,
			String ticker, String txt) {
		if (ctx != null) {
			PendingIntent contentIntent = PendingIntent.getActivity(ctx,
					PI_REQ_CODE, notificationIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationManager nm = (NotificationManager) ctx
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Resources res = ctx.getResources();
			Notification.Builder builder = new Notification.Builder(ctx);

			// res.getString(R.string.myTicker)
			Bitmap bmp = BitmapFactory.decodeResource(res,
					R.drawable.ic_drum_large);
			builder.setContentIntent(contentIntent)
					.setSmallIcon(R.drawable.ic_machine).setLargeIcon(bmp)
					.setTicker(ticker).setWhen(System.currentTimeMillis())
					.setAutoCancel(true)
					.setContentTitle(res.getString(R.string.myNotifTitle))
					.setContentText(txt);
			Notification n = builder.build();

			nm.notify(MY_NOTIF_ID, n);
		} else {
			Log.e(LOG_TAG, "do not have Context");
		}
	}

	@Override
	public void setService(Interconnect apiService) {
		Log.d(LOG_TAG, "setService apiService = " + apiService);
		this.apiService = apiService;
		if (apiService != null) {
			try {
				// this.apiService.registerMessenger(new Messenger(mHandler));
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
					addText("ticket.[+]:\n" + ticket.toString() + "\n");
					// mTvLog.append("ticket.add:\n" + ticket.toString() +
					// "\n");
				}
			});
		}

		@Override
		public void removeTicket(final Ticket ticket) throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					addText("ticket.[-]:\n" + ticket.toString() + "\n");
					// mTvLog.append("ticket.remove:\n" + ticket.toString() +
					// "\n");
				}
			});
		}

	};
}
