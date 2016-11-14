package com.g53mdp.cw02.musicplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final String
            ACT = "Act01 MainActivity",
            MUSIC_PATH = "/Music/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ACT,"onCreate");

        setPlayList();
    }

    private void setPlayList(){
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
                Log.d(ACT, selectedFromList.getAbsolutePath());
                // do something with selectedFromList...
            }
        });
    }
}
