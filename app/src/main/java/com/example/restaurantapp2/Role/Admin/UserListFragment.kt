package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Role.ChangePasswordDialogFragment
import com.example.restaurantapp2.adapter.UserAdapter
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.AuthRepository
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class UserListFragment: Fragment(R.layout.fragment_user_list) {
    private val userVM : UserVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UserAdapter(
            onStatusClick = { user ->
                val updatedUser = user.copy(
                    status = if (user.status == "ACTIVE") {
                        "LOCK"
                    } else {
                        "ACTIVE"
                    }
                )
                userVM.updateUserBE(updatedUser)
            }
        )
        val rv = view.findViewById<RecyclerView>(R.id.rvUserList)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter


        userVM.getAllUser()
        userVM.userList.observe(viewLifecycleOwner){list ->
            Log.d("Fragment", "list size = ${list.size}")
            adapter.updateData(list)
        }
        userVM.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        val btnAddAdmin = view.findViewById<FloatingActionButton>(R.id.fadAddAdmin)
        btnAddAdmin.setOnClickListener{
            if (parentFragmentManager.findFragmentByTag("CreateAdminDialog") != null) {
                return@setOnClickListener
            }
            val dialog = CreateAdminDialogFragment{ map ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try{
                        val authRepo = AuthRepository()
                        val response = authRepo.createAdmin(map)
                        if(response.errorMessage!=null){
                            Toast.makeText(requireContext(),"Error: ${response.errorMessage}",Toast.LENGTH_SHORT).show()
                            return@launch
                        }else{
                            Toast.makeText(requireContext(),"Success create admin",Toast.LENGTH_SHORT).show()
                            userVM.getAllUser()
                            parentFragmentManager.findFragmentByTag("CreateAdminDialog")
                                ?.let { (it as CreateAdminDialogFragment).dismiss() }
                        }
                    }catch (e:Exception){
                        Log.d("add admin", e.message.toString())
                        Toast.makeText(requireContext(),"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.show(parentFragmentManager, "CreateAdminDialog")
        }
    }
}