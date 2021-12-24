package com.pradnesh.trendingingit.model;

import android.util.Log;

import com.pradnesh.trendingingit.model.api.ApiClient;
import com.pradnesh.trendingingit.model.apiresponse.GitResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposRepository {

    private static ReposRepository INSTANCE;

    private ReposRepository(){
        //do nothing
    }

    public void getRepos(RepositoryCallBack callBack){
        ApiClient.getInstance().apiService.getRepos("trending+git","star")
                .enqueue(new Callback<GitResponse>() {
                    @Override
                    public void onResponse(Call<GitResponse> call, Response<GitResponse> response) {
                        if(response!=null && response.isSuccessful())
                            callBack.onResult(true, response.body());
                        else
                            callBack.onResult(false,null);
                    }

                    @Override
                    public void onFailure(Call<GitResponse> call, Throwable t) {
                        Log.e("Repoository","Something went wrong with network call", t);
                    }
                });
    }

    public static ReposRepository getInstance(){
        if(INSTANCE == null)
            INSTANCE = new ReposRepository();
        return INSTANCE;
    }

}