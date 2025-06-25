package com.pradnesh.trendingingit.model.api

import com.pradnesh.trendingingit.model.apiresponse.GitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search/repositories")
    fun getRepos(@Query("q") searchString: String, @Query("sort") sort: String): Call<GitResponse>
}
