package com.pradnesh.trendingingit.model

import com.pradnesh.trendingingit.model.apiresponse.GitResponse

fun interface RepositoryCallBack {
    fun onResult(isSuccess: Boolean, response: GitResponse?)
}
