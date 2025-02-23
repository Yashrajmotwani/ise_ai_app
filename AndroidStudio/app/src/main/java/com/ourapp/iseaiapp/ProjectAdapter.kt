package com.ourapp.iseaiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ourapp.iseaiapp.databinding.ItemProjectBinding

class ProjectAdapter(
    private val projects: List<Project>,
    private val onProjectClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size

    inner class ProjectViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) = with(binding) {
            projectTitle.text = project.name_of_post.takeUnless { it.isNullOrEmpty() } ?: project.discipline
            discipline.text = "Discipline: ${project.discipline.takeUnless { it.isNullOrEmpty() } ?: "NA"}"
            lastDate.text = "Last Date: ${project.last_date.takeUnless { it.isNullOrEmpty() || it == "N/A" } ?: "NA"}"
            college.text = "College: ${project.college}"

            root.setOnClickListener { onProjectClick(project) }
        }
    }
}
