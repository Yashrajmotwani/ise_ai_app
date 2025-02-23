package com.ourapp.iseaiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ourapp.iseaiapp.databinding.ItemProjectBinding

class FavProjectAdapter(
    private val favoriteProjects: List<Project>,
    private val onProjectClick: (Project) -> Unit
) : RecyclerView.Adapter<FavProjectAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteProjects[position])
    }

    override fun getItemCount() = favoriteProjects.size

    inner class FavoriteViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) = with(binding) {
            projectTitle.text = project.name_of_post.takeUnless { it.isNullOrEmpty() } ?: project.discipline.orEmpty()
            discipline.text = "Discipline: ${project.discipline.orEmpty()}"
            lastDate.text = "Last Date: ${project.last_date.takeUnless { it.isNullOrEmpty() || it == "N/A" } ?: "NA"}"
            college.text = "College: ${project.college.orEmpty()}"

            root.setOnClickListener { onProjectClick(project) }
        }
    }
}
