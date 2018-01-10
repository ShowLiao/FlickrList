package com.example.show.testflicker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ProcessingInstruction;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.HttpsURLConnection;

public class HTTPConnect implements Runnable {

    final static String fun = "HttpConn";
    private Callback mCallback;

    private static int CONNECT_TIMEOUT_MS = 5000;
    private static int READ_TIMEOUT_MS = 15000;

    public  static final int FLICKR_IMG_SQUARE = 1;
    public  static final int FLICKR_IMG_MED = 2;

    ArrayList<ImgConetent> list = new ArrayList<ImgConetent>();
    String url;
    public HTTPConnect(String url) {
        this.url = url;
    }

    public interface Callback {
        void onNewMessage(List<ImgConetent> string);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    //    public void newMessage(ByteArrayOutputStream in) {
    public void collectionSearchInfo(ByteArrayOutputStream in) {
        if (mCallback != null) {

            String jsonString = in.toString();

            try {
                JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
                JSONObject photos = root.getJSONObject("photos");
                JSONArray imageJSONArray = photos.getJSONArray("photo");
                for (int i = 0; i < imageJSONArray.length(); i++) {
                    JSONObject item = imageJSONArray.getJSONObject(i);

                    ImgConetent img = new ImgConetent();
                    img.setId(item.getString("id"));
                    img.setOwner(item.getString("owner"));
                    img.setSecret(item.getString("secret"));
                    img.setServer(item.getString("server"));
                    img.setFarm(item.getString("farm"));
                    img.setTitle(item.getString("title"));
                    img.setSquarePhotoURL(createPhotoURL(FLICKR_IMG_SQUARE, img));
                    img.setMediumPhotoURL(createPhotoURL(FLICKR_IMG_MED, img));

                    list.add(img);

                }

                mCallback.onNewMessage(list);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }



    private String createPhotoURL(int photoType, ImgConetent imgCon) {
        String tmp = null;
        tmp = "http://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server + "/" + imgCon.id + "_" + imgCon.secret;// +".jpg";
        switch (photoType) {
            case FLICKR_IMG_SQUARE:
                tmp += "_t";
                break;
            case FLICKR_IMG_MED:
                tmp += "_z";
                break;

        }
        tmp += ".jpg";
        return tmp;
    }

    public void newThread() {
        Log.e(fun, "newThread");
        Thread childThread = new Thread(this, "newThread");
        childThread.start();
    }

    @Override
    public void run() {

        getImageByTag();

    }

    public void getImageByTag() {

        try {

            URL mUrl = new URL(url);
            ByteArrayOutputStream baos = null;
            InputStream is = null;

            HttpsURLConnection httpURLConnection = null;

            httpURLConnection = (HttpsURLConnection) mUrl.openConnection();
            int response = httpURLConnection.getResponseCode();
            if (response == HttpsURLConnection.HTTP_OK) {
                Log.e(fun, "pass the response code!!!!!");
                httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                httpURLConnection.setReadTimeout(READ_TIMEOUT_MS);
                is = new BufferedInputStream(httpURLConnection.getInputStream());

                int size = 1024;
                byte[] buffer = new byte[size];

                baos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = is.read(buffer)) != -1) {
                    if (read > 0) {
                        baos.write(buffer, 0, read);
                        buffer = new byte[size];
                    }

                }
                is.close();
                httpURLConnection.disconnect();
            }

            collectionSearchInfo(baos);

        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
