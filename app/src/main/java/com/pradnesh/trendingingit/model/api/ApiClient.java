package com.pradnesh.trendingingit.model.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Singleton Pattern
public class ApiClient {

    private static ApiClient INSTANCE = null;
    private static final String BASE_URL = "https://api.github.com/";

    public ApiService apiService;

    private ApiClient() {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();
        apiService = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build()
                .create(ApiService.class);
    }

    synchronized public static ApiClient getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ApiClient();
        }
        return INSTANCE;
    }
}