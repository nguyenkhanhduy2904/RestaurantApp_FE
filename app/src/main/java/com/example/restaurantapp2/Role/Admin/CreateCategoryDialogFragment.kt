package com.example.restaurantapp2.Role.Admin

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.restaurantapp2.R

class CreateCategoryDialogFragment(
    private val onSave: (String) -> Unit
): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val view = requireActivity().layoutInflater
            .inflate(R.layout.dialog_popup_create_category, null)

        dialog.setContentView(view)

        val edtName = view.findViewById<EditText>(R.id.edtCategoryName)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Required"
                return@setOnClickListener
            }

            onSave(name)   // 🔥 send data back
            dismiss()
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