package com.example.restaurantapp2.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.restaurantapp2.models.DeviceTokenRequest
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.example.restaurantapp2.repository.DeviceTokenRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthManager (private val context: Context) {
    private val authRepo = AuthRepository()
    private val tokenRepo = DeviceTokenRepository()

    suspend fun loginAsLocal(username: String, password: String): UserProfile? {
        val response = authRepo.login(
            mapOf(
                "username" to username,
                "password" to password
            )
        )
        if (response.errorMessage != null) {
            Toast.makeText(context, "Error: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
            return null
        }

        val user = response.data as? UserProfile ?: return null

        // save login state (SharedPreferences)
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("userId", user.userId).apply()

        // register FCM token
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            val tokenRequest = DeviceTokenRequest(user.userId, fcmToken)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    tokenRepo.registerToken(tokenRequest)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed: ${e.message}")
                }
            }
        }

        return user
    }
    suspend fun loginWithGoogle(idToken: String): UserProfile? {
        val response = authRepo.googleAuth(
            mapOf("idToken" to idToken)
        )

        if (response.errorMessage != null) {
            Toast.makeText(context, "Error: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
            return null
        }
        Log.d("GOOGLE_API", "Response = $response")

        val user = response.data as? UserProfile ?: return null

        // save login state
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("userId", user.userId).apply()

        // register FCM
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            val tokenRequest = DeviceTokenRequest(user.userId, fcmToken)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    tokenRepo.registerToken(tokenRequest)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed: ${e.message}")
                }
            }
        }

        return user
    }
}