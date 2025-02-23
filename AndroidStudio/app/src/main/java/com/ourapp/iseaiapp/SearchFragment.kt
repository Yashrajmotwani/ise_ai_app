package com.ourapp.iseaiapp

import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.ourapp.iseaiapp.databinding.FragmentSearchBinding
import com.ourapp.iseaiapp.databinding.FragmentTeacherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

//        // Observe the search results
//        searchViewModel.searchResults.observe(viewLifecycleOwner, Observer { projects ->
//            // Update RecyclerView with search results
//            val adapter = ProjectAdapter(projects) { project ->
//                // Show dialog with project details when an item is clicked
//                showProjectDetailsDialog(project)
//            }
//            binding.recyclerView.adapter = adapter
//        })
//
//        // Listen for search query
//        binding.searchButton.setOnClickListener {
//            val query = binding.searchEditText.text.toString()
//            if (query.isNotEmpty()) {
//                // Call the search function
//                searchViewModel.search(query)
//            } else {
//                Toast.makeText(context, "Please enter a query", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchViewModel.search(query)
            } else {
                Toast.makeText(context, "Please enter a query", Toast.LENGTH_SHORT).show()
            }
        }

// 🔴 Observe errors (empty results or API failures)
        searchViewModel.searchError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showNoResultsDialog()
            }
        }

// 🔵 Observe search results to update RecyclerView
        searchViewModel.searchResults.observe(viewLifecycleOwner) { projects ->
            if (!projects.isNullOrEmpty()) {
                binding.recyclerView.adapter = ProjectAdapter(projects) { project ->
                    showProjectDetailsDialog(project)
                }
            }
        }

        return binding.root
    }

    private fun showNoResultsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("No Results Found")
            .setMessage("No Projects matched. Try searching with different keywords, such as:\n\n" +
                    "• Open or Closed\n" +
                    "• Project\n" +
                    "• College (e.g., 'IIT Tirupati')\n")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showProjectDetailsDialog(project: Project) {

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Create and display the dialog with project details
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_project_details, null)

        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle("Project Details")
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Save") { dialog, _ ->
                userId?.let { userId ->
                    RetrofitClient.api.saveFavorite(userId, project).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Project Saved!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, "Failed to save project", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            .setNegativeButton("Remove") { dialog, _ ->
                userId?.let { userId ->
                    RetrofitClient.api.removeFavorite(userId, project._id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Project Removed!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, "Project not Saved!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            .create()

        // Check if discipline, pi_name is empty and set it to "NA" if so
        val discipline = if (project.discipline.isNullOrEmpty()) "NA" else project.discipline
        val pi_name = if (project.pi_name.isNullOrEmpty()) "NA" else project.pi_name
        val lastdate = if (project.last_date.isNullOrEmpty() || project.last_date == "N/A") "NA" else project.last_date
        val postdate = if (project.posting_date.isNullOrEmpty() || project.posting_date == "N/A") "NA" else project.posting_date
        val projectname = if (project.name_of_post.isNullOrEmpty()) project.discipline else project.name_of_post

        var status = project.status ?: "" // Safe call to handle null

        if (status.isNullOrEmpty() ||
            (!status.equals("Open", ignoreCase = true) && !status.equals("Closed", ignoreCase = true))) {
            // Convert the last_date string to a timestamp for comparison
            val lastDate = project.last_date?.let { parseDate(it) } ?: 0L // Safe call for null last_date

            // Get the current date's timestamp
            val currentDate = System.currentTimeMillis() // Current timestamp

            // Compare last date with current date to determine status
            status = if (lastDate >= currentDate) {
                "Open" // If last date is in the future, status is "Closed"
            } else {
                "Closed" // If last date is in the past or equal to current date, status is "Open"
            }
        }

        if(lastdate == "NA") {
            status = "NA"
        }

        // Set the full details of the project in the dialog
        dialogView.findViewById<TextView>(R.id.projectName).text = "Project Name: ${projectname}"
        dialogView.findViewById<TextView>(R.id.pi_name).text = "PI Name: ${pi_name}"
        dialogView.findViewById<TextView>(R.id.projectStatus).text = "Status: ${status}"
        dialogView.findViewById<TextView>(R.id.projectDiscipline).text = "Discipline: ${discipline}"
        dialogView.findViewById<TextView>(R.id.projectDate).text = "Posting Date: ${postdate}"
        dialogView.findViewById<TextView>(R.id.lastDate).text = "Last Date: ${lastdate}"
        dialogView.findViewById<TextView>(R.id.college).text = "College: ${project.college}"


        // Set the advertisement link (make it clickable)
        val projectLinkTextView = dialogView.findViewById<TextView>(R.id.projectLink)
        projectLinkTextView.text = project.advertisement_link
        projectLinkTextView.maxLines = 1
        projectLinkTextView.ellipsize = TextUtils.TruncateAt.END
        projectLinkTextView.setMovementMethod(LinkMovementMethod.getInstance())


        alertDialog.show()
    }

    fun parseDate(dateString: String): Long {
        // List of possible date formats, including the format "dd-MM-yyyy"
        val formats = listOf(
            "dd-MM-yyyy", // Format you want to parse
            "dd.MM.yyyy",
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "yyyy/MM/dd"
        )

        val locale = Locale.getDefault()

        // Iterate through each format to try parsing
        for (format in formats) {
            val formatter = SimpleDateFormat(format, locale)
            formatter.timeZone = TimeZone.getTimeZone("UTC") // Use UTC for consistency

            try {
                val date = formatter.parse(dateString)
                if (date != null) {
                    println("Successfully parsed $dateString to $date")
                    return date.time // Return time in milliseconds
                }
            } catch (e: Exception) {
                println("Error parsing $dateString with format $format")  // Debugging log
            }
        }

        return 0L  // Return 0L if no valid format works
    }

}