package com.example.show.testflicker;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTPConnect implements Runnable {

    final static String fun = "HttpConn";
    private Callback mCallback;

    private static int CONNECT_TIMEOUT_MS = 5000;
    private static int READ_TIMEOUT_MS = 15000;

    String url;

    public HTTPConnect(String url)
    {
        this.url = url;
    }

    public interface Callback {
        void onNewMessage(String string);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    //    public void newMessage(ByteArrayOutputStream in) {
    public void collectionSearchInfo(ByteArrayOutputStream in) {

        String jsonString = "";

        if (mCallback != null)
            jsonString = in.toString();

        mCallback.onNewMessage(jsonString);
    }

    public void newThread() {

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
