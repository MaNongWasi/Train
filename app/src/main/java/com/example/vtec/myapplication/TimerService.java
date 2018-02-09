package com.example.vtec.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ActionProvider;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by VTEC on 3/21/2016.
 */
public class TimerService extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    long start_time;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        ctx = this;
        startService();
        start_time = System.currentTimeMillis();
    }

    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }

    private class mainTask extends TimerTask {
        public void run() {
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long time_interval = System.currentTimeMillis() - start_time;
//            Toast.makeText(getApplicationContext(), convertToString(time_interval), Toast.LENGTH_SHORT).show();
            if (MainActivity.timer_tv != null){
                MainActivity.timer_tv.setText(convertToString(time_interval));
            }
        }
    };

    public String convertToString(long time) {
        if ((time) >= 360000000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600000) / (60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());

        long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());

        timeCount = hour + ":" + minue + ":" + sec;

        return timeCount;
    }
}
