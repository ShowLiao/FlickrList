package com.example.show.testflicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by show on 1/11/18.
 */

public class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {


    private WeakReference<ImageView> imageView;

    public DownloadImageFromInternet(ImageView img) {
        this.imageView = new WeakReference<ImageView>(img);
    }

    protected Bitmap doInBackground(String... urls) {
        return downLoadBitmap(urls[0]);

    }

    protected void onPostExecute(Bitmap result) {

        if (isCancelled())
            result = null;

        if (imageView != null) {
            ImageView img = imageView.get();
            if (img != null) {
                if (result != null) {
                    img.setImageBitmap(result);
                } else {
                    Drawable placeholder = null;
                    img.setImageDrawable(placeholder);
                }
            }
        }
    }

    private Bitmap downLoadBitmap(String url) {
        Bitmap bimage = null;
        InputStream in = null;
        try {
            in = new java.net.URL(url).openStream();
            bimage = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error Message", e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != in)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return bimage;
    }
}
