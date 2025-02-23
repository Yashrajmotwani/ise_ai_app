package com.ourapp.ise_ai_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap // Google Map instance
    private lateinit var fusedLocationClient: FusedLocationProviderClient // For fetching user location
    private lateinit var geofencingClient: GeofencingClient // For handling geofences
    private val geofenceList = mutableListOf<Geofence>() // List to store geofences
    private val GEOFENCE_RADIUS = 20000f // Radius for geofences in meters
    private val GEOFENCE_REQUEST_CODE = 1002 // Request code for permissions

    // List of IIT locations with their name and website link
    private val iitLocations = listOf(
        LatLng(23.0333, 72.5704) to Pair("IIT Gandhinagar", "http://www.iitgn.ac.in/"),
        LatLng(20.2961, 85.8189) to Pair("IIT Bhubaneswar", "http://www.iitbbs.ac.in/"),
        LatLng(12.9911, 80.2354) to Pair("IIT Madras", "http://www.iitm.ac.in/"),
        LatLng(26.2006, 91.6788) to Pair("IIT Guwahati", "http://www.iitg.ac.in/")
        // Add more IITs as needed
         LatLng(22.7196, 75.8577) to Pair("IIT Indore", "http://www.iiti.ac.in/"),
        LatLng(26.4499, 80.3329) to Pair("IIT Kanpur", "http://www.iitk.ac.in/"),
        LatLng(26.2195, 73.0236) to Pair("IIT Jodhpur", "https://www.iitj.ac.in/"),
        LatLng(22.3026, 87.3101) to Pair("IIT Kharagpur", "http://www.iitkgp.ac.in/"),
        LatLng(17.5027, 78.3005) to Pair("IIT Hyderabad", "http://www.iith.ac.in"),
        LatLng(19.0836, 72.9157) to Pair("IIT Bombay", "http://www.iitb.ac.in/"),
        LatLng(25.6097, 85.1365) to Pair("IIT Patna", "http://www.iitp.ac.in/"),
        LatLng(28.6129, 77.2295) to Pair("IIT Delhi", "http://www.iitd.ac.in/"),
        LatLng(31.3317, 76.6821) to Pair("IIT Ropar", "http://www.iitrpr.ac.in/"),
        LatLng(32.2497, 76.9338) to Pair("IIT Mandi", "http://www.iitmandi.ac.in/"),
        LatLng(29.8500, 78.1636) to Pair("IIT Roorkee", "https://www.iitr.ac.in"),
        LatLng(25.3192, 82.9865) to Pair("IIT BHU", "http://www.iitbhu.ac.in"),
        LatLng(32.5500, 74.8756) to Pair("IIT Jammu", "http://iitjammu.ac.in"),
        LatLng(10.5600, 76.9574) to Pair("IIT Palakkad", "http://iitpkd.ac.in"),
        LatLng(13.7144, 79.5953) to Pair("IIT Tirupati", "http://iittp.ac.in/"),
        LatLng(15.2894, 73.9948) to Pair("IIT Goa", "http://www.iitgoa.ac.in"),
        LatLng(21.9214, 81.7327) to Pair("IIT Bhilai", "https://www.iitbhilai.ac.in/"),
        LatLng(15.3670, 75.0662) to Pair("IIT Dharwad", "http://www.iitdh.ac.in/"),
        LatLng(23.8160, 86.4343) to Pair("IIT Dhanbad", "https://www.iitism.ac.in/"),
        LatLng(12.9716, 77.5946) to Pair("IISc Bangalore", "http://www.iisc.ac.in/")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize the map fragment and set the callback
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        // Check if location permissions are granted
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GEOFENCE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GEOFENCE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                // Show an alert dialog if permission is denied
                AlertDialog.Builder(this)
                    .setMessage("Location permission is required for the app to function.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            setCurrentLocation()
        }

        // Add markers for IITs and setup geofences
        addIITMarkersAndGeofences()
    }

    private fun setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                
                // Add marker for user's current location
                val markerOptions = MarkerOptions()
                    .position(currentLatLng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                mMap.addMarker(markerOptions)
                
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                // Find and notify the nearest IIT
                val nearestIIT = getNearestIITLocation(currentLatLng)
                if (nearestIIT != null) {
                    sendNotification("Hi, you're at ${nearestIIT.second.first}!")
                }
            }
        }
    }

    private fun getNearestIITLocation(currentLatLng: LatLng): Pair<LatLng, Pair<String, String>>? {
        var nearestIIT: Pair<LatLng, Pair<String, String>>? = null
        var minDistance = Float.MAX_VALUE

        // Iterate through IIT locations to find the closest one
        for (iit in iitLocations) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentLatLng.latitude, currentLatLng.longitude,
                iit.first.latitude, iit.first.longitude,
                distance
            )

            // If the distance is less than 4km and is the minimum distance so far, update nearest IIT
            if (distance[0] < 4000f && distance[0] < minDistance) {
                minDistance = distance[0]
                nearestIIT = iit
            }
        }
        return nearestIIT
    }

    private fun addIITMarkersAndGeofences() {
        for (iit in iitLocations) {
            mMap.addMarker(
                MarkerOptions()
                    .position(iit.first)
                    .title(iit.second.first)
                    .snippet(iit.second.second)
            )

            val geofence = Geofence.Builder()
                .setRequestId(iit.second.first)
                .setCircularRegion(iit.first.latitude, iit.first.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            geofenceList.add(geofence)
        }
        addGeofences()
    }

    private fun addGeofences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        val geofencePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this, 0, Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                // Geofences added successfully
            }
            .addOnFailureListener {
                Log.e("Geofence", "Failed to add geofences", it)
            }
    }
}
