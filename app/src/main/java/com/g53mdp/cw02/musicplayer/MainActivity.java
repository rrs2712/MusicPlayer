package com.g53mdp.cw02.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.g53mdp.cw02.musicplayer.R.id.seekBar;

public class MainActivity extends AppCompatActivity {

//    Widgets
    private String playlistLabel="Now playing: ";
    private TextView tv_playLs;
    private Button btn_play;

//    Activity
    private final String
            ACT = "Act01 MainActivity",
            THR = "Act04 ProgressThread",
            MUSIC_PATH = "/Music/",
            MSG_ON_NO_MP3_SELECTED="Select a song";

//    Service
    private MP3Service service;
    private boolean isBound = false;

//    Thread
    private final MP3ProgressThread mp3ProgressThread = new MP3ProgressThread();
    private boolean threadRunning = false;
    private final int THREAD_SLEEP_TIME = 10;
    private Handler handler = new Handler();

    // ## Activity	lifecycle management ## //

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

        threadRunning = true;
        mp3ProgressThread.start();
    }

    @Override
    protected void onStop() {
        Log.d(ACT,"onStop");
        super.onStop();

        threadRunning =false;

        if(isBound){
            unbindService(serviceConnection);
            isBound=false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(ACT,"onDestroy");
        super.onDestroy();

        service.createNotification();
    }

    // ## Event methods ## //

    /**
     * Manages actions to take when stop button is pressed
     * @param view
     */
    public void onStopBtn(View view){
        Log.d(ACT,"onStopBtn");
        service.stopMP3();
        onStopGUI();
    }

    /**
     * Manages actions to take when play button is pressed
     * @param view
     */
    public void onPlayBtn(View view){
        Log.d(ACT,"onPlayBtn");

        service.playMP3();
        onPlayGUI();
    }

    // ## Service methods ## //

    /**
     * Creates a connection to bind the service
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(ACT, "onServiceConnected");
            MP3Service.MP3ServiceBinder binder = (MP3Service.MP3ServiceBinder) service;
            MainActivity.this.service = binder.getService();
            isBound=true;

            onPlayGUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(ACT,"onServiceDisconnected");
            isBound=false;
        }
    };

    /**
     * Attempts to play a new song by using the service
     * @param file
     */
    private void playNewMP3(File file){
        Log.d(ACT,"playNewMP3");

        if (!isBound){
            Intent i = new Intent(MainActivity.this,MP3Service.class);
            this.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        if (service.playNewMP3(file.getAbsolutePath())){
            onPlayGUI();
        }
    }

    // ## Class methods ## //

    /**
     * Sets the widgets of this activity
     */
    private void setWidgets(){
        tv_playLs = (TextView) findViewById(R.id.tv_playlist);
        tv_playLs.setText(MSG_ON_NO_MP3_SELECTED);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setText("Play");

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

    /**
     * Modifies current UI according to the stop event
     */
    private void onStopGUI(){
        Log.d(ACT,"onStopGUI");

        switch (service.getState()){
            case PLAYING:
            case PAUSED:
                break;
            case STOPPED:
                this.stopService(new Intent(this,MP3Service.class));
                btn_play.setText("Play");
                break;
            default:
                break;
        }
    }

    /**
     * Modifies current UI according to the play event
     */
    private void onPlayGUI(){

        if(!service.getLastSong().equals("")){
            File f = new File(service.getLastSong());
            tv_playLs.setText(playlistLabel + f.getName());
        }

        switch (service.getState()){
            case PLAYING:
                btn_play.setText("Pause");
                break;
            case PAUSED:
                btn_play.setText("Play");
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

    /**
     * Class to create a new thread for song progress
     */
    class MP3ProgressThread extends Thread{
        MP3ProgressThread(){}

        @Override
        public void run() {
            super.run();

            Log.d(THR,"Thread running");
            while (threadRunning){
                if(service!=null){
                    try {Thread.sleep(THREAD_SLEEP_TIME);} catch(Exception e) {return;}

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
                            seekBar.setMax(service.getMP3Duration());
                            seekBar.setProgress(service.getMP3Progress());
                        }
                    });
                }
            }
            Log.d(THR,"Thread ending");
        }
    }
}
