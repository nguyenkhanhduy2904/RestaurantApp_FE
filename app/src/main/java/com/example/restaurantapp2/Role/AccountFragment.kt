package com.example.restaurantapp2.Role

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.AuthActivity
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.SessionManager
import com.example.restaurantapp2.Utils.isValidEmail
import com.example.restaurantapp2.Utils.isValidPhoneNumber
import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.example.restaurantapp2.repository.DeviceTokenRepository
import com.example.restaurantapp2.repository.UserProfileRepository
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

class AccountFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var etUsername : TextInputEditText
    private lateinit var etPhone : TextInputEditText
    private lateinit var etEmail : TextInputEditText
    private lateinit var etAddress : TextInputEditText

    private lateinit var btnNextAction : MaterialButton
    private lateinit var btnCancel : MaterialButton
    private lateinit var btnChangePassword : MaterialButton
    private lateinit var btnLinkWithGoogle : LinearLayout
    private lateinit var btnLogout : MaterialButton

    private var isEditMode = false

//    private lateinit var user: UserProfile


    private val userVM: UserVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        etAddress = view.findViewById<TextInputEditText>(R.id.etAddress)

        btnNextAction = view.findViewById<MaterialButton>(R.id.btnNextAction)
        btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        btnChangePassword = view.findViewById<MaterialButton>(R.id.btnChangePassword)
        if(session.getLoginMethod()=="GOOGLE"){
            btnChangePassword.visibility = View.GONE
        }

        btnLinkWithGoogle = view.findViewById<LinearLayout>(R.id.btnLoginAsGoogle)
        btnLinkWithGoogle.visibility =View.GONE
        btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        btnNextAction.setOnClickListener {
            isEditMode = !isEditMode

            if (isEditMode) {
                enableEditMode()
            } else {
                disableEditMode()
                updateUser()
            }
        }

        btnCancel.setOnClickListener {
            isEditMode = false
            disableEditMode()

            val currentUser = userVM.user.value ?: return@setOnClickListener
            setupData(currentUser)
        }


        userVM.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                setupData(user)
            }
            disableEditMode()
            Log.d("Bundle user info", "onViewCreated: $user ")
        }

        btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                try {
                    val deviceTokenRepository = DeviceTokenRepository()
                    val token = FirebaseMessaging.getInstance().token.await()
                    val response = deviceTokenRepository.deleteToken(token)
                    FirebaseMessaging.getInstance().deleteToken()
                } catch (e: Exception) {
                    Log.e("LOGOUT", "Cleanup failed", e)
                }

                userVM.clearUser()
                val session  = SessionManager(requireContext())
                session.clear()

                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }

        btnChangePassword.setOnClickListener {

            if (parentFragmentManager.findFragmentByTag("ChangePasswordDialog") != null) {
                return@setOnClickListener
            }
            val dialog = ChangePasswordDialogFragment { request ->

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val authRepo = AuthRepository()
                        authRepo.changePassword(request)

                        Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show()

                        parentFragmentManager.findFragmentByTag("ChangePasswordDialog")
                            ?.let { (it as ChangePasswordDialogFragment).dismiss() }


                    } catch (e: Exception) {
                        if (e is HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            Log.e("API_ERROR", "Code: ${e.code()} Body: $errorBody")
                            Toast.makeText(requireContext(), "Failed: $errorBody", Toast.LENGTH_SHORT).show()

                        }else
                        Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            dialog.show(parentFragmentManager, "ChangePasswordDialog")

        }

    }

    fun enableEditMode(){
        etUsername.isEnabled = true
        etPhone.isEnabled = true
        etEmail.isEnabled = true
        etAddress.isEnabled = true

        btnCancel.visibility = View.VISIBLE
        btnNextAction.text = "Save"

        btnLinkWithGoogle.isEnabled = false
        btnLinkWithGoogle.alpha = 0.5f
        btnChangePassword.isEnabled = false
        btnChangePassword.alpha = 0.5f
        btnLogout.isEnabled = false
        btnLogout.alpha = 0.5f
    }

    fun disableEditMode(){
        etUsername.isEnabled = false
        etPhone.isEnabled = false
        etEmail.isEnabled = false
        etAddress.isEnabled = false

        btnCancel.visibility = View.GONE
        btnNextAction.text = "Edit Profile"

        btnLinkWithGoogle.isEnabled = true
        btnLinkWithGoogle.alpha = 1.0f
        btnChangePassword.isEnabled = true
        btnChangePassword.alpha = 1.0f
        btnLogout.isEnabled = true
        btnLogout.alpha = 1.0f
    }

    fun setupData(userProfile: UserProfile){
        etUsername.setText(userProfile.userName)
        etPhone.setText(userProfile.userPhone ?: "")
        etEmail.setText(userProfile.userEmail ?: "")
        etAddress.setText(userProfile.userAddress ?: "")

    }
    private fun updateUser() {
        val currentUser = userVM.user.value ?: return

        val name = etUsername.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()

//        if (name.isBlank()) {
//            etUsername.error = "Username required"
//            return
//        }

        if (!isValidEmail(email)) {
            etEmail.error = "Invalid email"
            return
        }

        if (!isValidPhoneNumber(phone)) {
            etPhone.error = "Invalid phone number"
            return
        }

//        if (address.isBlank()) {
//            etAddress.error = "Address required"
//            return
//        }


        viewLifecycleOwner.lifecycleScope.launch {
            val repo = UserProfileRepository()
            try {
                val updated = UserProfile(
                    userId = currentUser.userId,
                    userName = name,
                    userPhone = phone,
                    userEmail = email,
                    userAddress = address,
                    userRole = currentUser.userRole,
                    status = currentUser.status
                )

                val response = repo.updateUserInfo(currentUser.userId, updated)

                if (response.errorMessage != null) {
                    Toast.makeText(requireContext(),
                        "Update failed: ${response.errorMessage}",
                        Toast.LENGTH_LONG).show()
                } else if (response.data != null) {
                    val newUser = response.data as UserProfile

                    //THIS IS THE KEY LINE
                    userVM.updateUser(newUser)

                    Toast.makeText(requireContext(),
                        "Profile updated successfully!",
                        Toast.LENGTH_LONG).show()
                }

            } catch (e: Throwable) {
                Toast.makeText(requireContext(),
                    e.message ?: "Error",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

}