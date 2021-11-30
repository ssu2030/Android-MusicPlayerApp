package com.example.user.hw05_04;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by user on 2017-12-22.
 */

public class MusicPlayerAdapter extends BaseAdapter {

    List<MusicData> myList;
    LayoutInflater myLayoutInflater;
    Activity myActivity;

    public MusicPlayerAdapter(Activity myActivity, List<MusicData> myList) {
        this.myList = myList;
        this.myActivity = myActivity;
        myLayoutInflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = myLayoutInflater.inflate(R.layout.listview_item, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(layoutParams);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.album);
        Bitmap albumImage = getAlbumImage(myActivity, Integer.parseInt((myList.get(position)).getAlbumId()), 170);
        imageView.setImageBitmap(albumImage);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(myList.get(position).getTitle());

        return convertView;
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    private static Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
        if (uri != null) {
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = res.openFileDescriptor(uri, "r");
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);
                int scale = 0;
                if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);
                if (bitmap != null) {
                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (parcelFileDescriptor != null)
                        parcelFileDescriptor.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
