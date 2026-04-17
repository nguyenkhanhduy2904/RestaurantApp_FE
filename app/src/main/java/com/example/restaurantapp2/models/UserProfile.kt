package com.example.restaurantapp2.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val userId: Int,
    val userName: String,
    val userAddress: String?,
    val userPhone: String?,
    val userEmail: String?,
    val userRole: String,
    val status: String
) : Parcelable