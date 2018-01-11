package com.example.show.testflicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.show.testflicker.StringValue.*;

public class MainActivity extends AppCompatActivity implements HTTPConnect.Callback {

    private ArrayList<ImgConetent> apps;

    MainActivity self;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        self = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        createIntentFilter();

        apps = new ArrayList<>();
        search(STR_KEY);

        listView = findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImgConetent img = (ImgConetent) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ActivityInfo.class);
                intent.putExtra(STR_ID,img.getId());
                intent.putExtra(STR_URL, img.getMediumPhotoURL());
                startActivity(intent);


            }
        });
    }

    private void createIntentFilter() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(STR_KEY);

        getApplicationContext().registerReceiver(mReceiver, filter);
    }

    private void search(String strKey) {
        String url = FlickrMgr.searchPhoto(strKey);
        HTTPConnect conn = new HTTPConnect(url);
        conn.setCallback(self);
        conn.newThread();
    }

    private void openDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        SearchDialogFragment dia = SearchDialogFragment.newInstance("Search");
        dia.show(fm, "activity_search_dialog_fragment");
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String str = intent.getAction().toString();
            if (str.equals(STR_KEY)) {
                str = intent.getStringExtra(STR_SEARCH);
            }

            if (apps != null)
                apps.clear();

            search(str);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadListItems() {
        ListView listView = findViewById(R.id.list_item);

        ArrayAdapter<ImgConetent> adapter = new ArrayAdapter<ImgConetent>(this, R.layout.item, apps) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                if (null == convertView)
                    convertView = getLayoutInflater().inflate(R.layout.item, null);

                TextView viewLabel = convertView.findViewById(R.id.photo_label);
                viewLabel.setText(apps.get(position).getTitle());

                TextView viewDesc = convertView.findViewById(R.id.photo_desc);
                viewDesc.setText(apps.get(position).getTitle());

                new DownloadImageFromInternet((ImageView) findViewById(R.id.imgView))
                        .execute(apps.get(position).getSquarePhotoURL());

                return convertView;
            }


        };

        listView.setAdapter(adapter);

    }

    @Override
    public void onNewMessage(String imgList) {
        Message msg = handler.obtainMessage();
        msg.obj = imgList;
        handler.sendMessage(msg);
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            processData(msg);
            loadListItems();

        }
    };

    private void processData(Message msg) {

        String jsonString = (String)msg.obj;
        try {
            JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
            JSONObject photos = root.getJSONObject(STR_PHOTOS);
            JSONArray imageJSONArray = photos.getJSONArray(STR_PHOTO);
            for (int i = 0; i < imageJSONArray.length(); i++) {
                JSONObject item = imageJSONArray.getJSONObject(i);

                ImgConetent img = new ImgConetent();
                img.setId(item.getString(STR_ID));
                img.setOwner(item.getString(STR_OWNER));
                img.setSecret(item.getString(STR_SECRET));
                img.setServer(item.getString(STR_SERVER));
                img.setFarm(item.getString(STR_FARM));
                img.setTitle(item.getString(STR_TITLE));
                img.setSquarePhotoURL(FlickrMgr.createPhotoURL(FlickrMgr.FLICKR_IMG_SQUARE, img));
                img.setMediumPhotoURL(FlickrMgr.createPhotoURL(FlickrMgr.FLICKR_IMG_MED, img));

                apps.add(img);

            }

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
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

}
