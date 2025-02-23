package com.ourapp.iseaiapp

import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.ourapp.iseaiapp.databinding.ActivityFavprojectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FavProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavprojectBinding
    private lateinit var favoriteAdapter: FavProjectAdapter
    private val favoriteProjects: MutableList<Project> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavprojectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchFavoriteProjects()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)
        favoriteAdapter = FavProjectAdapter(favoriteProjects) { project -> showProjectDetailsDialog(project) }
        binding.recyclerViewFavorites.adapter = favoriteAdapter
    }

    private fun fetchFavoriteProjects() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            RetrofitClient.api.getFavoriteProjects(userId).enqueue(object : Callback<List<Project>> {
                override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updateFavoriteProjects(it) }
                    } else {
                        showToast("No Favorites...")
                    }
                }

                override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                    showToast("Error: ${t.message}")
                }
            })
        }
    }

    private fun updateFavoriteProjects(projects: List<Project>) {
        favoriteProjects.apply {
            clear()
            addAll(projects)
        }
        favoriteAdapter.notifyDataSetChanged()
    }

    private fun showProjectDetailsDialog(project: Project) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_project_details, null)

        val status = determineProjectStatus(project)
        val projectName = project.name_of_post.takeUnless { it.isNullOrEmpty() } ?: project.discipline

        dialogView.findViewById<TextView>(R.id.projectName).text = "Project Name: $projectName"
        dialogView.findViewById<TextView>(R.id.pi_name).text = "PI Name: ${project.pi_name ?: "NA"}"
        dialogView.findViewById<TextView>(R.id.projectStatus).text = "Status: $status"
        dialogView.findViewById<TextView>(R.id.projectDiscipline).text = "Discipline: ${project.discipline ?: "NA"}"
        dialogView.findViewById<TextView>(R.id.projectDate).text = "Posting Date: ${project.posting_date ?: "NA"}"
        dialogView.findViewById<TextView>(R.id.lastDate).text = "Last Date: ${project.last_date ?: "NA"}"
        dialogView.findViewById<TextView>(R.id.college).text = "College: ${project.college ?: "NA"}"

        val projectLinkTextView = dialogView.findViewById<TextView>(R.id.projectLink)
        projectLinkTextView.apply {
            text = project.advertisement_link
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            movementMethod = LinkMovementMethod.getInstance()
        }

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setTitle("Project Details")
            .setPositiveButton("Close", null)
            .setNegativeButton("Remove") { _, _ -> removeProject(userId, project) }
            .show()
    }

    private fun determineProjectStatus(project: Project): String {
        if (project.status.equals("Open", ignoreCase = true) || project.status.equals("Closed", ignoreCase = true)) {
            return project.status
        }

        val lastDateMillis = project.last_date?.let { parseDate(it) } ?: return "NA"
        val currentMillis = System.currentTimeMillis()

        return if (lastDateMillis >= currentMillis) "Open" else "Closed"
    }

    private fun removeProject(userId: String, project: Project) {
        RetrofitClient.api.removeFavorite(userId, project._id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    favoriteProjects.remove(project)
                    favoriteAdapter.notifyDataSetChanged()
                    showToast("Project Removed!")
                } else {
                    showToast("Failed to remove project!")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun parseDate(dateString: String): Long {
        val formats = listOf("dd-MM-yyyy", "dd.MM.yyyy", "yyyy-MM-dd", "MM/dd/yyyy", "yyyy/MM/dd")
        val locale = Locale.getDefault()

        for (format in formats) {
            try {
                val formatter = SimpleDateFormat(format, locale).apply { timeZone = TimeZone.getTimeZone("UTC") }
                return formatter.parse(dateString)?.time ?: continue
            } catch (_: Exception) { }
        }
        return 0L
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
