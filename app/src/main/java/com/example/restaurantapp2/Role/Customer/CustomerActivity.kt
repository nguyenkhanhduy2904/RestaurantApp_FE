package com.example.restaurantapp2.Role.Customer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Role.AccountFragment
import com.example.restaurantapp2.Utils.SessionManager
import com.example.restaurantapp2.data.AppDatabase
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.example.restaurantapp2.repository.CartRepository
import com.example.restaurantapp2.repository.OrderRepository
import com.example.restaurantapp2.repository.UserProfileRepository
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.CartVMFactory
import com.example.restaurantapp2.viewmodels.OrderVM
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CustomerActivity : AppCompatActivity() {
    private val userVM: UserVM by viewModels()
    private val orderVM: OrderVM by viewModels()

    private val fcmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("fcmReceive", "onReceive called")

            val title = intent.getStringExtra("title")
            val body = intent.getStringExtra("body")
            val type = intent.getStringExtra("type")

//            Snackbar.make(
//                findViewById(android.R.id.content),
//                "$title: $body",
//                Snackbar.LENGTH_LONG
//            ).show()

            if (type == "ORDER_UPDATED") {
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
                orderVM.loadAllOrderByUserId(userId)

            }
        }
    }

    val cartVM: CartVM by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val repo = CartRepository(db.cartDao())
        val orderRepo = OrderRepository()
        CartVMFactory(repo, orderRepo)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("INSETS", "Customer fitsSystemWindows=${window.decorView.fitsSystemWindows}")
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



//        var user: UserProfile? = null
//        user = intent.getParcelableExtra<UserProfile>("user")
//        Log.d("CustomerActivity", "Received user info: $user")
//        Toast.makeText(this, "Receive data for: ${user?.userName}, role: ${user?.userRole}", Toast.LENGTH_LONG).show()







    }

    private fun initUI() {
        val frgList = ListFragment();
        val frgCart = CartFragment();
        val frgSetting = SettingFragment();
        val frgOrderStatus = OrderStatusFragment()
        val frgAccount = AccountFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView);

        setCurrentFragment(frgList);

        bottomNavigationView.setOnItemSelectedListener {
//            Toast.makeText(this, "Selected: ${it.title}", Toast.LENGTH_SHORT).show()
            when (it.itemId){
                R.id.milist -> setCurrentFragment(frgList)
                R.id.micart -> setCurrentFragment(frgCart)

                R.id.miorder_status -> {
//                    val bundle = Bundle()
//                    bundle.putParcelable("user", user)
//
//                    val orderStatusFragment = OrderStatusFragment().apply{
//                        arguments = bundle
//                    }

                    setCurrentFragment(frgOrderStatus)
                }
                R.id.mi_account_info -> {
//                    val bundle = Bundle()
//                    bundle.putParcelable("user", user)
//
//                    val accountFragment = AccountFragment().apply {
//                        arguments = bundle
//                    }

                    setCurrentFragment(frgAccount)

                }
            }
            true
        }
    }

    public fun hideBottomNavBar(){
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = BottomNavigationView.GONE
    }

    public fun showBottomNavBar(){
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = BottomNavigationView.VISIBLE
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
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