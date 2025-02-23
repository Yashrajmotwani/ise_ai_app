package com.ourapp.iseaiapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchResults = MutableLiveData<List<Project>>()
    val searchResults: LiveData<List<Project>> get() = _searchResults

    private val _searchError = MutableLiveData<Boolean>()
    val searchError: LiveData<Boolean> get() = _searchError

    fun search(query: String) {
        Log.d("SearchViewModel", "Searching with query: $query")

        RetrofitClient.api.searchProjects(query).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful) {
                    val results = response.body()
                    if (results.isNullOrEmpty()) {
                        _searchError.postValue(true) // No results found
                    } else {
                        _searchResults.postValue(results) // Successfully received results
                        _searchError.postValue(false) // Reset error state
                    }
                } else {
                    Log.e("SearchViewModel", "Failed to get response: ${response.message()}")
                    _searchError.postValue(true) // API failure
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Log.e("SearchViewModel", "Request failed: ${t.message}")
                _searchError.postValue(true) // Network failure
            }
        })
    }
}
