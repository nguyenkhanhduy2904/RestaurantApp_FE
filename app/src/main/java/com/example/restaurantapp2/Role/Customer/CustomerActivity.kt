package com.example.restaurantapp2.Role.Customer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val frgList = ListFragment();
        val frgCart = CartFragment();
        val frgSetting = SettingFragment();
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView);

        setCurrentFragment(frgList);

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.milist -> setCurrentFragment(frgList)
                R.id.micart -> setCurrentFragment(frgCart)
                R.id.misetting -> setCurrentFragment(frgSetting)
            }
            true
        }





    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}