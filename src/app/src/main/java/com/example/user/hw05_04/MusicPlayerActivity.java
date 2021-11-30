package com.example.user.hw05_04;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<MusicData> list;
    private int position;
    private MediaPlayer mediaPlayer;
    private TextView title, currentTimeSet, totalTimeSet;
    private ImageView album, previous, play, pause, next;
    private SeekBar seekBar;
    boolean isPlaying = true;
    private ContentResolver contentResolver;
    private ProgressUpdate progressUpdate;

    public static Thread musicPlayThread = null;

    int duration_time = 0;
    MusicPlayerService musicPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Intent intent = getIntent();

        position = intent.getIntExtra("position", 0);
        list = (ArrayList<MusicData>) intent.getSerializableExtra("playlist");

        mediaPlayer = new MediaPlayer();

        album = (ImageView) findViewById(R.id.album);
        title = (TextView) findViewById(R.id.title);
        currentTimeSet = (TextView) findViewById(R.id.current_time_set);
        totalTimeSet = (TextView) findViewById(R.id.total_time_set);
        seekBar = (SeekBar) findViewById(R.id.seekbar);

        previous = (ImageView) findViewById(R.id.pre);
        play = (ImageView) findViewById(R.id.play);
        pause = (ImageView) findViewById(R.id.pause);
        next = (ImageView) findViewById(R.id.next);

        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);

        contentResolver = getContentResolver();
        if (mediaPlayer.isPlaying() == false) {
            musicPlayThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playMusic(list.get(position));
                }
            });
            musicPlayThread.start();
        }else if (mediaPlayer.isPlaying() == true) {
            musicPlayThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playMusic(musicPlayerService.list.get(musicPlayerService.position));
                }
            });
            musicPlayThread.start();
        };
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        //시크바 구현부분이당
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position + 1 < list.size()) {
                    position++;
                    playMusic(list.get(position));
                }
            }
        });
    }
    public void playMusic(MusicData musicData) {
        try {
            Intent serviceIntent = new Intent(this, MusicPlayerService.class);
            serviceIntent.putExtra("position", position);
            serviceIntent.putExtra("playlist", list);
            serviceIntent.putExtra("uri", list.get(position).getPath());
            startService(serviceIntent);
            seekBar.setProgress(0);
            title.setText(musicData.getArtist() + " - " + musicData.getTitle());
            title.setSelected(true);
            Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicData.getId());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();

            seekBar.setMax(mediaPlayer.getDuration());
            Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicData.getAlbumId()), getApplication()));
            album.setImageBitmap(bitmap);
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            duration_time = mediaPlayer.getDuration();
            if ((duration_time / 1000) % 60 < 10) {
                totalTimeSet.setText("0" + duration_time / 60000 + ":0" + (duration_time / 1000) % 60);
            } else {
                totalTimeSet.setText("0" + duration_time / 60000 + ":" + (duration_time / 1000) % 60);
            }
        } catch (Exception e) {
        }
    }
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                musicPlayerService.serviceInMediaPlayer.seekTo(musicPlayerService.serviceInMediaPlayer.getCurrentPosition());
                musicPlayerService.serviceInMediaPlayer.start();

                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                musicPlayerService.serviceInMediaPlayer.pause();

                break;
            case R.id.pre:
                if (position - 1 >= 0) {
                    position--;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                }
                break;
            case R.id.next:
                if (position + 1 < list.size()) {
                    position++;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                }
                break;
        }
    }
    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(100);
                    if (mediaPlayer != null) {
                        seekBar.setProgress(musicPlayerService.serviceInMediaPlayer.getCurrentPosition());
                        if ((musicPlayerService.serviceInMediaPlayer.getCurrentPosition() / 1000) % 60 < 10) {
                            currentTimeSet.setText("0" + musicPlayerService.serviceInMediaPlayer.getCurrentPosition() / 60000 + ":0" + (musicPlayerService.serviceInMediaPlayer.getCurrentPosition() / 1000) % 60);
                        } else {
                            currentTimeSet.setText("0" + musicPlayerService.serviceInMediaPlayer.getCurrentPosition() / 60000 + ":" + (musicPlayerService.serviceInMediaPlayer.getCurrentPosition() / 1000) % 60);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate", e.getMessage());
                }
            }
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Intent backToMain = new Intent(this , MainActivity.class);
        startActivity(backToMain);
        finish();
        super.onDestroy();
    }
}











