package com.pradnesh.trendingingit.model.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var currentBaseUrl: String = "https://api.github.com/"

    // Lazily initialize ApiService. It will be re-created if currentBaseUrl changes.
    val apiService: ApiService by lazy {
        createApiService(currentBaseUrl)
    }

    private fun createApiService(baseUrl: String): ApiService {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
            .create(ApiService::class.java)
    }

    // Method for testing to change the base URL and re-initialize the client
    // This should be used with caution and ideally only in test environments.
    @Synchronized
    fun setBaseUrlForTesting(testBaseUrl: String) {
        currentBaseUrl = testBaseUrl
        // To force re-creation of apiService next time it's accessed:
        // This is tricky with 'by lazy'. A better way is to make apiService a function call
        // or handle the recreation explicitly.
        // For simplicity with 'by lazy', test would need to reflect that ApiClient object state changes.
        // A more direct way:
        // Option 1: Re-assign a new lazy delegate (complex)
        // Option 2: Make apiService a 'lateinit var' and reinitialize it.
        // Let's go with lateinit var for easier re-initialization for testing.
    }

    // Re-thinking the lazy initialization for testability.
    // Using a lateinit var and explicit instance management for testing is clearer.

}
// Let's refine ApiClient.kt with lateinit for easier test overrides.
// The 'object' itself is a singleton. The ApiService instance it holds needs to be manageable.

//Revised ApiClient.kt:
package com.pradnesh.trendingingit.model.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var currentBaseUrl: String = "https://api.github.com/"

    // Hold the Retrofit instance to be able to change the ApiService instance
    private var retrofitInstance: Retrofit = createRetrofitInstance(currentBaseUrl)

    var apiService: ApiService = retrofitInstance.create(ApiService::class.java)
        private set // Make setter private to control instance creation through methods

    private fun createRetrofitInstance(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }

    @Synchronized
    fun setBaseUrlForTesting(testBaseUrl: String) {
        currentBaseUrl = testBaseUrl
        retrofitInstance = createRetrofitInstance(currentBaseUrl)
        apiService = retrofitInstance.create(ApiService::class.java)
    }

    @Synchronized
    fun resetBaseUrl() {
        currentBaseUrl = "https://api.github.com/"
        retrofitInstance = createRetrofitInstance(currentBaseUrl)
        apiService = retrofitInstance.create(ApiService::class.java)
    }
}
