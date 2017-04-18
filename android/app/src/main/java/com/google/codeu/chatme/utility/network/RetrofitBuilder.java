package com.google.codeu.chatme.utility.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    public static final String BASE_URL = "https://us-central1-chatme-e99a4.cloudfunctions.net/";

    public static FirebaseService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FirebaseService service = retrofit.create(FirebaseService.class);
        return service;
    }
}
