package com.ezzat.spofi.Control;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.View.HomeActivity;
import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;
    boolean send = true;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        FirebaseMethods.onReportsChange(new FirebaseCallback() {
            @Override
            public void onValueReturned(Object value) {
                Report r = (Report) value;
                if (repotInTime(r) && reportInRange(r)) {
                    Utils.sendNotification(getApplicationContext());
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone re = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        re.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null, null);

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();


    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        Utils.sendNotification(getBaseContext());
                    }
                });
            }
        };
    }

    private boolean reportInRange(Report report) {
        float[] distance = new float[2];
        com.ezzat.spofi.Model.Location location = Utils.getLocation(getApplicationContext(), report.getLocation());
        android.location.Location.distanceBetween(Double.parseDouble(report.getLocation().getLat()),
                Double.parseDouble(report.getLocation().getLang()),
                Double.parseDouble(location.getLat()),
                Double.parseDouble(location.getLang()),
                distance);

        if ( distance[0] <= 1609.34)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean repotInTime(Report report) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        Date convertedDate = new Date();
        try {
            convertedDate = sdfDate.parse(report.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("dodTime", now.getDay() - convertedDate.getDay() + "");
        boolean val = now.getYear() == convertedDate.getYear() &&
                now.getMonth() == convertedDate.getMonth() &&
                Math.abs(now.getDay() - convertedDate.getDay()) <= 1;
        return val;
    }
}
