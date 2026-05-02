package com.example.restaurantapp2

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.Role.Admin.AdminActivity
import com.example.restaurantapp2.Role.Customer.CustomerActivity
import com.example.restaurantapp2.Utils.SessionManager
import com.example.restaurantapp2.Utils.retryOnce
import com.example.restaurantapp2.repository.UserProfileRepository
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val session = SessionManager(this)

        val role = session.getUserRole()
        val userId = session.getUserId()
        Log.d("role", role.toString())
        Log.d("id logged", userId.toString())
        if (session.isLoggedIn()) {
            Log.d("Session is true", "Session is true")
            lifecycleScope.launch {
                try {
                    val userRepo = UserProfileRepository()
                    val response = retryOnce(times = 3) {
                        userRepo.getUserInfo(userId)
                    }

                    val user = response.data
                    Log.d("Auth debug", "user = [$user]")

                    if (user != null && user.status !="LOCK") {
                        Log.d("Auth debug", "role = [$role]")

                        val intent = when (role) {

                            "ADMIN" -> Intent(this@AuthActivity, AdminActivity::class.java)
                            "CUSTOMER" -> Intent(this@AuthActivity, CustomerActivity::class.java)
                            else -> null
                        }

                        intent?.putExtra("user", user)
                        startActivity(intent)
                        finish()

                    } else {
                        forceLogout(session)
                    }

                } catch (e: Exception) {
                    Log.e("Auth", "getUserInfo failed after retry", e)

                    Toast.makeText(
                        this@AuthActivity,
                        "Network error. Please login again.",
                        Toast.LENGTH_SHORT
                    ).show()

                    forceLogout(session)
                }
            }
            return
        }

        if (!session.isLoggedIn() && savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flAuthContainer, LoginFragment())
                .commit()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

    }
    private fun forceLogout(session : SessionManager) {
        session.clear()

        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}