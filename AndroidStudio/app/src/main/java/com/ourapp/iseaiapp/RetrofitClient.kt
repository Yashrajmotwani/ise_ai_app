package com.ourapp.iseaiapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    val BASE_URL = "https://ise-android-project.onrender.com/"

    val BASE_URL = "http://10.25.84.207:5000/"

    // Create a Retrofit instance
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Define the base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java) // Create the ApiService
    }

}
