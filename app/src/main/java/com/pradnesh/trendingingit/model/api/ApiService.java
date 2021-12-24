package com.pradnesh.trendingingit.model.api;

import com.pradnesh.trendingingit.model.apiresponse.GitResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("search/repositories")
    Call<GitResponse> getRepos(@Query("q") String searchString, @Query("sort") String sort);

}
