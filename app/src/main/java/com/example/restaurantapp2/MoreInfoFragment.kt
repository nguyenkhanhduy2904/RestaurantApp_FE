package com.example.restaurantapp2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.models.UserProfile
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MoreInfoFragment: Fragment(R.layout.fragment_more_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etAddress = view.findViewById<TextInputEditText>(R.id.etAddress)

        btnSave.setOnClickListener{
            val userName = etUsername.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()


           val uProfile = UserProfile(
               userId = 1,//holder
               userName = userName,
               userEmail = email,
               userPhone = phone,
               userAddress = address,
               userRole = "",//holder
               status = ""//holder

           )



        }

    }
}