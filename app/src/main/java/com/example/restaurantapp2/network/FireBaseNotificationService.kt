package com.example.restaurantapp2.network

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restaurantapp2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseNotificationService: FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        Log.e("FCM", "🔥 Service created")
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //TODO: update db or sth
        Log.d("FCM", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {

//        val title = message.notification?.title ?: message.data["title"] ?: "Title"
//        val body = message.notification?.body ?: message.data["body"] ?: "Body"

        val intent = Intent("FCM_EVENT").apply {
            putExtra("title", message.data["title"])
            putExtra("body", message.data["body"])
            putExtra("type", message.data["type"])
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        Log.d("FCM", "Message received: ${message.data}")

        Log.d("FCM", "Message received: ${message.data}")
    }
}