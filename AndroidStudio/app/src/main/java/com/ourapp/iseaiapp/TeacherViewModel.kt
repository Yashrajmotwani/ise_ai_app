package com.ourapp.iseaiapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    private val _fsearchResults = MutableLiveData<List<Teacher>?>()
    val fsearchResults: LiveData<List<Teacher>?> get() = _fsearchResults

    private val _searchError = MutableLiveData<Boolean>()  // 🔴 Used to track search failures
    val searchError: LiveData<Boolean> get() = _searchError

    fun fsearch(query: String) {
        Log.d("TeacherViewModel", "Searching with query: $query")

        RetrofitClient.api.searchFaculty(query).enqueue(object : Callback<List<Teacher>> {
            override fun onResponse(call: Call<List<Teacher>>, response: Response<List<Teacher>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("TeacherViewModel", "Received ${body?.size ?: 0} results")

                    _fsearchResults.postValue(body ?: emptyList())

                    // 🔵 Reset error state when results are found
                    _searchError.postValue(body.isNullOrEmpty())
                } else {
                    Log.e("TeacherViewModel", "Failed response: ${response.errorBody()?.string()}")
                    _searchError.postValue(true)  // 🔴 Trigger error pop-up
                }
            }

            override fun onFailure(call: Call<List<Teacher>>, t: Throwable) {
                Log.e("TeacherViewModel", "Network request failed: ${t.message}")
                _searchError.postValue(true)  // 🔴 Trigger error pop-up
            }
        })
    }
}
