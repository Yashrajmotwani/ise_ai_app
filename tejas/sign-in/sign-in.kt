package com.ourapp.ise_ai_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {

    // Companion object to store a constant request code for Google Sign-In
    companion object {
        private const val RC_SIGN_IN = 9001  // Unique request code for Google Sign-In
    }

    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in) // Set the UI layout for the sign-in screen

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()

        // Check if the user is already signed in
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // If a user is already signed in, directly navigate to the main screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the SignInActivity so the user can't navigate back to it
        }

        // Find the sign-in button in the layout
        val signInButton = findViewById<Button>(R.id.signInButton)
        
        // Set a click listener for the sign-in button
        signInButton.setOnClickListener {
            signIn() // Call the signIn() function when the button is clicked
        }
    }

    /**
     * Initiates the Google Sign-In process.
     */
    private fun signIn() {
        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Request an ID token for authentication
            .requestEmail() // Request user's email
            .build()

        // Create a Google Sign-In client with the specified options
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Start the Google Sign-In intent
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) // Launch Google Sign-In activity
    }

    /**
     * Handles the result of the Google Sign-In intent.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // Retrieve the sign-in task result
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Get the Google Sign-In account
                val account = task.getResult(ApiException::class.java)

                // Authenticate with Firebase using the Google account's ID token
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Display an error message if sign-in fails
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Authenticates the user with Firebase using the Google ID token.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Create Firebase credential using Google ID token
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Sign in with the Firebase credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If authentication is successful, get the signed-in user
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    // Navigate to the main activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Close the sign-in activity so the user can't go back to it
                } else {
                    // Show an error message if authentication fails
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
