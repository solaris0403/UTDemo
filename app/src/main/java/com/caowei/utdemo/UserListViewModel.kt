package com.caowei.utdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserListViewModel : ViewModel() {
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    public fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    public fun setLoadingLiveData(value: Boolean){
        loadingLiveData.value = value
    }
}