package cz.xlinux.mainApp;

import cz.xlinux.mainApp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	// private MainActivity mThis;
	private TextView mTvLog;

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
	}

	@Override
	public void onClick(View v) {
		mTvLog.setText("...");
		switch (v.getId()) {
		}
	}
}
