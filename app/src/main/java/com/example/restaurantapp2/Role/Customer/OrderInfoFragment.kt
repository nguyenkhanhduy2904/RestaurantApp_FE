package com.example.restaurantapp2.Role.Customer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.Utils.isValidPhoneNumber
import com.example.restaurantapp2.repository.OrderRepository
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.OrderVM
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class OrderInfoFragment: Fragment(R.layout.fragment_edit_order) {

    private val cartVM: CartVM by lazy {
        (requireActivity() as CustomerActivity).cartVM
    }
    private val orderVM : OrderVM by activityViewModels()
    private val userVM: UserVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAddress = view.findViewById<TextInputEditText>(R.id.etAddress)
        val btnPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        btnAddress.setText(userVM.user.value?.userAddress ?: "")
        btnPhone.setText(userVM.user.value?.userPhone ?: "")
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)

        cartVM.totalPrice.observe(viewLifecycleOwner) { total ->
            if (total != null) {
                tvPrice.text = convertedPrice(total)
            }
        }
        val selectedPaymentMethod = view.findViewById<RadioGroup>(R.id.rgPaymentMethod)

        val btnConfirm = view.findViewById<View>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            if(cartVM.cartUIList.value.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Your Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val address = (btnAddress as TextInputEditText).text.toString()
            val phone = (btnPhone as TextInputEditText).text.toString()
            if(address.isBlank()){
                btnAddress.error = "Require!"
                return@setOnClickListener
            }
            if(phone.isBlank()){
                btnPhone.error = "Require!"
                return@setOnClickListener
            }
            if(!isValidPhoneNumber(phone)){
                btnPhone.error = "Invalid!"
                return@setOnClickListener
            }
            val selectedId = selectedPaymentMethod.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Please select payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = when (selectedId) {
                R.id.rbCOD -> "COD"
                R.id.rbVNPAY -> "VNPAY"
                else -> ""
            }
            val userId = userVM.user.value?.userId

            if (userId == null) {
                Toast.makeText(requireContext(), "User not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cartVM.placeOrder(userId = userId, address = address, phone = phone, paymentStatus = "UNPAID", paymentMethod = paymentMethod)
            cartVM.success.observe(viewLifecycleOwner){ order ->
                if (order != null) {
                    // reset so it doesn't re-trigger on back navigation
                    cartVM.resetSuccess()

                    // go back to previous fragment
                    parentFragmentManager.popBackStack()
                }

            }

        }
        cartVM.success.observe(viewLifecycleOwner){ order ->
            if (order != null) {
                if(order.paymentMethod == "VNPAY"){
                    cartVM.getVnpayUrl(orderId = order.orderId)
                }
            }
            else {
                Toast.makeText(requireContext(), "Order placed", Toast.LENGTH_SHORT).show()
            }

        }

        cartVM.vnpayUrl.observe(viewLifecycleOwner) { url ->
            Log.d("VNPay", "url received: $url")
            if (!url.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }



    }
}