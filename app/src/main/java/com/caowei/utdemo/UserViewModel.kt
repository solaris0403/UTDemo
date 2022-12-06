package com.caowei.utdemo

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val userLiveData: MutableLiveData<String> = MutableLiveData()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    public fun getUserData() {
        loadingLiveData.value = true
        val task = MyAsyncTask()
        task.execute()
    }

    public fun getUserLiveData(): LiveData<String> {
        return userLiveData
    }

    public fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    @SuppressLint("StaticFieldLeak")
    inner class MyAsyncTask : AsyncTask<Void, Void, String>() {
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            loadingLiveData.value = false
            userLiveData.value = result;//抛出用户信息
        }

        override fun doInBackground(vararg params: Void?): String {
            Thread.sleep(2000)
            return "我是胡飞洋，公众号名字也是胡飞洋，欢迎关注~"
        }
    }
}