package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.databinding.ItemUserBinding
import com.example.restaurantapp2.models.UserProfile

class UserAdapter(
    private val onStatusClick : (UserProfile) -> Unit

) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val users = mutableListOf<UserProfile>()

    inner class UserViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item : UserProfile){
            binding.tvUserID.text = item.userId.toString()
            binding.tvUserName.text = item.userName
            binding.tvUserEmail.text = item.userEmail
            binding.tvUserPhone.text = item.userPhone
            binding.tvRole.text = item.userRole
            binding.switchOption.setOnCheckedChangeListener(null)
            binding.switchOption.isChecked = item.status == "ACTIVE"

            binding.switchOption.setOnCheckedChangeListener { _, _ ->
                onStatusClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),parent, false
        )
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = users[position]
        holder.bind(item)
    }

    fun updateData(newUsers: List<UserProfile>) {
        Log.d("UserAdapter", "updating with ${newUsers.size} users")
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
}