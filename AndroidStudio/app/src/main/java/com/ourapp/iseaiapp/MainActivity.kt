package com.ourapp.iseaiapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var backPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        // Load the default fragment only if savedInstanceState is null (prevents reloading on rotation)
        if (savedInstanceState == null) {
            replaceFragment(SearchFragment())
            bottomNavigationView.selectedItemId = R.id.nav_positions
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
            // Handle item selection
            when (menuItem.itemId) {
                R.id.nav_positions -> {
                    // Replace the content with the Home Fragment
                    replaceFragment(SearchFragment())
                    highlightMenuItem(menuItem)
                    true
                }
                R.id.nav_teachers -> {
                    // Replace the content with the Search Fragment
                    replaceFragment(TeacherFragment())
                    highlightMenuItem(menuItem)
                    true
                }

                else -> false
            }
        }

        // Set the default selected item
        bottomNavigationView.selectedItemId = R.id.nav_positions


        fab.setOnClickListener {
            showBottomSheet()
        }

    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val optionMap: Map<Int, Class<*>> = mapOf(
            R.id.view_map_option to MapActivity::class.java,
            R.id.saved_project_option to FavProjectActivity::class.java,
            R.id.saved_teacher_option to FavTeacherActivity::class.java,
            R.id.view_college_list_option to CollegeListActivity::class.java,
            R.id.signout to SignOut::class.java
        )


        optionMap.forEach { (viewId, activityClass) ->
            bottomSheetView.findViewById<View>(viewId).setOnClickListener {
                val intent = Intent(this, activityClass)
                if (activityClass == SignOut::class.java) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    private fun highlightMenuItem(item: MenuItem) {
        val menuView: View = bottomNavigationView.findViewById(item.itemId)
        val scaleX = ObjectAnimator.ofFloat(menuView, "scaleX", 1.2f)
        val scaleY = ObjectAnimator.ofFloat(menuView, "scaleY", 1.2f)

        scaleX.duration = 150
        scaleY.duration = 150

        scaleX.start()
        scaleY.start()

        // Optionally, reset the scale after a short delay
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(menuView, "scaleX", 1f).start()
            ObjectAnimator.ofFloat(menuView, "scaleY", 1f).start()
        }, 200)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)

        if (fragment !is SearchFragment) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    // Override onBackPressed to handle back button logic
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        bottomNavigationView.selectedItemId = R.id.nav_positions
        highlightMenuItem(bottomNavigationView.menu.findItem(R.id.nav_positions))

        if (currentFragment is SearchFragment) {
            if (backPressedOnce) {
                super.onBackPressed() // Exit the app
                bottomNavigationView.selectedItemId = R.id.nav_positions
                highlightMenuItem(bottomNavigationView.menu.findItem(R.id.nav_positions))

            } else {
                backPressedOnce = true
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce = false }, 2000)
            }
        } else {
            // Navigate back if there are fragments in the back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                // If no fragments in the back stack, replace with SearchFragment
                replaceFragment(SearchFragment())

                // Explicitly set the selected item and trigger the listener
                bottomNavigationView.selectedItemId = R.id.nav_positions

                // Manually call the onItemSelectedListener to trigger the highlight animation
                bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
                    // Call the same logic as before
                    if (menuItem.itemId == R.id.nav_positions) {
                        highlightMenuItem(menuItem)
                    }
                    true
                }
            }
        }
    }



}