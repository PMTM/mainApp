package cz.xlinux.mainApp;

import aidl.sp.API.Coupon;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ChangeHandler extends Handler {

	private static final String LOG_TAG = "ChangeHandler";
	private MainActivity act;

	public ChangeHandler() {
		super();
	}

	public ChangeHandler(MainActivity act) {
		super();
		this.act = act;
	}

	@Override
	public void handleMessage(Message msg) {
		aidl.sp.API.Coupon cu = (Coupon) msg.obj;
		String info = "";
		if (cu!=null) {
			info="tag="+cu.getTag()+", text="+cu.getText();
		} else {
			info=msg.toString();
		}
		Log.d(LOG_TAG, "handleMessage: "+info);
		if (act != null) {
			act.addText("here we are not\n"+info+"\n");
		} else {
			Log.d(LOG_TAG, "does not have handle to display");
			if (MyApplication.shareAct!=null) {
				act=MyApplication.shareAct;
				act.addText("here we are\n"+info+"\n");
			}
		}
	}

}
