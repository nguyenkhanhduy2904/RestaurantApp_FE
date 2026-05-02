package com.example.restaurantapp2.Utils

import android.content.Context
import com.example.restaurantapp2.models.UserProfile

class SessionManager(context: Context)  {
    private val prefs = context.getSharedPreferences("authPrefs", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, userRole : String, loginMethod: String) {
        prefs.edit().apply {
            putInt("userId", userId)
            putString("userRole", userRole)
            putString("loginMethod", loginMethod)
            apply()
        }
    }

    fun getUserId(): Int {
        return prefs.getInt("userId", -1)
    }

    fun getUserRole(): String? {
        return prefs.getString("userRole", null)
    }
    fun getLoginMethod() : String?{
        return prefs.getString("loginMethod", null)
    }
    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

}