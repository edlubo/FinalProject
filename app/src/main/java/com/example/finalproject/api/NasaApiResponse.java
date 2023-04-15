package com.example.finalproject.api;

import com.google.gson.annotations.SerializedName;

public class NasaApiResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("url")
    private String url;

    @SerializedName("hdurl")
    private String hdUrl;

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getHdUrl() {
        return hdUrl;
    }
}
