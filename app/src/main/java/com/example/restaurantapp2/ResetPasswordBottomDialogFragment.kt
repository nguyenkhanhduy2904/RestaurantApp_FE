package com.example.restaurantapp2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.Utils.isValidEmail
import com.example.restaurantapp2.models.ResetPasswordRequest
import com.example.restaurantapp2.repository.AuthRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ResetPasswordBottomDialogFragment() : BottomSheetDialogFragment(R.layout.bottom_dialog_forgot_password_fragment) {
    private enum class Step {
        ENTER_EMAIL,
        ENTER_OTP,
        ENTER_NEWPASSWORD
    }



    private var currentStep = Step.ENTER_EMAIL

    private lateinit var btnSendEmail: MaterialButton
    private lateinit var btnSendOTP: MaterialButton
    private lateinit var btnSendNewPassword: MaterialButton

    private lateinit var edtEmail: EditText
    private lateinit var edtOTP: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var lbEmail :TextView
    private lateinit var lbOTP :TextView
    private lateinit var lbPassword :TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSendEmail = view.findViewById(R.id.btnSendEmail)
        btnSendOTP = view.findViewById(R.id.btnSendOTP)
        btnSendNewPassword = view.findViewById(R.id.btnSendNewPassword)

        edtEmail = view.findViewById(R.id.edtEnterEmail)
        edtOTP = view.findViewById(R.id.edtEnterOTP)
        edtNewPassword = view.findViewById(R.id.edtEnterNewPassword)

        lbEmail = view.findViewById(R.id.lbEnterEmail)
        lbOTP = view.findViewById(R.id.lbEnterOTP)
        lbPassword = view.findViewById(R.id.lbEnterNewPassword)

        val authRepo = AuthRepository()
        var userId : Int = -1



        btnSendEmail.setOnClickListener {
            val email = edtEmail.text.toString()
            if (email.isBlank()) {
                edtEmail.error = "Email required"
                return@setOnClickListener
            }
            if(!isValidEmail(email)){
                edtEmail.error = "Invalid Email"
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = authRepo.forgotPassword(email)
                    if(response.errorMessage!=null){
                        Log.d("Forgot pass", response.errorMessage)
                        Toast.makeText(requireContext(),"Error: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    else{
                        Toast.makeText(requireContext(),"Check your email for OTP", Toast.LENGTH_SHORT).show()
                        currentStep = Step.ENTER_OTP
                        updateUI()

                    }
                }catch (e: Exception){
                    Log.d("Forgot pass", e.message.toString())
                    Toast.makeText(requireContext(),"Error: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
                }
            }


        }

        btnSendOTP.setOnClickListener {
            val otp = edtOTP.text.toString()
            if (otp.isBlank()) {
                edtOTP.error = "OTP required"
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {

                    val response = authRepo.verifyOtp(otp)
                    if(response.errorMessage!=null){
                        Log.d("Forgot pass otp", response.errorMessage)
                        Toast.makeText(requireContext(),"Error: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    else{
                        userId = response.data?.userId ?: -1
                        Toast.makeText(requireContext(),"Enter your new password", Toast.LENGTH_SHORT).show()
                        currentStep = Step.ENTER_NEWPASSWORD
                        updateUI()

                    }
                }catch (e: Exception){
                    Log.d("Forgot pass", e.message.toString())
                    Toast.makeText(requireContext(),"Error: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnSendNewPassword.setOnClickListener {
            if(userId!=-1){
                val newPass = edtNewPassword.text.toString()
                if (newPass.isBlank()) {
                    edtNewPassword.error = "Min 6 characters"
                    return@setOnClickListener
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    try {


                        val response = authRepo.resetPassword(
                            ResetPasswordRequest(
                                        userId,
                                        newPass
                        ))
                        if(response.errorMessage!=null){
                            Log.d("Forgot pass new pass", response.errorMessage)
                            Toast.makeText(requireContext(),"Error: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        else{

                            Toast.makeText(requireContext(),"Reset password success", Toast.LENGTH_SHORT).show()
                            dismiss()


                        }
                    }catch (e: Exception){
                        Log.d("Forgot pass", e.message.toString())
                        Toast.makeText(requireContext(),"Error: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }

            }


        }
    }

    private fun updateUI() {

        when (currentStep) {

            Step.ENTER_EMAIL -> {
                edtEmail.visibility = View.VISIBLE
                edtOTP.visibility = View.GONE
                edtNewPassword.visibility = View.GONE

                btnSendEmail.visibility = View.VISIBLE
                btnSendOTP.visibility = View.GONE
                btnSendNewPassword.visibility = View.GONE

                lbEmail.visibility = View.VISIBLE
                lbOTP.visibility = View.GONE
                lbPassword.visibility = View.GONE
            }

            Step.ENTER_OTP -> {
                edtEmail.visibility = View.GONE
                edtOTP.visibility = View.VISIBLE
                edtNewPassword.visibility = View.GONE

                btnSendEmail.visibility = View.GONE
                btnSendOTP.visibility = View.VISIBLE
                btnSendNewPassword.visibility = View.GONE

                lbEmail.visibility = View.GONE
                lbOTP.visibility = View.VISIBLE
                lbPassword.visibility = View.GONE
            }

            Step.ENTER_NEWPASSWORD -> {
                edtEmail.visibility = View.GONE
                edtOTP.visibility = View.GONE
                edtNewPassword.visibility = View.VISIBLE

                btnSendEmail.visibility = View.GONE
                btnSendOTP.visibility = View.GONE
                btnSendNewPassword.visibility = View.VISIBLE

                lbEmail.visibility = View.GONE
                lbOTP.visibility = View.GONE
                lbPassword.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        currentStep = Step.ENTER_EMAIL
        updateUI()

        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        val displayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels * 0.5).toInt()

        bottomSheet?.layoutParams?.height = height

        BottomSheetBehavior.from(bottomSheet!!).state =
            BottomSheetBehavior.STATE_EXPANDED
    }

}