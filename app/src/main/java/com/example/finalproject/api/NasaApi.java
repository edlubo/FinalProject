package com.example.finalproject.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApi {
    @GET("planetary/apod")
    Call<NasaApiResponse> getApod(@Query("api_key") String apiKey, @Query("date") String date);
}
