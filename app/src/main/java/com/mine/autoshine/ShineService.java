package com.mine.autoshine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;
import android.content.res.Configuration;

public class ShineService extends Service {
	private ShineControl shiner;

	private final BroadcastReceiver communicator = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int extra = intent.getIntExtra(Constants.SERVICE_INTENT_EXTRA, -1);

			if (extra == Constants.SERVICE_INTENT_PAYLOAD_PING) {
				Toast.makeText(context, getResources().getString(R.string.service_running) + "\n" +
						getResources().getString(R.string.sensor_c) + " " + shiner.getLastSensorValue() + ",  " +
						getResources().getString(R.string.brightness_c) + " " + shiner.getSetBrightness(),
						Toast.LENGTH_SHORT).show();
			}

			if (extra == Constants.SERVICE_INTENT_PAYLOAD_SET) {
				shiner.reconfigure();
				Toast.makeText(context, getResources().getString(R.string.config_updated), Toast.LENGTH_SHORT).show();
			}

			if (intent.getIntExtra(Constants.SERVICE_INTENT_EXTRA_TAP, -1) == 0) {
				shiner.startListening();
			}
		}
	};

	private final BroadcastReceiver deviceState = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				shiner.stopListening();
			} else {
				shiner.setLandscape(
						context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

				if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
					shiner.onScreenUnlock();
				else
					shiner.startListening();
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setUpAsForeground();

		shiner = new ShineControl(this);
		shiner.onScreenUnlock();
		IntentFilter commandFilt = new IntentFilter(Constants.SERVICE_INTENT_ACTION);
		registerReceiver(communicator, commandFilt, RECEIVER_EXPORTED);

		IntentFilter deviceFilt = new IntentFilter(Intent.ACTION_SCREEN_ON);
		deviceFilt.addAction(Intent.ACTION_SCREEN_OFF);
		deviceFilt.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(deviceState, deviceFilt);

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		shiner.stopListening();
		unregisterReceiver(communicator);
		unregisterReceiver(deviceState);
		Toast.makeText(this, getResources().getString(R.string.service_stopped), Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	private void setUpAsForeground() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		String channelId = "shine_channel_id";
		NotificationChannel channel = new NotificationChannel(channelId, "Auto Shine", NotificationManager.IMPORTANCE_HIGH);
		channel.setImportance(NotificationManager.IMPORTANCE_MIN);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		notificationManager.createNotificationChannel(channel);

		Intent tapIntent = new Intent();
		tapIntent.putExtra(Constants.SERVICE_INTENT_EXTRA_TAP, 0);
		tapIntent.setAction(Constants.SERVICE_INTENT_ACTION);
		PendingIntent tapPendingIntent = PendingIntent.getBroadcast(this, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

		Notification.Builder notificationBuilder = new Notification.Builder(this, channelId);
		Notification mNotification = notificationBuilder.setOngoing(true)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setCategory(Notification.CATEGORY_SERVICE)
				.setContentIntent(tapPendingIntent)
				.build();

		startForeground(123, mNotification);
	}
}
