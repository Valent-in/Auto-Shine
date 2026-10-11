package com.mine.autoshine;

import static com.mine.autoshine.ShineControl.convertFromPWM;

import android.content.Context;
import android.content.SharedPreferences;

public class MySettings {
	private final Context context;
	private SharedPreferences sharedPref;
	public int l1, l2, l3, l4, b1, b2, b3, b4;
	public int threshold;
	public int mode;
	public boolean rotate;

	MySettings(Context context) {
		this.context = context;
		load();
	}

	public void load() {
		sharedPref = context.getSharedPreferences("mine.authoshine", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
		l1 = sharedPref.getInt("l1", 20);
		l2 = sharedPref.getInt("l2", 200);
		l3 = sharedPref.getInt("l3", 2000);
		l4 = sharedPref.getInt("l4", 20000);
		b1 = sharedPref.getInt("b1", convertFromPWM(31));
		b2 = sharedPref.getInt("b2", convertFromPWM(63));
		b3 = sharedPref.getInt("b3", convertFromPWM(127));
		b4 = sharedPref.getInt("b4", convertFromPWM(255));
		threshold = sharedPref.getInt("threshold", 0);
		mode = sharedPref.getInt("mode", Constants.WORK_MODE_ALWAYS);
		rotate = sharedPref.getBoolean("rotate", true);
	}

	public void save() {
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("l1", l1);
		editor.putInt("l2", l2);
		editor.putInt("l3", l3);
		editor.putInt("l4", l4);
		editor.putInt("b1", b1);
		editor.putInt("b2", b2);
		editor.putInt("b3", b3);
		editor.putInt("b4", b4);
		editor.putInt("threshold", threshold);
		editor.putInt("mode", mode);
		editor.putBoolean("rotate", rotate);
		editor.commit();
	}
}
