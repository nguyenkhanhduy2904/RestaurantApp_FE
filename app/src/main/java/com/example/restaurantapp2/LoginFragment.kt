package com.example.restaurantapp2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.Role.Admin.AdminActivity
import com.example.restaurantapp2.Role.Customer.CustomerActivity
import com.example.restaurantapp2.Utils.AuthManager
import com.example.restaurantapp2.Utils.SessionManager
import com.example.restaurantapp2.data.AppDatabase
import com.example.restaurantapp2.models.CartItem
import com.example.restaurantapp2.models.DeviceTokenRequest
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.example.restaurantapp2.repository.CartRepository
import com.example.restaurantapp2.repository.DeviceTokenRepository
import com.example.restaurantapp2.repository.OrderRepository
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.CartVMFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLoginLocal = view.findViewById<MaterialButton>(R.id.btnLogin)
        val authManager = AuthManager(requireContext())

        val btnForgotPassword = view.findViewById<TextView>(R.id.tvForgotPassword)
        btnForgotPassword.setOnClickListener{
            val dialog = ResetPasswordBottomDialogFragment()
            dialog.show(parentFragmentManager, "ResetPasswordDialog")
        }

        googleLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken

                    if (idToken == null) {
                        Log.d("idToken", "id token was null")
//                        Toast.makeText(requireContext(), "Id token null", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val user = authManager.loginWithGoogle(idToken)


                            if (user != null) {
                                val session = SessionManager(requireContext())
                                session.saveUser(userId = user.userId, userRole = user.userRole, loginMethod = "GOOGLE")
                                session.isLoggedIn()

                                navigateUser(user)
                                Log.d("Google sign in", "User data is " + user.userRole + user.userName + user.userId + user.status)
                            }
                            else{
                                Log.d("Google sign in", "User data is null" )
                            }

                        } catch (e: Exception) {
                            Log.e("GoogleLogin", "Error: ${e.message}", e)  // add this
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: ApiException) {
                    Log.e("GoogleLogin", "Error: ${e.message}", e)  // add this
                    Toast.makeText(requireContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }


        btnLoginLocal.setOnClickListener {
            btnLoginLocal.isEnabled = false
            Log.d("Butto test", "Button login clicked")

            val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
            val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)

            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val missingFields = mutableListOf<String>()
            if(username.isBlank()){
                etUsername.error = "Required"
                missingFields.add("Username")

            }
            if(password.isBlank()){
                etPassword.error = "Required"
                missingFields.add("Password")

            }
            if (missingFields.isNotEmpty()) {
                btnLoginLocal.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "Please enter: ${missingFields.joinToString(", ")}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val user = authManager.loginAsLocal(username, password)


                    if (user != null && user.status !="LOCK") {
                        val session = SessionManager(requireContext())
                        session.saveUser(userId = user.userId, userRole = user.userRole, loginMethod = "LOCAL")
                        navigateUser(user)
                    }
                    if(user != null && user.status =="LOCK"){
                        Toast.makeText(requireContext(), "Your Account have been lock",Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                } catch (e: HttpException) {
                    when (e.code()) {
                        401 -> Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    btnLoginLocal.isEnabled = true
                }
            }

        }

        val tvSignupLocal = view.findViewById<TextView>(R.id.tvSignupLocal)
        tvSignupLocal.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.flAuthContainer, CreateSigninFragment())
                .addToBackStack(null)
                .commit()

//            Toast.makeText(requireContext(), "Clicked create account", Toast.LENGTH_LONG).show()
        }
        val btnLoginAsGoogle = view.findViewById<View>(R.id.btnLoginAsGoogle)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1025657299481-drpdf0f8s7ndgeffb9r2ona2uo2ju4ud.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        btnLoginAsGoogle.setOnClickListener {
            googleLauncher.launch(googleSignInClient.signInIntent)
        }



    }

    private fun navigateUser(user: UserProfile) {
        val intent = when (user.userRole) {
            "ADMIN" -> Intent(requireContext(), AdminActivity::class.java)
            "CUSTOMER" -> Intent(requireContext(), CustomerActivity::class.java)
            else -> return
        }

        intent.putExtra("user", user)
        startActivity(intent)
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


//                Toast.makeText(requireContext(), "Logged in as $name ($email)", Toast.LENGTH_LONG).show()
                Log.d("GoogleLogin", "ID Token: $idToken")

                val authRepo = AuthRepository()
                viewLifecycleOwner.lifecycleScope.launch {
                    try{
                        if(idToken == null){
//                            Toast.makeText(requireContext(), "Id token was null", Toast.LENGTH_LONG).show()
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
//                            Toast.makeText(requireContext(), "Google login success", Toast.LENGTH_LONG).show()
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