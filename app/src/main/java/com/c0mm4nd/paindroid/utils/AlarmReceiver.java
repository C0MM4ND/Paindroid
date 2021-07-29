package com.c0mm4nd.paindroid.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DELETEME", "received alarm");
        Toast.makeText(context, "Time to add pain record today....", Toast.LENGTH_LONG).show();
    }
}