package com.example.restaurantapp2.Role.Admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Role.AccountFragment
import com.example.restaurantapp2.Utils.SessionManager
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.UserProfileRepository
import com.example.restaurantapp2.viewmodels.OrderVM
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {
    private val userVM: UserVM by viewModels()
    private val orderVM : OrderVM by viewModels()

    private val fcmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("fcmReceive", "onReceive called")

            val title = intent.getStringExtra("title")
            val body = intent.getStringExtra("body")
            val type = intent.getStringExtra("type")


            if (type == "NEW_ORDER") {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "$title: $body",
                    Snackbar.LENGTH_LONG
                ).show()

                // Optional: refresh orders here
                //TODO: add logic later
                Log.d("FCM_DEBUG", "userVM.user.value = ${userVM.user.value}")
                Log.d("FCM_DEBUG", "userId = ${userVM.user.value?.userId}")
                val userId = userVM.user.value?.userId ?: return
                orderVM.loadAllOrder()

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("INSETS", "Admin fitsSystemWindows=${window.decorView.fitsSystemWindows}")



        val intentUser = intent.getParcelableExtra<UserProfile>("user")
        Log.d("userVM", intentUser?.userName.toString())

        if (intentUser != null) {
            userVM.setUser(intentUser)
            initUI()
            return
        }

        val session = SessionManager(this)

        if (!session.isLoggedIn()) {
            finish()
            return
        }

        lifecycleScope.launch {
            val repo = UserProfileRepository()
            val response = repo.getUserInfo(session.getUserId())

            val user = response.data

            if (user == null) {
                session.clear()
                finish()
                return@launch
            }

            userVM.setUser(user)
            initUI()   //only start UI AFTER user is ready
        }
    }
    private fun initUI() {
        val frgProdLs = ProductListFragment()
        val frgOrderLs = OrderListFragment()
        val frgSetting = SettingFragment()
        val frgUserLs = UserListFragment()
        val frgAccount = AccountFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView);


        setCurrentFragment(frgProdLs);

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.mi_product_list -> setCurrentFragment(frgProdLs)
                R.id.mi_user_list -> setCurrentFragment(frgUserLs)

                R.id.mi_order_list -> setCurrentFragment(frgOrderLs)
                R.id.mi_account_info -> setCurrentFragment(frgAccount)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }


    public fun hideBottomNavBar(){
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = BottomNavigationView.GONE
    }

    public fun showBottomNavBar(){
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = BottomNavigationView.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(fcmReceiver, IntentFilter("FCM_EVENT"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(fcmReceiver)
    }
}