package com.example.finalproject;

import android.net.Uri;

public class ImageItem {
    private String name;
    private Uri uri;

    public ImageItem(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

}
