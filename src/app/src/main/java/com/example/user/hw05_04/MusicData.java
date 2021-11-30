package com.example.user.hw05_04;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2017-12-22.
 */

public class MusicData implements Parcelable {

    private String id;
    private String albumId;
    private String title;
    private String artist;
    private String path;
    private String duration;

    public MusicData() {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(albumId);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(path);
        dest.writeString(duration);
    }

    public static final Parcelable.Creator<MusicData> CREATOR = new Parcelable.Creator<MusicData>() {

        public MusicData createFromParcel(Parcel parcel) {
            return new MusicData(parcel);
        }

        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };


    public MusicData(Parcel parcel) {
        id = parcel.readString();
        albumId = parcel.readString();
        title = parcel.readString();
        artist = parcel.readString();
        path = parcel.readString();
        duration = parcel.readString();
       /* mCover = new byte[parcel.readInt()];
        parcel.readByteArray(mCover);*/
    }

    @Override
    public String toString() {
        return "MusicData{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}