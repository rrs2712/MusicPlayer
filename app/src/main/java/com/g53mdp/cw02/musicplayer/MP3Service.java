package com.g53mdp.cw02.musicplayer;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MP3Service extends Service {

    private final String ACT ="Act02 MP3Service";
    private final IBinder binder = new MP3ServiceBinder();
    private MP3Player mp3Player = new MP3Player();

    private String lastSelectedSong="";

//    public MP3Service() {
//    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ACT, "onBind");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ACT,"onCreate()");

        createNotification();
    }

    @Override
    public void onDestroy() {
        Log.d(ACT,"onDestroy");

        destroyNotification();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ACT,"onStartCommand");

        return Service.START_REDELIVER_INTENT;
    }

    private void createNotification(){
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

    private void destroyNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }


    public class MP3ServiceBinder extends Binder{
        void onPlayNewSongMP3(String x){
            lastSelectedSong = x;
            MP3Service.this.onPlayNewSongMP3();}
        void onStopMP3(){MP3Service.this.onStopMP3();}
        void onPlayMP3(){MP3Service.this.onPlayMP3();}
    }

    private void onPlayNewSongMP3(){
        Log.d(ACT,"onPlayNewSongMP3");

       // lastSelectedSong = songPath;

        switch (mp3Player.getState()){
            case PLAYING:
            case PAUSED:
                mp3Player.stop();
            case STOPPED:
                mp3Player.load(lastSelectedSong);
                break;
            default:
                break;
        }
    }

    private void onStopMP3(){
        Log.d(ACT,"onStopMP3");

        switch (mp3Player.getState()){
            case PLAYING:
            case PAUSED:
                mp3Player.stop();
                break;
            case STOPPED:
                break;
            default:
                break;
        }
    }

    private void onPlayMP3(){
        Log.d(ACT,"onPlayMP3");

        switch (mp3Player.getState()){
            case PLAYING:
                mp3Player.pause();
                break;
            case PAUSED:
                mp3Player.play();
                break;
            case STOPPED:
                if (!lastSelectedSong.equals("")) {
                    mp3Player.load(lastSelectedSong);
                } else {
                }
                break;
            default:
                break;
        }
    }
}
