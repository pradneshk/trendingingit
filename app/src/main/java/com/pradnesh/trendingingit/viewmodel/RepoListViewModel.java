package com.pradnesh.trendingingit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pradnesh.trendingingit.model.ReposRepository;
import com.pradnesh.trendingingit.model.RepositoryCallBack;
import com.pradnesh.trendingingit.model.apiresponse.GitResponse;
import com.pradnesh.trendingingit.model.apiresponse.Item;

import java.util.List;

public class RepoListViewModel extends BaseViewModel {

    MutableLiveData<List<Item>> repoList = new MutableLiveData<>();

    public LiveData<List<Item>> getRepoList() {
        return repoList;
    }

    public void fetchRepoList(){
        dataLoading.setValue(true);
        ReposRepository.getInstance().getRepos((isSuccess, response) -> {
            dataLoading.setValue(false);
            if(isSuccess){
                empty.setValue(false);
                repoList.setValue(response.getItems());
            } else {
                empty.setValue(true);
                toastMessage.setValue("Oops something went wrong!");
            }

        });
    }
}
