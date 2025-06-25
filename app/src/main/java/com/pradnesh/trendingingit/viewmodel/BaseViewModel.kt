package com.pradnesh.trendingingit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() { // 'open' to allow inheritance

    // Backing property for empty state (MutableLiveData)
    private val _empty = MutableLiveData<Boolean>(true)
    // Public LiveData for observing empty state
    val empty: LiveData<Boolean> = _empty

    // Backing property for dataLoading state (MutableLiveData)
    private val _dataLoading = MutableLiveData<Boolean>(false)
    // Public LiveData for observing dataLoading state
    val dataLoading: LiveData<Boolean> = _dataLoading

    // Backing property for toast messages (MutableLiveData)
    private val _toastMessage = MutableLiveData<String>()
    // Public LiveData for observing toast messages
    val toastMessage: LiveData<String> = _toastMessage

    // Protected setters for child classes to modify these values if needed
    // Or, if modification is only internal to BaseViewModel or via specific methods,
    // these setters might not be needed, or could be internal methods.
    // For now, let's assume child classes might need to set these directly for simplicity,
    // matching the Java version's direct field access (though Java had package-private access).
    // If direct mutation from subclasses is desired:
    // protected fun setEmpty(value: Boolean) { _empty.value = value }
    // protected fun setDataLoading(value: Boolean) { _dataLoading.value = value }
    // protected fun setToastMessage(message: String) { _toastMessage.value = message }
    // However, the Java version used package-private fields, which RepoListViewModel directly set.
    // In Kotlin, to replicate this, we can make the MutableLiveData instances 'internal' or 'protected'.
    // Let's make them protected for now, so RepoListViewModel can access them.

    protected val _protectedEmpty = _empty // Expose MutableLiveData to subclasses if needed
    protected val _protectedDataLoading = _dataLoading
    protected val _protectedToastMessage = _toastMessage
}

// Re-evaluating the protected mutable live data exposure.
// The Java version had package-private fields. RepoListViewModel directly accessed `dataLoading.setValue(true)`.
// To allow similar access in Kotlin from a subclass in the same package:
// Option 1: Make MutableLiveData fields `internal`. (Kotlin's package-private equivalent)
// Option 2: Keep them private and provide protected methods to update them.
// Option 3: Expose the MutableLiveData directly as protected (less encapsulated, but mirrors Java access).

// Let's go with exposing MutableLiveData as protected for direct access from RepoListViewModel,
// as it aligns with the original Java structure's accessibility.
// This means RepoListViewModel will access e.g. `_dataLoading.value = ...`

package com.pradnesh.trendingingit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    protected val _empty = MutableLiveData<Boolean>(true)
    val empty: LiveData<Boolean> = _empty

    protected val _dataLoading = MutableLiveData<Boolean>(false)
    val dataLoading: LiveData<Boolean> = _dataLoading

    // For toastMessage, it's likely set by subclasses when an error occurs.
    // It doesn't have an initial value in the Java version, so initialize as MutableLiveData<String>()
    protected val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage
}
