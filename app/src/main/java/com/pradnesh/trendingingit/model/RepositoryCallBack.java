package com.pradnesh.trendingingit.model;

import com.pradnesh.trendingingit.model.apiresponse.GitResponse;

public interface RepositoryCallBack {
    public void onResult(boolean isSuccess, GitResponse response);
}
