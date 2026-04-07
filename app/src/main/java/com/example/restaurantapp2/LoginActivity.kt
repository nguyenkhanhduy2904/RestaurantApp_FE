package com.example.restaurantapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.restaurantapp2.Role.Admin.AdminActivity
import com.example.restaurantapp2.Role.Customer.CustomerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)//check this layout name!!!!!!
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1025657299481-sd6ei3fvd1bn68ncnklb161agkh2eic0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val btnLoginAsGoogle = findViewById<View>(R.id.btnLoginAsGoogle)
        btnLoginAsGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100) // RC_SIGN_IN = 100

        }






        //un-comment this code and change the layout to activity_login to use the quick login buttons for admin and customer

//        val btnAdmin = findViewById<Button>(R.id.admin_button)
//        val btnCustomer = findViewById<Button>(R.id.customer_button)
//
//        btnAdmin.setOnClickListener {
//            val intent = Intent(this, AdminActivity::class.java)
//            startActivity(intent)
//        }
//        btnCustomer.setOnClickListener {
//            val intent = Intent(this, CustomerActivity::class.java)
//            startActivity(intent)
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val name = account.displayName
                val email = account.email
                val idToken = account.idToken

                Toast.makeText(this, "Logged in as $name ($email)", Toast.LENGTH_LONG).show()
                Log.d("GoogleLogin", "ID Token: $idToken")

            } catch (e: ApiException) {
                Toast.makeText(this, "Sign in failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
                Log.d("GoogleLogin", "Sign in failed: ${e.statusCode}")
            }
        }
    }




}