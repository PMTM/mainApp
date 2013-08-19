
package cz.xlinux.mainApp;

import java.util.Random;

import aidl.core.API.AbstractBestInterface;
import aidl.core.API.AbstractFunInterface;
import aidl.core.API.AbstractJoyInterface;
import aidl.core.API.OnCouponChange;
import aidl.core.API.OnNewHistoryItem;
import aidl.core.API.OnNewReceipt;
import aidl.core.API.OnTicketChange;
import aidl.core.API.SecurityWatchdog;
import aidl.self.API.Interconnect;
import aidl.sp.API.Ticket;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import cz.xlinux.db.MyContentProvider;
import cz.xlinux.db.TableItems;
import cz.xlinux.ifImpl.DefaultImplBestIf;
import cz.xlinux.ifImpl.MyFirstBestIfImpl;
import cz.xlinux.libAPI.libFce.APIConnectionLocal;
import cz.xlinux.libAPI.libFce.CBOnSvcChangeLocal;

public class MainActivity extends ListActivity implements OnClickListener, CBOnSvcChangeLocal,
        LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "MainActivity";

    private static final int DELETE_ID = Menu.FIRST + 1;

    // private MainActivity mThis;
    private static TextView mTvLog;

    private Interconnect apiService;

    private APIConnectionLocal conn;

    private boolean isBound;

    private TextView mTitle;

    // private CommentsDataSource datasource;
    // private ArrayAdapter<Comment> mDBAdapter;
    private static Context ctx;

    private LoaderManager loadermanager;

    private CursorLoader cursorLoader;

    private SimpleCursorAdapter mDBAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.getListView().setDividerHeight(2);

        initDBLink();

        // mThis = this;
        // Prepare items on screen
        mTvLog = (TextView) findViewById(R.id.tvLog);
        // mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTitle = (TextView) findViewById(R.id.tvTitle);
        mTitle.setText("pid=" + android.os.Process.myPid() + " tid=" + android.os.Process.myTid());

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
        registerForContextMenu(getListView());

        bindLocalService();
    }

    private void initDBLink() {
        loadermanager = getLoaderManager();
        String[] uiBindFrom = {
            TableItems.COLUMN_NAME
        };
        int[] uiBindTo = {
            android.R.id.text1
        };
        mDBAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                uiBindFrom, uiBindTo, 0);
        setListAdapter(mDBAdapter);

        loadermanager.initLoader(1, null, this);
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
        // datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // datasource.close();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        mTvLog.setText(".clicked.");
        switch (v.getId()) {
            case R.id.btAddItem:
                String randomValue = generateRandomString();
                insertValueIntoDB(randomValue);
                break;
            case R.id.btDelItem:
                if (getListAdapter().getCount() > 0) {
                    Long id = getListAdapter().getItemId(0);
                    Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
                    getContentResolver().delete(uri, null, null);
                }
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + info.id);
                getContentResolver().delete(uri, null, null);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private String generateRandomString() {
        String[] comments = new String[] {
            "Cool", "Very nice", "Hate it"
        };
        int nextInt = new Random().nextInt(3);
        String randomValue = comments[nextInt];
        return randomValue;
    }

    private void insertValueIntoDB(String randomValue) {
        ContentValues values = new ContentValues();
        values.put(TableItems.COLUMN_NAME, randomValue);

        @SuppressWarnings("unused")
        Uri todoUri = getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
    }

    public void addText(String txt) {
        mTvLog.append(txt);
        sendNotificationInstance(LOG_TAG, txt);
        Log.d(LOG_TAG, "logging:" + txt);
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

    public static void sendNotification(Intent notificationIntent, String ticker, String txt) {
        if (ctx != null) {
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, PI_REQ_CODE,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationManager nm = (NotificationManager) ctx
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = ctx.getResources();
            Notification.Builder builder = new Notification.Builder(ctx);

            // res.getString(R.string.myTicker)
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.ic_drum_large);
            builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_drum)
                    .setLargeIcon(bmp).setTicker(ticker).setWhen(System.currentTimeMillis())
                    .setAutoCancel(true).setContentTitle(res.getString(R.string.myNotifTitle))
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

        @Override
        public AbstractBestInterface getBestAPI(String version) throws RemoteException {
            if (version.startsWith("v1.x")) {
                return new MyFirstBestIfImpl(version);
            } else {
                return new DefaultImplBestIf();
            }
        }

        @Override
        public AbstractFunInterface getFunAPI(String arg0) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public AbstractJoyInterface getJoyAPI(String arg0) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

    };

    OnTicketChange.Stub mOnTicketChangeImpl = new OnTicketChange.Stub() {

        @Override
        public void addTicket(final String dummy, final Ticket ticket) throws RemoteException {
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

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = {
            TableItems.COLUMN_ID, TableItems.COLUMN_NAME
        };
        cursorLoader = new CursorLoader(this, MyContentProvider.CONTENT_URI, projection, null,
                null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mDBAdapter != null && cursor != null)
            mDBAdapter.swapCursor(cursor); // swap the new cursor in.
        else
            Log.v(LOG_TAG, "OnLoadFinished: mAdapter is null");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        if (mDBAdapter != null)
            mDBAdapter.swapCursor(null);
        else
            Log.v(LOG_TAG, "OnLoadFinished: mAdapter is null");
    }
}
