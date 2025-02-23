package com.ourapp.ise_ai_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Suppressing the default SplashScreen warning because we're using a custom implementation
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting the splash screen layout
        setContentView(R.layout.splash_screen)

        // Enable full-screen edge-to-edge display for a modern UI experience
        enableEdgeToEdge()

        // Adjust window insets to ensure UI elements are properly positioned on the screen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Get system bar insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Apply padding
            insets // Return the modified insets
        }

        // Create a delay before transitioning to the next activity (SignInActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an intent to switch from SplashScreen to SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent) // Start the SignInActivity
            finish() // Close the SplashScreen so the user can't return to it using the back button
        }, 3000) // 3000 milliseconds (3 seconds) delay before transitioning
    }
}
