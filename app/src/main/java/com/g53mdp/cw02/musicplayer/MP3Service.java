package com.g53mdp.cw02.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;

public class MP3Service extends Service {

    private final String
            NOTIFICATION_TITLE = "Music Player",
            ACT ="Act02 Service";
    private final IBinder binder = new MP3ServiceBinder();
    private final MP3Player mp3Player = new MP3Player();
    private String
            msgLabel="Now playing: ",
            lastSelectedSong="";

    public class MP3ServiceBinder extends Binder{
        MP3Service getService(){
            // Return this instance to clients
            return MP3Service.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ACT, "onBind");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ACT,"onCreate()");
    }

    @Override
    public void onDestroy() {
        Log.d(ACT,"onDestroy");
        super.onDestroy();

        stopMP3();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ACT,"onStartCommand");

        return Service.START_STICKY;
    }

    public void createNotification(){

        if (mp3Player.getState()== MP3Player.MP3PlayerState.STOPPED){
            Log.d(ACT,"No reason to create a notification.");
            return;
        }

        Log.d(ACT,"createNotification");
        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,mainActivity,0);

        File f = new File(mp3Player.getFilePath());

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(msgLabel + mp3Player.getFilePath())
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(msgLabel + f.getName())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    public boolean playNewMP3(String songPath){
        Log.d(ACT,"playNewMP3");

        lastSelectedSong = songPath;
        boolean reply = false;

        switch (mp3Player.getState()){
            case PLAYING:
            case PAUSED:
                mp3Player.stop();
            case STOPPED:
                mp3Player.load(lastSelectedSong);
                reply = true;
                break;
            default:
                break;
        }

        return  reply;
    }

    public void stopMP3(){
        Log.d(ACT,"stopMP3");

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

    public void playMP3(){
        Log.d(ACT,"playMP3");

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
                }
                break;
            default:
                break;
        }
    }

    public MP3Player.MP3PlayerState getState(){
        return mp3Player.getState();
    }

    public String getLastSong(){
        return lastSelectedSong;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(ACT,"onUnbind");
        return super.onUnbind(intent);
    }
}
