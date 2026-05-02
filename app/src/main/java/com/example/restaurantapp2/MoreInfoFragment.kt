package com.example.restaurantapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.Role.Admin.AdminActivity
import com.example.restaurantapp2.Role.Customer.CustomerActivity
import com.example.restaurantapp2.Utils.isValidEmail
import com.example.restaurantapp2.Utils.isValidPhoneNumber
import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.UserProfileRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MoreInfoFragment: Fragment(R.layout.fragment_more_info) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getParcelable<UserProfile>("userInfo")
        Log.d("MoreInfo", "User = $user")

        val btnSave = view.findViewById<MaterialButton>(R.id.btnNextAction)
        val btnNotNow = view.findViewById<TextView>(R.id.tvNotNow)
        val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etAddress = view.findViewById<TextInputEditText>(R.id.etAddress)

        btnSave.setOnClickListener {
            val userName = etUsername.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()

//            if (userName.isBlank()) {
//                etUsername.error = "Username required"
//                return@setOnClickListener
//            }

            if (!isValidEmail(email)) {
                etEmail.error = "Invalid email"
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(phone)) {
                etPhone.error = "Invalid phone number"
                return@setOnClickListener
            }

//            if (address.isBlank()) {
//                etAddress.error = "Address required"
//                return@setOnClickListener
//            }

            val updatedUser = user?.copy(
                userName = userName,
                userAddress = address,
                userPhone = phone,
                userEmail = email
            )

            val userPro5Repo = UserProfileRepository()
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    if(user!=null && updatedUser !=null){
                        val response = userPro5Repo.updateUserInfo(user.userId, updatedUser)
                        if (!isAdded) return@launch

                        if (response.errorMessage != null) {
                            Toast.makeText(requireContext(), "Add more info failed (error response): ${response.errorMessage}", Toast.LENGTH_LONG).show()
                            Log.d("Failed update more info", response.errorMessage.toString())
                        }
                        else if(response.data!=null){
                            Toast.makeText(requireContext(),"Updated successfully: ${response.data}", Toast.LENGTH_LONG).show()
                            Log.d("Success update more info", response.data.toString())

                            if(user?.userRole == "ADMIN"){
                                val intent = Intent(requireContext(), AdminActivity::class.java)
                                intent.putExtra("user", user)
                                startActivity(intent)
                            }
                            else {
                                val intent = Intent(requireContext(), CustomerActivity::class.java)
                                intent.putExtra("user", user)
                                startActivity(intent)

                            }


                        }
                    }


                } catch (e: Throwable) {
                    if (!isAdded) return@launch

                    Log.e("MORE_INFO_ERROR", "Error: ", e)

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
                }


            }

        }

        btnNotNow.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Skip profile update?")
                .setMessage(
                    "Your profile is incomplete.\n\n" +
                            "You can update it later in Settings.\n\n" +
                            "⚠️ Accounts without email may not be recoverable if you forget your password."
                )
                .setPositiveButton("Continue") { _, _ ->

                    val intent = if (user?.userRole == "ADMIN") {
                        Intent(requireContext(), AdminActivity::class.java)
                    } else {
                        Intent(requireContext(), CustomerActivity::class.java)
                    }

                    intent.putExtra("user", user)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}