package com.example.restaurantapp2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateSigninFragment: Fragment(R.layout.fragment_create_signin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNext = view.findViewById<MaterialButton>(R.id.btnNext)

        val etUserName = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<TextInputEditText>(R.id.etConfirmedPassword)



        btnNext.setOnClickListener(){
            btnNext.isEnabled = false
            val username = etUserName.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val missingFields = mutableListOf<String>()
            if(username.isBlank()){
                missingFields.add("Username")
                etPassword.error = "Required"
            }
            if(password.isBlank()){
                missingFields.add("Password")
                etUserName.error = "Required"

            }
            if(confirmPassword.isBlank()){
                missingFields.add("Confirmed Password")
                etConfirmPassword.error = "Required"

            }
            if (missingFields.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter: ${missingFields.joinToString(", ")}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if(password != confirmPassword){
                Toast.makeText(requireContext(), "Password and Confirm Password do not match", Toast.LENGTH_LONG).show()
                btnNext.isEnabled = true
                return@setOnClickListener

            }
            Log.d("SIGNUP_DEBUG", "username='$username'")
            val authRepo = AuthRepository()
            viewLifecycleOwner.lifecycleScope.launch {

                try{
                    val response = authRepo.localRegister(
                        mapOf(
                            "username" to username,
                            "password" to password
                        )
                    )


                    if (!isAdded) return@launch
                    if (response.errorMessage != null) {
                        Toast.makeText(requireContext(), "Create account failed (error response): ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        btnNext.isEnabled = true
                    }
                    else{
                        Toast.makeText(requireContext(), "Account created successfully! Please login.", Toast.LENGTH_LONG).show()
                        btnNext.isEnabled = true
                        val data = response.data
                        Log.d("CreateSigninFragment", "Account created successfully, data: $data")

                       //change fragment here(after account create success
//                        val userInfo = UserProfile(
//
//                        )
                        val userProfile = data as UserProfile
                        Log.d("UserProfile data check", userProfile.toString())
                        val moreInfoFragment = MoreInfoFragment()
                        val bundle = Bundle()
                        bundle.putParcelable("userInfo", userProfile)
                        moreInfoFragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.flAuthContainer, moreInfoFragment)
                            .addToBackStack(null)
                            .commit()

                    }

                }
                catch (e: Throwable) {
                    if (!isAdded) return@launch

                    Log.e("SIGNUP_ERROR", "Error: ", e)

                    val message = when (e) {
                        is HttpException -> {
                            val errorBody = e.response()?.errorBody()?.string()

                            try {
                                val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                                apiError?.errorMessage ?: "Server error (${e.code()})"
                            } catch (ex: Exception) {
                                "Server error (${e.code()})"
                            }
                        }
                        else -> e.message ?: "Unknown error"
                    }

                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                } finally {
                    btnNext.isEnabled = true
                }

            }
        }


    }
}