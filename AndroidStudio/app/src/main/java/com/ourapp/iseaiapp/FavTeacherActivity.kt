package com.ourapp.iseaiapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.ourapp.iseaiapp.databinding.ActivityFavteacherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavteacherBinding
    private lateinit var favoriteAdapter: FavTeacherAdapter
    private val favoriteTeacher = mutableListOf<Teacher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavteacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewFavTeachers.layoutManager = LinearLayoutManager(this)

        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            fetchFavoriteTeacher(userId)
        }

        favoriteAdapter = FavTeacherAdapter(favoriteTeacher, ::showTeacherDetailsDialog)
        binding.recyclerViewFavTeachers.adapter = favoriteAdapter
    }

    private fun fetchFavoriteTeacher(userId: String) {
        RetrofitClient.api.getFavoriteTeacher(userId).enqueue(object : Callback<List<Teacher>> {
            override fun onResponse(call: Call<List<Teacher>>, response: Response<List<Teacher>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        favoriteTeacher.apply {
                            clear()
                            addAll(it)
                        }
                        favoriteAdapter.notifyDataSetChanged()
                    } ?: showToast("No Favorites...")
                } else {
                    showToast("No Favorites...")
                }
            }

            override fun onFailure(call: Call<List<Teacher>>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showTeacherDetailsDialog(teacher: Teacher) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_teacher_details, null)

        val alertDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setTitle("Teacher Details")
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Remove") { dialog, _ ->
                removeFavoriteTeacher(userId, teacher._id, dialog)
            }
            .create()

        with(dialogView) {
            findViewById<ImageView>(R.id.teacherImage).apply {
                Glide.with(context)
                    .load(teacher.image_link)
                    .placeholder(R.drawable.baseline_face_24)
                    .error(R.drawable.baseline_face_24)
                    .into(this)
            }

            setTextWithPrefix(R.id.teacherName, "Faculty Name: ", teacher.name)
            setTextWithPrefix(R.id.position, "Position: ", teacher.position)
            setTextWithPrefix(R.id.qualification, "Qualification: ", teacher.qualification)
            setTextWithPrefix(R.id.areaInterest, "Areas of Interest: ", teacher.areas_of_interest)
            setTextWithPrefix(R.id.college, "College: ", teacher.college)
            setTextWithPrefix(R.id.department, "Department: ", teacher.department)

            setupPhoneTextView(findViewById(R.id.phone), teacher.phone)
            setupEmailTextView(findViewById(R.id.emailID), teacher.email)
        }

        alertDialog.show()
    }

    private fun removeFavoriteTeacher(userId: String, teacherId: String, dialog: android.content.DialogInterface) {
        RetrofitClient.api.removeFavoriteTeacher(userId, teacherId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                showToast(if (response.isSuccessful) "Teacher Profile Removed!" else "Teacher not Saved!")
                if (response.isSuccessful) restartActivity()
                dialog.dismiss()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun setupPhoneTextView(phoneTextView: TextView, phone: String?) {
        phoneTextView.apply {
            text = "Phone: ${phone.takeUnless { it.isNullOrEmpty() || it == "N/A" } ?: "NA"}"
            isClickable = phone?.isNotEmpty() == true && phone != "N/A"
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark).takeIf { isClickable } ?: ContextCompat.getColor(context, android.R.color.black))

            setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            }
        }
    }

    private fun setupEmailTextView(emailTextView: TextView, email: String?) {
        emailTextView.apply {
            text = "Email: ${email.takeUnless { it.isNullOrEmpty() || it == "N/A" } ?: "NA"}"
            isClickable = email?.isNotEmpty() == true && email != "N/A"
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark).takeIf { isClickable } ?: ContextCompat.getColor(context, android.R.color.black))
            movementMethod = LinkMovementMethod.getInstance().takeIf { isClickable }

            setOnClickListener {
                if (isValidEmail(email)) {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
                        putExtra(Intent.EXTRA_SUBJECT, "Re: Your work in <Add Area of Interest>")
                    }
                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                } else {
                    showToast("Invalid email address")
                }
            }
        }
    }

    private fun isValidEmail(email: String?) = email?.matches(Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) ?: false

    private fun restartActivity() {
        finish()
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun android.view.View.setTextWithPrefix(viewId: Int, prefix: String, value: String?) {
        findViewById<TextView>(viewId).text = "$prefix${value.orEmpty()}"
    }
}
