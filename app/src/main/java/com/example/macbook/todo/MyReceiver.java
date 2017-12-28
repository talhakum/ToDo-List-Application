package com.example.macbook.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by macbook on 19/12/2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("App", "called receiver method");
        try {
            Utils.generateNotification(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}