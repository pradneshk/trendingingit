package com.pradnesh.trendingingit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {

    MutableLiveData<Boolean> empty = new MutableLiveData<>(true);
    MutableLiveData<Boolean> dataLoading = new MutableLiveData<>(false);
    MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public LiveData<Boolean> getEmpty() {
        return empty;
    }

    public LiveData<Boolean> getDataLoading() {
        return dataLoading;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }
}
