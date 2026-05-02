package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.repository.UserProfileRepository
import kotlinx.coroutines.launch

class UserVM: ViewModel() {
    private val repo = UserProfileRepository()
    private val _user = MutableLiveData<UserProfile?>()
    val user: LiveData<UserProfile?> = _user
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _userList = MutableLiveData<List<UserProfile>>(emptyList())
    val userList : LiveData<List<UserProfile>> = _userList

    fun setUser(userProfile: UserProfile) {
        _user.value = userProfile
    }

    fun getUserById(id: Int){
        viewModelScope.launch {
            try{
                val response = repo.getUserInfo(id)
                if(response.errorMessage!=null){
                    Log.d("USER VM", response.errorMessage.toString())
                    _toastMessage.value = response.errorMessage ?: "Unknown error"

                }
                else{
                    _user.value =response.data as UserProfile
                    Log.d("USER VM", response.data.toString())
                    _toastMessage.value = (response.data ?: "Unknown error").toString()
                }
            }catch (e : Exception){
                Log.d("USER VM", e.message.toString())
                _toastMessage.value = e.message ?: "Unknown error"
            }
        }
    }

    fun updateUser(userProfile: UserProfile) {
        _user.value = userProfile
    }
    fun clearUser(){
        _user.value = null
    }
    fun getAllUser(){
        viewModelScope.launch {
            try{
                val response = repo.getAllUser()

                if (response.errorMessage != null) {
                    Log.d("UserVM", response.errorMessage)

                } else if (response.data != null) {
                    Log.d("UserVM", response.data.toString())
                    val list = response.data?: emptyList()
                    _userList.value = list


                }
            }catch (e: Exception){
                Log.d("USER VM", e.message.toString())
                _toastMessage.value = e.message ?: "Unknown error"

            }
        }
    }
    fun updateUserBE(userProfile: UserProfile) {
        viewModelScope.launch {
            try {
                val response = repo.updateUserInfo(userProfile.userId,userProfile)

                if (response.errorMessage == null) {
                    _toastMessage.value = "Update success"
                    getAllUser() // refresh list
                } else {

                    Log.d("UserVM", response.errorMessage)
                }

            } catch (e: Exception) {
                Log.d("UserVM", e.message.toString())
                _toastMessage.value = e.message ?: "Unknown error"
            }
        }
    }
}