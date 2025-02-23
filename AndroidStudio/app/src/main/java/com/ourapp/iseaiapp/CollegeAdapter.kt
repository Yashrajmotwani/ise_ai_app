package com.ourapp.iseaiapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CollegeAdapter(private val collegeList: List<College>) : RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder>() {

    inner class CollegeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val collegeLogo: ImageView = itemView.findViewById(R.id.collegeLogo)
        private val collegeName: TextView = itemView.findViewById(R.id.collegeName)
        private val founded: TextView = itemView.findViewById(R.id.founded)
        private val numFaculty: TextView = itemView.findViewById(R.id.num_faculty)
        private val numStudents: TextView = itemView.findViewById(R.id.num_students)
        private val stateLocation: TextView = itemView.findViewById(R.id.state)
        private val collegeCard: CardView = itemView.findViewById(R.id.college_card)

        fun bind(college: College) {
            collegeName.text = college.Name
            founded.text = "Founded in: ${college.Founded}"
            stateLocation.text = "State: ${college.stateUT}"
            numFaculty.text = "Number of Faculty: ${college.Faculty}"
            numStudents.text = "Number of Students: ${college.Students}"

            // Load college logo using Glide
            Glide.with(itemView.context)
                .load(college.Logo)
                .into(collegeLogo)

            // Set click listener to open college website
            collegeCard.setOnClickListener { openCollegeWebsite(college.Website) }
        }

        private fun openCollegeWebsite(website: String) {
            if (website.isNotEmpty()) {
                val url = if (!website.startsWith("http://") && !website.startsWith("https://")) {
                    "https://$website"
                } else {
                    website
                }
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CollegeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollegeViewHolder, position: Int) {
        holder.bind(collegeList[position])
    }

    override fun getItemCount(): Int = collegeList.size
}
