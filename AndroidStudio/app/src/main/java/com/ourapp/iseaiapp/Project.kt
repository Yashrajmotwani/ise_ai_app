package com.ourapp.iseaiapp

data class Project(
    val _id: String,
    val name_of_post: String,
    val discipline: String,
    val posting_date: String,
    val last_date: String,
    val pi_name: String,
    val advertisement_link: String,
    val status: String,
    val college: String,
    val department: String,
    var isSaved: Boolean = false
)