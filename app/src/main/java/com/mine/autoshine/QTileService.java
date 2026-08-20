package com.mine.autoshine;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class QTileService extends TileService {
	ShineService sService;
	boolean isBound = false;
	int storedState = -1;

	@Override
	public void onStartListening() {
		super.onStartListening();
		Intent intent = new Intent(this, ShineService.class);

		if (!isBound)
			bindService(intent, connection, 0);

		if (storedState == -1)
			updateTile(Tile.STATE_UNAVAILABLE);

	}

	@Override
	public void onStopListening() {
		super.onStopListening();

		if (isBound)
			unbindService(connection);

		isBound = false;
	}

	@Override
	public void onClick() {
		super.onClick();

		if (isBound) {
			storedState = sService.getState();
			if (storedState == Constants.SERVICE_STATE_PAUSED) {
				updateTile(Tile.STATE_ACTIVE);
				storedState = Constants.SERVICE_STATE_ACTIVE;
			} else {
				updateTile(Tile.STATE_INACTIVE);
				if (storedState == Constants.SERVICE_STATE_ACTIVE)
					storedState = Constants.SERVICE_STATE_PAUSED;
			}
		} else {
			updateTile(Tile.STATE_UNAVAILABLE);
		}

		Intent tapIntent = new Intent();
		tapIntent.putExtra(Constants.SERVICE_INTENT_EXTRA_TAP, 0);
		tapIntent.setAction(Constants.SERVICE_INTENT_ACTION);
		sendBroadcast(tapIntent);
	}

	private final ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			ShineService.LocalBinder binder = (ShineService.LocalBinder) service;
			sService = binder.getService();
			isBound = true;

			storedState = sService.getState();

			if (storedState == Constants.SERVICE_STATE_ACTIVE)
				updateTile(Tile.STATE_ACTIVE);
			else
				updateTile(Tile.STATE_INACTIVE);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			sService = null;
			isBound = false;
			storedState = -1;
			updateTile(Tile.STATE_UNAVAILABLE);
		}
	};

	private void updateTile(int state) {
		Tile tile = getQsTile();
		tile.setState(state);
		tile.updateTile();
	}
}