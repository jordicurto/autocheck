package com.autochecker.activity;

import com.autochecker.service.AutoCheckerService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public abstract class AutoCheckerAbstractActivity extends Activity implements
		ServiceConnection {
	
	private final String TAG = getClass().getSimpleName();

	private boolean serviceBound = false;

	private Messenger messengerActivity = new Messenger(
			new AutoCheckServiceHandler());
	private Messenger messengerService = null;

	
	private class AutoCheckServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case AutoCheckerService.MSG_GEOFENCE_TRANSITION_DONE:
				Log.d(TAG, "Proximity alert was procesed by receiver/service ");
				onReceiveProximityAlert(msg.arg1);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, AutoCheckerService.class), this,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		if (serviceBound) {

			if (messengerService != null) {
				try {
					Message msg = Message.obtain(null,
							AutoCheckerService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = messengerActivity;
					messengerService.send(msg);
				} catch (RemoteException e) {
					Log.e(TAG, "Can't send unregister message to service", e);
				}
			}

			unbindService(this);
			serviceBound = false;
		}

		super.onStop();
	}

	@Override
	public void onServiceConnected(ComponentName className,
			IBinder serviceBinder) {

		messengerService = new Messenger(serviceBinder);

		try {

			Message msg = Message.obtain(null,
					AutoCheckerService.MSG_REGISTER_CLIENT);
			msg.replyTo = messengerActivity;
			messengerService.send(msg);

			serviceBound = true;

		} catch (RemoteException e) {
			Log.e(TAG, "Can't send register message to service ", e);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
		serviceBound = false;
	}
	
	protected abstract void onReceiveProximityAlert(int locationId);
}
