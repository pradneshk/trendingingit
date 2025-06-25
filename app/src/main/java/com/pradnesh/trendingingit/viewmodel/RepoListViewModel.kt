package com.pradnesh.trendingingit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pradnesh.trendingingit.model.ReposRepository
import com.pradnesh.trendingingit.model.RepositoryCallBack
import com.pradnesh.trendingingit.model.apiresponse.Item
import com.pradnesh.trendingingit.util.EspressoIdlingResource

class RepoListViewModel : BaseViewModel() {

    // Backing property for repoList
    private val _repoList = MutableLiveData<List<Item>>()
    // Public LiveData for observing repoList
    val repoList: LiveData<List<Item>> = _repoList

    fun fetchRepoList() {
        EspressoIdlingResource.increment() // Increment before network call
        _dataLoading.value = true // Access protected MutableLiveData from BaseViewModel

        ReposRepository.getRepos(RepositoryCallBack { isSuccess, response ->
            try {
                _dataLoading.value = false // Access protected MutableLiveData from BaseViewModel
                if (isSuccess && response != null && response.items != null) {
                    _repoList.value = response.items
                    _empty.value = response.items.isEmpty() // Access protected MutableLiveData
                } else {
                    // Handles isSuccess == false OR response is null OR getItems() is null
                    _empty.value = true // Access protected MutableLiveData
                    _toastMessage.value = "Oops something went wrong!" // Access protected MutableLiveData
                }
            } finally {
                EspressoIdlingResource.decrement() // Decrement after network call and processing
            }
        })
    }
}
