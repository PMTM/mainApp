package cz.xlinux.mainApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	// private MainActivity mThis;
	private TextView mTvLog;
	private BroadcastReceiver mReceiver;
	private ChangeHandler mHandler;

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
		MyApplication.shareAct=this;
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
}
