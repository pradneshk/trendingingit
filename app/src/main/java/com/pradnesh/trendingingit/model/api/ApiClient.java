package com.pradnesh.trendingingit.model.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Singleton Pattern
public class ApiClient {

    private static ApiClient INSTANCE = null;
    private static String BASE_URL = "https://api.github.com/"; // Made non-final

    public ApiService apiService;

    // Private constructor for singleton
    private ApiClient(String baseUrl) {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();
        apiService = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build()
                .create(ApiService.class);
    }

    // Public method to get instance
    synchronized public static ApiClient getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ApiClient(BASE_URL);
        }
        return INSTANCE;
    }

    // Method for testing to change the base URL and re-initialize the client
    // This should be used with caution and ideally only in test environments.
    synchronized public static void setBaseUrlForTesting(String testBaseUrl) {
        BASE_URL = testBaseUrl;
        // Force re-creation of the instance with the new base URL next time getInstance() is called.
        INSTANCE = null;
    }

    // Optional: A way to reset to the default URL after tests, if needed.
    synchronized public static void resetBaseUrl() {
        BASE_URL = "https://api.github.com/";
        INSTANCE = null;
    }
}