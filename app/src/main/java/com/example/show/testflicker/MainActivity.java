package com.example.show.testflicker;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HTTPConnect.Callback{

    private ArrayList<ImgConetent> apps;
    private static final String STR_KEY = "key";

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
        search("9windows");

        listView = findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "item" + position + " should open new Activity", Toast.LENGTH_LONG).show();
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

            apps = new ArrayList<>();
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

//                new DownloadImageFromInternet((ImageView) findViewById(R.id.imgView))
//                        .execute(apps.get(position).getSquarePhotoURL());
                ImageView img = convertView.findViewById(R.id.imgView);
                Bitmap bimage = null;
                try {
                    InputStream in = new java.net.URL(apps.get(position).getSquarePhotoURL()).openStream();
                    bimage = BitmapFactory.decodeStream(in);

                } catch (Exception e) {

                    e.printStackTrace();
                }
                 img.setImageBitmap(bimage);
//                return super.getView(position, convertView, parent);
                return convertView;
            }


        };

        listView.setAdapter(adapter);

    }

    @Override
    public void onNewMessage(List<ImgConetent> imgList) {
        Message msg = handler.obtainMessage();
        msg.obj = imgList;
        handler.sendMessage(msg);
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            apps = (ArrayList<ImgConetent>) msg.obj;

            for (ImgConetent c:apps) {
//
////                Log.e(fun, c.getId());
                Log.e("handle====", c.getSquarePhotoURL());
//                Log.e(fun, c.getTitle());
//                new DownloadImageFromInternet((ImageView) findViewById(R.id.imgView))
//                        .execute(c.getMediumPhotoURL());
            }

            loadListItems();
        }
    };

//    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
//        ImageView imageView;
//
//        public DownloadImageFromInternet(ImageView imageView) {
//            this.imageView = imageView;
////            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String imageURL = urls[0];
//            Bitmap bimage = null;
//            try {
//                InputStream in = new java.net.URL(imageURL).openStream();
//                bimage = BitmapFactory.decodeStream(in);
//
//            } catch (Exception e) {
//                Log.e("Error Message", e.getMessage());
//                e.printStackTrace();
//            }
//            return bimage;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            imageView.setImageBitmap(result);
//        }
//    }

}
