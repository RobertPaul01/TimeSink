package com.abe.robert.timesink;

import android.net.Uri;

/**
 * Created by Abe on 4/1/2017.
 */

public class User {

    private String name, email, uid;
    private Uri photoUri;

    public User(String name, String email, Uri photoUri, String uid) {
        this.name = name;
        this.email = email;
        this.photoUri = photoUri;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
