package com.mine.autoshine;

public class Constants {
	public static final int WORK_MODE_ALWAYS = 1;
	public static final int WORK_MODE_PORTRAIT = 2;
	public static final int WORK_MODE_UNLOCK = 3;
	public static final int WORK_MODE_LANDSCAPE = 4;

	public static final String SERVICE_INTENT_ACTION = "SHyNE command";
	public static final String SERVICE_INTENT_EXTRA = "reconfigure";
	public static final String SERVICE_INTENT_EXTRA_TAP = "notification_tap";

	public static final int SERVICE_INTENT_PAYLOAD_PING = 0;
	public static final int SERVICE_INTENT_PAYLOAD_SET = 1;
}
