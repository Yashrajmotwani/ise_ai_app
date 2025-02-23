package com.ourapp.ise_ai_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOut : AppCompatActivity() {

    // Firebase authentication instance
    private lateinit var mAuth: FirebaseAuth

    // Google Sign-In client instance
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Request ID token for Firebase authentication
            .requestEmail() // Request email for sign-in
            .build()

        // Initialize Google Sign-In client with the configured options
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Get reference to the TextView that will show the logged-in user's name
        val textView = findViewById<TextView>(R.id.name)

        // Get current authenticated user
        val auth = Firebase.auth
        val user = auth.currentUser

        // Display logged-in user's name (if available)
        if (user != null) {
            textView.text = "Signed in as: ${user.displayName ?: "Unknown"}"
        } else {
            textView.text = "No user signed in"
        }

        // Reference to the logout button
        val sign_out_button = findViewById<Button>(R.id.logout_button)

        // Set click listener on logout button
        sign_out_button.setOnClickListener {
            signOutAndStartSignInActivity() // Call function to sign out user
        }
    }

    /**
     * Signs out the user from Firebase and Google Sign-In,
     * then redirects them to the SignInActivity.
     */
    private fun signOutAndStartSignInActivity() {
        // Sign out from Firebase authentication
        mAuth.signOut()

        // Sign out from Google Sign-In and handle the result
        mGoogleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("SignOut", "User signed out successfully") // Debugging log
            } else {
                Log.e("SignOut", "Sign out failed", task.exception) // Log error if sign-out fails
            }

            // Redirect user to sign-in screen after logging out
            val intent = Intent(this@SignOut, SignInActivity::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent going back to SignOut screen
        }
    }
}
