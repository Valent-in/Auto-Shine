package com.mine.autoshine;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class AutoStart extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent pushIntent = new Intent(context, ShineService.class);
			context.startService(pushIntent);
		}
	}
}
