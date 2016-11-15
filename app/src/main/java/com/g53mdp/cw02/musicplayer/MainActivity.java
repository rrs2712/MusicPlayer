package com.g53mdp.cw02.musicplayer;

import android.content.Intent;
import android.os.Environment;
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

import static com.g53mdp.cw02.musicplayer.MP3Player.MP3PlayerState.PAUSED;
import static com.g53mdp.cw02.musicplayer.MP3Player.MP3PlayerState.PLAYING;

public class MainActivity extends AppCompatActivity {

    //LAS = LLevame al servicio
    private MP3Player mp3Player = new MP3Player();
    private String
            lastSelectedSong="",
            playlistLabel="Now playing: ";
    private final String
            ACT = "Act01 MainActivity",
            MUSIC_PATH = "/Music/",
            MSG_ON_NO_MP3_SELECTED="Select a song";
    private TextView tv_playLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ACT,"onCreate");

        setWidgets();
    }

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
                //LAS
                playNewSong(selectedFromList);
            }
        });
    }

    //LAS
    private void playNewSong(File file){
        this.startService(new Intent(this,MP3Service.class));

        tv_playLs.setText(playlistLabel + file.getName());

        lastSelectedSong = file.getAbsolutePath();

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

    public void onStopBtn(View view){
        switch (mp3Player.getState()){
            case PLAYING:
            case PAUSED:
                mp3Player.stop();
                this.stopService(new Intent(this,MP3Service.class));
                break;
            case STOPPED:
                break;
            default:
                break;
        }
    }

    public void onPlayBtn(View view){
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
                    tv_playLs.setText(MSG_ON_NO_MP3_SELECTED);
                    Toast.makeText(this,MSG_ON_NO_MP3_SELECTED, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


}
