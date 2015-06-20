package com.thepriest.andrea.ramtool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean bScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            RAMToolApp.bScreenIsOn = false;
            bScreenOn = false;
            if (RAMToolApp.bLog) RAMToolApp.mLogHelper.appendLog(context.getString(R.string.screen_off));
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            RAMToolApp.bScreenIsOn = true;
            bScreenOn = true;
            if (RAMToolApp.bLog) RAMToolApp.mLogHelper.appendLog(context.getString(R.string.screen_on));
        }
    }
}