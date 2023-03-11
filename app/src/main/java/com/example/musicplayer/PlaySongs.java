package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySongs extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaplayer.stop();
        mediaplayer.release();
        updateSeek.interrupt();
    }

    TextView textview;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaplayer;
    String text;
    int pos;
    SeekBar seekbar;
    Thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        textview = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekbar = findViewById(R.id.seekBar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songlist");
        text = intent.getStringExtra("currentSong");
        textview.setText(text);
        textview.setSelected(true);
        pos = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(pos).toString());
        mediaplayer = MediaPlayer.create(this, uri);
        mediaplayer.start();
        seekbar.setMax(mediaplayer.getDuration());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaplayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread() {
            @Override
            public void run() {
                int curr = 0;
                try {
                    while (curr < mediaplayer.getDuration()) {
                        curr = mediaplayer.getCurrentPosition();
                        seekbar.setProgress(curr);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaplayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaplayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaplayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (pos != 0) {
                    pos = pos - 1;
                } else {
                    pos = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(pos).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaplayer.start();
                play.setImageResource(R.drawable.pause);
                seekbar.setMax(mediaplayer.getDuration());
                text =songs.get(pos).getName().toString();
                textview.setText(text);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (pos!= songs.size()-1) {
                    pos = pos + 1;
                } else {
                    pos = 0;
                }
                Uri uri = Uri.parse(songs.get(pos).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaplayer.start();
                play.setImageResource(R.drawable.pause);
                seekbar.setMax(mediaplayer.getDuration());
                text = songs.get(pos).getName().toString();
                textview.setText(text);
            }
        });
    }
}