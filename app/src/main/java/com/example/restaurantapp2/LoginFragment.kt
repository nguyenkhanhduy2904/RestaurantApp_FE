package com.example.restaurantapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.Role.Admin.AdminActivity
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLoginLocal = view.findViewById<MaterialButton>(R.id.btnLogin)
        btnLoginLocal.setOnClickListener {

            btnLoginLocal.isEnabled = false

            val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
            val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)

            val username = etUsername.text.toString()
            val password = etPassword.text.toString()



            val authRepo = AuthRepository()

            viewLifecycleOwner.lifecycleScope.launch {
                try{
                    val response = authRepo.login(
                        mapOf(
                            "username" to username,
                            "password" to password
                        )
                    )

                    if (response.errorMessage != null) {
                        Toast.makeText(requireContext(), "Login failed: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        btnLoginLocal.isEnabled = true
                    }
                    else {
                        btnLoginLocal.isEnabled = true
                        val data = response.data
                        Log.d("LoginActivity", "Login successful, data: $data")

                        val userProfile = UserProfile(
                            userId = (data?.get("userId") as? Double)?.toInt() ?: 0,
                            userName = data?.get("userName") as? String ?: "",
                            userAddress = data?.get("userAddress") as? String,
                            userPhone = data?.get("userPhone") as? String,
                            userEmail = data?.get("userEmail") as? String,
                            userRole = data?.get("userRole") as? String ?: "",
                            status = data?.get("status") as? String ?: ""
                        )
                        if (userProfile.userRole == "ADMIN") {
                            val intent = Intent(requireContext(), AdminActivity::class.java)
////                        intent.putExtra("userProfile", userProfile)
                            startActivity(intent)
                            Log.d(
                                "LoginActivity",
                                "this was an admin, status: ${userProfile.status}"
                            )
                        }



                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_LONG)
                            .show()
                    }

                }
                catch (e: HttpException){
                    if (e.code() == 401) {
                        Log.e("API", "Invalid credentials")
                        Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_LONG).show()
                        btnLoginLocal.isEnabled = true
                    } else {
                        Log.e("API", "HTTP error: ${e.code()}")
                        Toast.makeText(requireContext(), "Login failed: HTTP error ${e.code()}", Toast.LENGTH_LONG).show()
                        btnLoginLocal.isEnabled = true
                    }
                }catch (e: Exception){
                    Log.e("API", "Error: ${e.message}")
                    btnLoginLocal.isEnabled = true
                }

            }


        }

        val tvSignupLocal = view.findViewById<TextView>(R.id.tvSignupLocal)
        tvSignupLocal.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.flAuthContainer, CreateSigninFragment())
                .addToBackStack(null) // 👈 important for back button
                .commit()

            Toast.makeText(requireContext(), "Clicked create account", Toast.LENGTH_LONG).show()
        }





        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1025657299481-drpdf0f8s7ndgeffb9r2ona2uo2ju4ud.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val btnLoginAsGoogle = view.findViewById<View>(R.id.btnLoginAsGoogle)
        btnLoginAsGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100) // RC_SIGN_IN = 100

        }
    }


//        //un-comment this code and change the layout to activity_login to use the quick login buttons for admin and customer
//
////        val btnAdmin = findViewById<Button>(R.id.admin_button)
////        val btnCustomer = findViewById<Button>(R.id.customer_button)
////
////        btnAdmin.setOnClickListener {
////            val intent = Intent(this, AdminActivity::class.java)
////            startActivity(intent)
////        }
////        btnCustomer.setOnClickListener {
////            val intent = Intent(this, CustomerActivity::class.java)
////            startActivity(intent)
////        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val name = account.displayName
                val email = account.email
                val idToken = account.idToken


                Toast.makeText(requireContext(), "Logged in as $name ($email)", Toast.LENGTH_LONG).show()
                Log.d("GoogleLogin", "ID Token: $idToken")

                val authRepo = AuthRepository()
                viewLifecycleOwner.lifecycleScope.launch {
                    try{
                        if(idToken == null){
                            Toast.makeText(requireContext(), "Id token was null", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                        val response = authRepo.googleAuth(
                            mapOf("idToken" to idToken)
                        )
                        if(response.errorMessage !=null){
                            Toast.makeText(requireContext(), "Google Login fail", Toast.LENGTH_LONG).show()
                            Log.d("google login", "Google login failed: ${response.errorMessage}")
                        }
                        else{
                            val data = response.data
                            Toast.makeText(requireContext(), "Google login success", Toast.LENGTH_LONG).show()
                            Log.d("google login", "Google login sucess")
                            Log.d("google login", "data: $data")
                        }

                    }catch (e: HttpException){
                        Log.e("google login", "HTTP error: ${e.message}")
                        Toast.makeText(requireContext(), "Server error", Toast.LENGTH_LONG).show()

                    }catch (e:Exception){
                        Log.e("google login", "Error: ${e.message}")
                        Toast.makeText(requireContext(), "Unexpected error", Toast.LENGTH_LONG).show()

                    }
                }





            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Sign in failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
                Log.d("GoogleLogin", "Sign in failed: ${e.statusCode}")
            }
        }
    }




}