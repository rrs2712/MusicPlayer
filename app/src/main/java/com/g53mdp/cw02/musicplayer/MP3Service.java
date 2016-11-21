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
    private String
            msgLabel="Now playing: ",
            lastSelectedSong="";
    private final IBinder binder = new MP3ServiceBinder();
    private final MP3Player mp3Player = new MP3Player();


    // ## Service lifecycle management ## //

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

    // ## Client methods ## //

    /**
     * Creates a notification with current song info and activity to launch.
     * Won't disappear on message swiped from the bar, will disappear when
     * touched instead
     */
    public void createNotification(){

        if (mp3Player.getState()== MP3Player.MP3PlayerState.STOPPED){
            Log.d(ACT,"No reason to create a notification.");
            return;
        }

        Log.d(ACT,"createNotification");
        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,mainActivity,0);

        File f = new File(mp3Player.getFilePath());
        String
                msg = msgLabel + f.getName(),
                content = f.getName(),
                title = NOTIFICATION_TITLE + " (" + mp3Player.getState().name() + ")";

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(msg)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    /**
     * Wraps mp3 play functionality
     * @param songPath
     * @return boolean
     */
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

    /**
     * Wraps mp3 stop functionality
     */
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

    /**
     * Wraps mp3 play functionality and decides what to do when
     * pause or play needed
     */
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

    /**
     *Wraps mp3 state
     * @return current state
     */
    public MP3Player.MP3PlayerState getState(){
        return mp3Player.getState();
    }

    /**
     *
     * @return last played song (path)
     */
    public String getLastSong(){
        return lastSelectedSong;
    }

    /**
     * Wraps mp3 progress functionality
     * @return int progress
     */
    public int getMP3Progress(){
        return mp3Player.getProgress();
    }

    /**
     * Wraps mp3 duration functionality
     * @return int song duration
     */
    public int getMP3Duration(){
        return mp3Player.getDuration();
    }

    /**
     * Class to return this instance to clients
     */
    public class MP3ServiceBinder extends Binder{
        MP3Service getService(){
            return MP3Service.this;
        }
    }
}
