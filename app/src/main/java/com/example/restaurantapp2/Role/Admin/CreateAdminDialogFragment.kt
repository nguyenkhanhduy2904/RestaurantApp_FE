package com.example.restaurantapp2.Role.Admin

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.restaurantapp2.R
import com.example.restaurantapp2.models.ChangePasswordRequest
import com.example.restaurantapp2.viewmodels.UserVM

class CreateAdminDialogFragment(
    private val onSave : ((Map<String, String>)) -> Unit
) : DialogFragment() {

    private val userVM : UserVM by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        val dialog = Dialog(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_pop_up_create_admin, null)

        dialog.setContentView(view)

        val edtUserName = view.findViewById<EditText>(R.id.edtLoginName)

        val edtNewPass = view.findViewById<EditText>(R.id.edtPassword)
        val edtConfirmed = view.findViewById<EditText>(R.id.edtConfirmedPassword)
        val btnSave = view.findViewById<Button>(R.id.btnNextAction)

        btnSave.setOnClickListener{
            val username = edtUserName.text.toString().trim()
            val newPass = edtNewPass.text.toString().trim()
            val confirm = edtConfirmed.text.toString().trim()

            val missingFields = mutableListOf<String>()

            if (username.isEmpty()) {
                missingFields.add("Username")
                edtUserName.error = "Required"
            }
            if (newPass.isEmpty()) {
                missingFields.add("New password")
                edtNewPass.error = "Required"
            }
            if (confirm.isEmpty()) {
                missingFields.add("Confirm password")
                edtConfirmed.error = "Required"
            }

            if (missingFields.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter: ${missingFields.joinToString(", ")}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if(newPass != confirm){
                Toast.makeText(requireContext(), "Password need to be the same as confirm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSave(
                mapOf(
                    "username" to username,
                    "password" to newPass
                )
            )
//            dismiss()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}