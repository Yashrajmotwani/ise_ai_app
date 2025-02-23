package com.ourapp.iseaiapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollegeListActivity : AppCompatActivity() {

    private lateinit var collegeAdapter: CollegeAdapter
    private val collegeList = mutableListOf<College>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_college_list)

        setupRecyclerView()
        fetchColleges()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        collegeAdapter = CollegeAdapter(collegeList)
        recyclerView.adapter = collegeAdapter
    }

    private fun fetchColleges() {
        RetrofitClient.api.getColleges().enqueue(object : Callback<List<College>> {
            override fun onResponse(call: Call<List<College>>, response: Response<List<College>>) {
                if (response.isSuccessful) {
                    response.body()?.let { colleges ->
                        updateCollegeList(colleges)
                    } ?: showToast("Received empty response")
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<College>>, t: Throwable) {
                showToast("Failed to fetch data: ${t.message}")
            }
        })
    }

    private fun updateCollegeList(colleges: List<College>) {
        collegeList.apply {
            clear()
            addAll(colleges)
        }
        collegeAdapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
