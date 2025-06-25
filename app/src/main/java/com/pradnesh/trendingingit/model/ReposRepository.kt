package com.pradnesh.trendingingit.model

import android.util.Log
import com.pradnesh.trendingingit.model.api.ApiClient
import com.pradnesh.trendingingit.model.apiresponse.GitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ReposRepository {

    private const val TAG = "ReposRepository"

    fun getRepos(callBack: RepositoryCallBack) {
        ApiClient.apiService.getRepos("trending+git", "star")
            .enqueue(object : Callback<GitResponse> {
                override fun onResponse(call: Call<GitResponse>, response: Response<GitResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        callBack.onResult(true, response.body())
                    } else {
                        Log.e(TAG, "API call failed or returned empty body. Code: ${response.code()}, Message: ${response.message()}")
                        callBack.onResult(false, null)
                    }
                }

                override fun onFailure(call: Call<GitResponse>, t: Throwable) {
                    Log.e(TAG, "Something went wrong with network call", t)
                    callBack.onResult(false, null) // Notify callback on failure
                }
            })
    }
}
