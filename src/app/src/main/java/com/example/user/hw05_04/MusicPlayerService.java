package com.example.user.hw05_04;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 2017-12-22.
 */

public class MusicPlayerService extends Service {
    protected static final String PLAY = "play";
    protected static final String PAUSE = "pause";
    protected static final String PREV = "prev";
    protected static final String NEXT = "next";


    BroadcastReceiver broadcastReceiver;

    public static int position;
    public static MediaPlayer serviceInMediaPlayer;
    public static ArrayList<MusicData> list;
    public static  Thread musicPlayThread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        position = intent.getIntExtra("position", 0);
        list = (ArrayList<MusicData>) intent.getSerializableExtra("playlist");

        musicPlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (serviceInMediaPlayer == null) {
                    serviceInMediaPlayer = new MediaPlayer();
                } else {
                    serviceInMediaPlayer.stop();
                    serviceInMediaPlayer = new MediaPlayer();
                }
                try {
                    MusicBroadcast();
                    playMusic(list.get(position));
                } catch (Exception e) {
                }
                while (true) {
                    musicNotification();
                    try {
                    } catch (Exception e) {
                    }
                }
            }
        });
        musicPlayThread.start();
        return START_REDELIVER_INTENT;
    }

    public void playMusic(MusicData musicData) {
        try {
            Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicData.getId());
            serviceInMediaPlayer.reset();
            serviceInMediaPlayer.setDataSource(this, musicURI);
            serviceInMediaPlayer.prepare();
            serviceInMediaPlayer.start();
        } catch (Exception e) {
        }
    }

    private void MusicBroadcast() {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PLAY)) {
                    serviceInMediaPlayer.start();
                } else if (intent.getAction().equals(PAUSE)) {
                    serviceInMediaPlayer.pause();
                } else if (intent.getAction().equals(PREV)) {
                    if(position -1 >=0){
                        position--;
                        playMusic(list.get(position));
                    }
                } else if (intent.getAction().equals(NEXT)) {
                    if(position + 1 < list.size()){
                        position++;
                        playMusic(list.get(position));
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PAUSE);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);

        registerReceiver(this.broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (serviceInMediaPlayer != null) {
            serviceInMediaPlayer.release();
            serviceInMediaPlayer = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void musicNotification() {
        try {
            Intent intent_activity = new Intent(this, MusicPlayerActivity.class);
            intent_activity.putExtra("position", position);
            intent_activity.putExtra("playlist", list);

            PendingIntent pendingIntent_activity = PendingIntent.getActivity(this, 0, intent_activity, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadcast_prev = new Intent();
            broadcast_prev.setAction(PREV);
            PendingIntent pendingIntent_prev = PendingIntent.getBroadcast(this, 1, broadcast_prev, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadcast_play = new Intent();
            broadcast_play.setAction(PLAY);
            PendingIntent pendingIntent_play = PendingIntent.getBroadcast(this, 2, broadcast_play, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadcast_pause = new Intent();
            broadcast_pause.setAction(PAUSE);
            PendingIntent pendingIntent_pause = PendingIntent.getBroadcast(this, 3, broadcast_pause, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadcast_next = new Intent();
            broadcast_next.setAction(NEXT);
            PendingIntent pendingIntent_next = PendingIntent.getBroadcast(this, 4, broadcast_next, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews musicRemoteView = new RemoteViews(getPackageName(), R.layout.activity_notification);

            Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(list.get(position).getAlbumId()), getApplication()));

            musicRemoteView.setImageViewBitmap(R.id.imageView_album, bitmap);
            musicRemoteView.setTextViewText(R.id.textView_title, list.get(position).getArtist() + " - " + list.get(position).getTitle());


            musicRemoteView.setImageViewResource(R.id.pauseorplay, R.drawable.pause);
            musicRemoteView.setImageViewResource(R.id.pre, R.drawable.previous);
            musicRemoteView.setImageViewResource(R.id.next, R.drawable.next);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            if (serviceInMediaPlayer.isPlaying() == true) {
                builder.setSmallIcon(R.drawable.play);
                musicRemoteView.setImageViewResource(R.id.pauseorplay, R.drawable.pause);
            } else {
                builder.setSmallIcon(R.drawable.pause);
                musicRemoteView.setImageViewResource(R.id.pauseorplay, R.drawable.play);
            }
            musicRemoteView.setOnClickPendingIntent(R.id.imageView_album, pendingIntent_activity);
            musicRemoteView.setOnClickPendingIntent(R.id.pre, pendingIntent_prev);
            musicRemoteView.setOnClickPendingIntent(R.id.next, pendingIntent_next);

            if (serviceInMediaPlayer.isPlaying() == true) {
                musicRemoteView.setOnClickPendingIntent(R.id.pauseorplay, pendingIntent_pause);
            } else {
                musicRemoteView.setOnClickPendingIntent(R.id.pauseorplay, pendingIntent_play);
            }

            builder.setContentIntent(pendingIntent_activity);

            builder.setContentIntent(pendingIntent_prev);
            builder.setContentIntent(pendingIntent_play);
            builder.setContentIntent(pendingIntent_next);

            builder.setCustomContentView(musicRemoteView);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
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
}