package com.example.show.testflicker;

import android.graphics.Bitmap;

/**
 * Created by show on 1/10/18.
 */

public class ImgConetent {
    String id;
    String owner;
    Bitmap photo;
    String secret;
    String server;
    String farm;
    String squarePhotoURL;
    String mediumPhotoURL;
    String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getSquarePhotoURL() {
        return squarePhotoURL;
    }

    public void setSquarePhotoURL(String squarePhotoURL) {
        this.squarePhotoURL = squarePhotoURL;
    }

    public String getMediumPhotoURL() {
        return mediumPhotoURL;
    }

    public void setMediumPhotoURL(String mediumPhotoURL) {
        this.mediumPhotoURL = mediumPhotoURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
