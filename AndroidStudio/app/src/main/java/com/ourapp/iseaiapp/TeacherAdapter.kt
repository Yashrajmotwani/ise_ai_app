package com.ourapp.iseaiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ourapp.iseaiapp.databinding.ItemTeacherBinding
import android.text.TextUtils

class TeacherAdapter(
    private val teachers: List<Teacher>,
    private val onTeacherClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val binding = ItemTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(teachers[position])
    }

    override fun getItemCount() = teachers.size

    inner class TeacherViewHolder(private val binding: ItemTeacherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(teacher: Teacher) {
            binding.apply {
                teacherName.text = teacher.name ?: "NA"
                areaInterest.text = teacher.areas_of_interest?.takeIf { it.isNotBlank() } ?: "NA"
                college.text = "College: ${teacher.college ?: "NA"}"
                emailID.text = "Email: ${teacher.email?.takeIf { it.isNotBlank() } ?: "NA"}"
                department.text = "Dept: ${teacher.department?.takeIf { it.isNotBlank() } ?: "NA"}"

                // Truncate Area of Interest if too long
                areaInterest.maxLines = 1
                areaInterest.ellipsize = TextUtils.TruncateAt.END

                // Load teacher image using Glide
                Glide.with(teacherImage.context)
                    .load(teacher.image_link)
                    .placeholder(R.drawable.baseline_face_24)
                    .error(R.drawable.baseline_face_24)
                    .into(teacherImage)

                // Handle item click
                root.setOnClickListener { onTeacherClick(teacher) }
            }
        }
    }
}
