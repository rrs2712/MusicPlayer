package com.g53mdp.cw02.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MP3Service extends Service {

    final private String ACT ="Act03 MP3Service";

    public MP3Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ACT, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ACT,"onCreate()");

        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,mainActivity,0);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Message")
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle("Title")
                .setContentText("Content text")
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    @Override
    public void onDestroy() {
        Log.d(ACT,"onDestroy");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ACT,"onStartCommand");
        return Service.START_REDELIVER_INTENT;
    }
}
