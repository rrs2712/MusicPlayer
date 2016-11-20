package com.g53mdp.cw02.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String playlistLabel="Now playing: ";
    private final String
            ACT = "Act01 MainActivity",
            MUSIC_PATH = "/Music/",
            MSG_ON_NO_MP3_SELECTED="Select a song";
    private TextView tv_playLs;
    private MP3Service service;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ACT,"onCreate");

        setWidgets();
    }

    @Override
    protected void onStart() {
        Log.d(ACT,"onStart");
        super.onStart();

        Intent i = new Intent(MainActivity.this,MP3Service.class);
        this.startService(i);
        this.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.d(ACT,"onStop");
        super.onStop();

        if(isBound){
            unbindService(serviceConnection);
            isBound=false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(ACT,"onDestroy");
        service.createNotification();
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(ACT, "onServiceConnected");
            MP3Service.MP3ServiceBinder binder = (MP3Service.MP3ServiceBinder) service;
            MainActivity.this.service = binder.getService();
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(ACT,"onServiceDisconnected");
            isBound=false;
        }
    };

    private void setWidgets(){
        tv_playLs = (TextView) findViewById(R.id.tv_playlist);
        tv_playLs.setText(MSG_ON_NO_MP3_SELECTED);

        final ListView lv = (ListView) findViewById(R.id.lv_playlist);
        File musicDir = new File(Environment.getExternalStorageDirectory().getPath() + MUSIC_PATH);

        File list[] = musicDir.listFiles();
        lv.setAdapter(new ArrayAdapter<File>(this,android.R.layout.simple_list_item_1, list));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));

                playNewMP3(selectedFromList);
            }
        });
    }


    private void playNewMP3(File file){
        Log.d(ACT,"playNewMP3");

        if (!isBound){
            Intent i = new Intent(MainActivity.this,MP3Service.class);
            this.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        if (service.playNewMP3(file.getAbsolutePath())){
            tv_playLs.setText(playlistLabel + file.getName());
        }
    }

    public void onStopBtn(View view){
        Log.d(ACT,"onStopBtn");
        service.stopMP3();
        onStopGUI();
    }



    private void onStopGUI(){
        Log.d(ACT,"onStopGUI");

        switch (service.getState()){
            case PLAYING:
            case PAUSED:
                break;
            case STOPPED:
                this.stopService(new Intent(this,MP3Service.class));
                break;
            default:
                break;
        }
    }

    public void onPlayBtn(View view){
        Log.d(ACT,"onPlayBtn");

        service.playMP3();
        onPlayGUI();
    }

    private void onPlayGUI(){
        switch (service.getState()){
            case PLAYING:
                break;
            case PAUSED:
                break;
            case STOPPED:
                if (service.getLastSong().equals("")) {
                    tv_playLs.setText(MSG_ON_NO_MP3_SELECTED);
                    Toast.makeText(this,MSG_ON_NO_MP3_SELECTED, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
