package com.ourapp.iseaiapp

import com.google.gson.annotations.SerializedName

data class College(
    val No: String,                // The No. field, e.g., "1"
    val Name: String,              // The name of the college, e.g., "IIT Kharagpur"
    val Abbreviation: String,      // The abbreviation of the college, e.g., "IITKGP"
    val Founded: String,           // The year the college was founded, e.g., "1951"
    @SerializedName("Converted as IIT") val ConvertedAsIIT: String,    // The year the college was converted as IIT, e.g., "1951"
    @SerializedName("State/UT") val stateUT: String,  // The state in which the college is located, e.g., "West Bengal"
    val Website: String,           // URL to the college's website
    val Faculty: String,           // Number of faculty members, e.g., "928"
    val Students: String,          // Number of students, e.g., "15,862"
    val Logo: String               // URL to the college logo image
)
