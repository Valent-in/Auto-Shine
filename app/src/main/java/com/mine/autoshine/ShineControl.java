package com.mine.autoshine;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Handler;

import android.content.ContentResolver;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class ShineControl implements SensorEventListener {
	private final SensorManager sMgr;
	private final Sensor lightSensor;
	private final MySettings sett;
	private final ContentResolver cResolver;

	private final Context mContext;
	private final TelephonyManager telephonyManager;

	private final Handler delayer = new Handler();

	private final long pause = 1000;

	private boolean onListen = false;
	private boolean landscape = false;

	private float lux = 0;
	private int tempBrightness = 0;

	ShineControl(Context context) {
		sett = new MySettings(context);
		cResolver = context.getContentResolver();
		sMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		lightSensor = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

		mContext = context;
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			lux = event.values[0];
			setBrightness((int) lux);
		}
	}

	private boolean shouldSuspend() {
		if (sett.mode == Constants.WORK_MODE_ALWAYS)
			return false;

		if (sett.mode == Constants.WORK_MODE_LANDSCAPE && landscape)
			return false;

		if (sett.mode == Constants.WORK_MODE_PORTRAIT && !landscape)
			return false;

		return true;
	}

	private void scheduleSuspend() {
		if (shouldSuspend())
			delayer.postDelayed(new Runner(), pause);
	}

	public int getState() {
		if (!shouldSuspend())
			if (onListen)
				return Constants.SERVICE_STATE_ACTIVE;
			else
				return Constants.SERVICE_STATE_PAUSED;

		return Constants.SERVICE_STATE_SUSPENDED;
	}

	private class Runner implements Runnable {
		public void run() {
			boolean ringing = false;
			if (mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				int state = telephonyManager.getCallState();
				ringing = (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK);
			}

			if (ringing) {
				delayer.postDelayed(new Runner(), pause * 2);
			} else {
				stopListening();
			}

		}
	}

	public void startListening(boolean onRotate) {
		if ((sett.mode == Constants.WORK_MODE_UNLOCK) && !sett.rotate && onRotate)
			return;

		if (!onListen) {
			sMgr.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			onListen = true;
		}

		scheduleSuspend();
	}

	public void stopListening() {
		if (onListen)
			sMgr.unregisterListener(this);

		delayer.removeCallbacksAndMessages(null);
		onListen = false;
	}

	private void setBrightness(int lux) {
		float x1, y1, x2, y2;
		int brightness;
		if (lux <= sett.l1)
			brightness = sett.b1;
		else if (lux > sett.l4)
			brightness = sett.b4;
		else {
			x1 = sett.l3;
			x2 = sett.l4;
			y1 = sett.b3;
			y2 = sett.b4;

			if (lux <= sett.l2) {
				x1 = sett.l1;
				x2 = sett.l2;
				y1 = sett.b1;
				y2 = sett.b2;
			} else if (lux <= sett.l3) {
				x1 = sett.l2;
				x2 = sett.l3;
				y1 = sett.b2;
				y2 = sett.b3;
			}

			float div = lux - x1;
			float xlen = x2 - x1;
			float ylen = y2 - y1;
			float coef = div / xlen;
			brightness = (int) (y1 + ylen * coef);
		}

		int currentBrightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
		tempBrightness = brightness;
		if (Math.abs(currentBrightness - brightness) > sett.threshold) {
			Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
		}

	}

	public void reconfigure() {
		stopListening();
		sett.load();
		startListening(false);
	}

	public void setLandscape(boolean land) {
		landscape = land;
	}

	public void onScreenUnlock() {
		Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

		startListening(false);
	}

	public int getLastSensorValue() {
		return (int) lux;
	}

	public int getSetBrightness() {
		return tempBrightness;
	}

	public boolean getOnListen() {
		return onListen;
	}
}
