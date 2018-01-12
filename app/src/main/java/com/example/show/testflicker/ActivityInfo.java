package com.example.show.testflicker;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.show.testflicker.StringValue.*;

public class ActivityInfo extends AppCompatActivity implements HTTPConnect.Callback {

    TextView textTitle;
    TextView textUserName;
    TextView textTags;
    ImageView imgView;

    ActivityInfo self;
    ImgDetailInfo imgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        self = this;

        Intent intent = getIntent();

        String id = intent.getStringExtra(STR_ID);
        getDetailInfo(id);
        imgInfo = new ImgDetailInfo();
        imgInfo.id = id;
        imgInfo.setMediumPhotoURL(intent.getStringExtra(STR_URL));

    }

    public void getDetailInfo(String id) {

        String url = FlickrMgr.getPhotoInfo(id);
        HTTPConnect conn = new HTTPConnect(url);
        conn.setCallback(self);
        conn.newThread();
    }


    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            processData(msg);
            displayInfo();

        }
    };

    @Override
    public void onNewMessage(String img) {
        Message msg = handler.obtainMessage();
        msg.obj = img;
        handler.sendMessage(msg);
    }

    private void processData(Message msg) {

        String jsonString = (String)msg.obj;
        try {
            JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
            JSONObject photo = root.getJSONObject(STR_PHOTO);
            JSONObject owner = photo.getJSONObject(STR_OWNER);
            JSONObject title = photo.getJSONObject(STR_TITLE);
            JSONObject desc = photo.getJSONObject(STR_DESC);

            imgInfo.setUsername(owner.getString(STR_USER_NAME));
            imgInfo.setDesc(desc.getString(STR_CONTENT));
            imgInfo.setTitle(title.getString(STR_CONTENT));

            JSONObject theTags = photo.getJSONObject(STR_TAGS);
            JSONArray  tags = theTags.getJSONArray(STR_TAG);
            String tag = "";
            for (int j=0; j<tags.length(); j++) {

                JSONObject tmp = tags.getJSONObject(j);
                if (0 == tag.length())
                    tag = STR_TAGS + ":" + tmp.getString(STR_CONTENT);
                else
                    tag = tag + "," + tmp.getString(STR_CONTENT);

            }
            imgInfo.setTag(tag);

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void displayInfo() {

        textUserName = findViewById(R.id.textUserName);
        textUserName.setText(imgInfo.getUsername());

        imgView = findViewById(R.id.imgViewInfo);
        new DownloadImageFromInternet((ImageView) findViewById(R.id.imgViewInfo))
                .execute(imgInfo.getMediumPhotoURL());

        textTitle = findViewById(R.id.textTitle);
        textTitle.setText(imgInfo.getTitle());

        textTags = findViewById(R.id.textTags);
        textTags.setText(imgInfo.getTag());
    }

}
