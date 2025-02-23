package com.ourapp.iseaiapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.ourapp.iseaiapp.databinding.FragmentTeacherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherFragment : Fragment() {

    private lateinit var teacherViewModel: TeacherViewModel
    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTeacherBinding.inflate(inflater, container, false)
        teacherViewModel = ViewModelProvider(this).get(TeacherViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                teacherViewModel.fsearch(query)
            } else {
                showToast("Please enter a search term")
            }
        }

        // 🔴 Observe only failures for pop-ups
        teacherViewModel.searchError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showNoResultsDialog()
            }
        }

        // 🔵 Observe search results to update RecyclerView
        teacherViewModel.fsearchResults.observe(viewLifecycleOwner) { teachers ->
            if (!teachers.isNullOrEmpty()) {
                binding.recyclerView.adapter = TeacherAdapter(teachers, ::showTeacherDetailsDialog)
            }
        }

        return binding.root
    }

    // Function to show No Results popup
    private fun showNoResultsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("No Results Found")
            .setMessage("Try searching with different keywords, such as:\n\n" +
                    "• Specific research area (e.g., 'Machine Learning')\n" +
                    "• Faculty name (e.g., 'Sridhar Chimalakonda')\n" +
                    "• Professor or Associate\n" +
                    "• College (e.g., 'IIT Tirupati')\n")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showTeacherDetailsDialog(teacher: Teacher) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_teacher_details, null)

        with(dialogView) {
            findViewById<TextView>(R.id.teacherName).text = "Faculty Name: ${teacher.name}"
            findViewById<TextView>(R.id.position).text = "Position: ${teacher.position}"
            findViewById<TextView>(R.id.qualification).text = "Qualification: ${teacher.qualification}"
            findViewById<TextView>(R.id.areaInterest).text = "Areas of Interest: ${teacher.areas_of_interest}"
            findViewById<TextView>(R.id.college).text = "College: ${teacher.college}"
            findViewById<TextView>(R.id.department).text = "Department: ${teacher.department}"

            // Load teacher image
            findViewById<ImageView>(R.id.teacherImage).let { imageView ->
                Glide.with(imageView.context)
                    .load(teacher.image_link)
                    .placeholder(R.drawable.baseline_face_24)
                    .error(R.drawable.baseline_face_24)
                    .into(imageView)
            }

            setupClickableText(findViewById(R.id.phone), teacher.phone, "Phone") { phone ->
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            }

            setupClickableText(findViewById(R.id.emailID), teacher.email, "Email") { email ->
                if (email.isValidEmail()) {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Re: Your work in <Add Area of Interest>")
                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                } else {
                    showToast("Invalid email address")
                }
            }
        }

        // Show AlertDialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle("Faculty Details")
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .apply {
                userId?.let { id ->
                    setNeutralButton("Save") { dialog, _ -> saveTeacher(id, teacher, dialog) }
                    setNegativeButton("Remove") { dialog, _ -> removeTeacher(id, teacher._id, dialog) }
                }
            }
            .show()
    }

    private fun setupClickableText(textView: TextView, value: String?, label: String, onClick: (String) -> Unit) {
        val text = value?.takeIf { it.isNotEmpty() && it != "N/A" } ?: "NA"
        textView.text = "$label: $text"
        textView.isClickable = text != "NA"

        if (textView.isClickable) {
            context?.let { textView.setTextColor(ContextCompat.getColor(it, android.R.color.holo_blue_dark)) }
            textView.setMovementMethod(LinkMovementMethod.getInstance())
            textView.setOnClickListener { onClick(text) }
        }
    }

    private fun saveTeacher(userId: String, teacher: Teacher, dialog: android.content.DialogInterface) {
        RetrofitClient.api.saveFavoriteTeacher(userId, teacher).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                showToast(if (response.isSuccessful) "Teacher Profile Saved!" else "Failed to save Teacher Profile")
                dialog.dismiss()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun removeTeacher(userId: String, teacherId: String, dialog: android.content.DialogInterface) {
        RetrofitClient.api.removeFavoriteTeacher(userId, teacherId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                showToast(if (response.isSuccessful) "Teacher Profile Removed!" else "Teacher not Saved!")
                dialog.dismiss()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun String.isValidEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
        return matches(Regex(emailPattern))
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
