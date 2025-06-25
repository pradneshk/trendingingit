package com.pradnesh.trendingingit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pradnesh.trendingingit.model.ReposRepository;
import com.pradnesh.trendingingit.model.RepositoryCallBack;
import com.pradnesh.trendingingit.model.apiresponse.GitResponse;
import com.pradnesh.trendingingit.util.EspressoIdlingResource; // Import Idling Resource
import com.pradnesh.trendingingit.model.apiresponse.Item;

import java.util.List;

public class RepoListViewModel extends BaseViewModel {

    MutableLiveData<List<Item>> repoList = new MutableLiveData<>();

    public LiveData<List<Item>> getRepoList() {
        return repoList;
    }

    public void fetchRepoList(){
        EspressoIdlingResource.increment(); // Increment before network call
        dataLoading.setValue(true);
        ReposRepository.getInstance().getRepos((isSuccess, response) -> {
            try {
                dataLoading.setValue(false);
                if (isSuccess && response != null && response.getItems() != null) {
                    repoList.setValue(response.getItems());
                    empty.setValue(response.getItems().isEmpty());
                } else {
                    // Handles isSuccess == false OR response is null OR getItems() is null
                    empty.setValue(true);
                    toastMessage.setValue("Oops something went wrong!");
                }
            } finally {
                EspressoIdlingResource.decrement(); // Decrement after network call and processing
            }
        });
    }
}
