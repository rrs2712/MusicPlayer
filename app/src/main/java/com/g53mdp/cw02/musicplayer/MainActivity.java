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

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String
            lastSelectedSong="",
            playlistLabel="Now playing: ";
    private final String
            ACT = "Act01 MainActivity",
            MUSIC_PATH = "/Music/",
            MSG_ON_NO_MP3_SELECTED="Select a song";
    private TextView tv_playLs;
    private MP3Service.MP3ServiceBinder serviceBinder = null;

    static String FILE_ABS_PATH = "file_absolute_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ACT,"onCreate");

        setWidgets();

        Intent i = new Intent(MainActivity.this,MP3Service.class);
        this.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(ACT, "onServiceConnected");
            serviceBinder = (MP3Service.MP3ServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(ACT,"onServiceDisconnected");
            serviceBinder = null;
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

                playNewSong(selectedFromList);
            }
        });
    }


    private void playNewSong(File file){
        tv_playLs.setText(playlistLabel + file.getName());
        lastSelectedSong = file.getAbsolutePath();

        serviceBinder.onPlayNewSongMP3(lastSelectedSong);
    }

    public void onStopBtn(View view){
        onStopGUI();
        serviceBinder.onStopMP3();
    }

    private void onStopGUI(){
//        switch (mp3Player.getState()){
//            case PLAYING:
//            case PAUSED:
//                this.stopService(new Intent(this,MP3Service.class));
//                break;
//            case STOPPED:
//                break;
//            default:
//                break;
//        }
    }

    public void onPlayBtn(View view){
        onPlayGUI();
        serviceBinder.onPlayMP3();
    }

    private void onPlayGUI(){
//        switch (mp3Player.getState()){
//            case PLAYING:
//                break;
//            case PAUSED:
//                break;
//            case STOPPED:
//                if (!lastSelectedSong.equals("")) {
//                } else {
//                    tv_playLs.setText(MSG_ON_NO_MP3_SELECTED);
//                    Toast.makeText(this,MSG_ON_NO_MP3_SELECTED, Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }
    }

    @Override
    protected void onDestroy() {
        if(serviceConnection!=null){
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
        Log.d(ACT,"onDestroy");
    }


}
