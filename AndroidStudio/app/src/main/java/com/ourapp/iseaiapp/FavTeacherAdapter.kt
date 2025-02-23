package com.ourapp.iseaiapp

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ourapp.iseaiapp.databinding.ItemTeacherBinding

class FavTeacherAdapter(
    private val favoriteTeachers: List<Teacher>,
    private val onTeacherClick: (Teacher) -> Unit
) : RecyclerView.Adapter<FavTeacherAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteTeachers[position])
    }

    override fun getItemCount() = favoriteTeachers.size

    inner class FavoriteViewHolder(private val binding: ItemTeacherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(teacher: Teacher) = with(binding) {
            teacherName.text = teacher.name
            areaInterest.text = teacher.areas_of_interest
            college.text = "College: ${teacher.college.orEmpty()}"
            emailID.text = "Email: ${teacher.email.takeUnless { it.isNullOrEmpty() } ?: "NA"}"
            department.text = "Dept: ${teacher.department.takeUnless { it.isNullOrEmpty() } ?: "NA"}"

            // Truncate Area of Interest if too long
            areaInterest.apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }

            // Load teacher's image using Glide
            Glide.with(teacherImage.context)
                .load(teacher.image_link)
                .placeholder(R.drawable.baseline_face_24)
                .error(R.drawable.baseline_face_24)
                .into(teacherImage)

            root.setOnClickListener { onTeacherClick(teacher) }
        }
    }
}
