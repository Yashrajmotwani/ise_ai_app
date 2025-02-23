package com.ourapp.iseaiapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

// BroadcastReceiver to handle geofence events such as entering a specific location
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Extract geofencing event from the received intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Check if the event contains an error
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e("GeofenceReceiver", "Error: $errorMessage")
                return
            }
        }

        // Retrieve the type of geofence transition (ENTER, EXIT, or DWELL)
        val transition = geofencingEvent?.geofenceTransition
        Log.d("GeofenceReceiver", "Geofence transition: $transition")

        // Check if the user has entered a geofence region
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the list of geofences that triggered the event
            val triggeredGeofences = geofencingEvent.triggeringGeofences
            if (triggeredGeofences != null) {
                for (geofence in triggeredGeofences) {
                    val iitName = geofence.requestId // Unique ID of the geofence
                    Log.d("GeofenceReceiver", "Entered geofence: $iitName")
                    sendNotification(context, iitName) // Send a notification to the user
                }
            }
        }
    }

    /**
     * Sends a notification to the user when they enter a geofenced area.
     * @param context - Application context
     * @param iitName - The name/ID of the IIT geofence the user entered
     */
    private fun sendNotification(context: Context, iitName: String) {
        // Create an intent to open the MapActivity when the notification is clicked
        val notificationIntent = Intent(context, MapActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Get the NotificationManager system service
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "geofence_channel"

        // Create a notification channel (Required for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification with a title, content text, and an icon
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Geofence Alert")
            .setContentText("You have entered the area of $iitName.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss the notification when clicked
            .build()

        // Show the notification
        notificationManager.notify(0, notification)
    }
}