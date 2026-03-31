package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val frgProdLs = ProductListFragment()
        val frgOrderLs = OrderListFragment()
        val frgSetting = SettingFragment()
        val frgUserLs = UserListFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView);


        setCurrentFragment(frgProdLs);

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.mi_product_list -> setCurrentFragment(frgProdLs)
                R.id.mi_user_list -> setCurrentFragment(frgUserLs)
                R.id.misetting -> setCurrentFragment(frgSetting)
                R.id.mi_order_list -> setCurrentFragment(frgOrderLs)
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
}