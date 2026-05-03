package com.example.restaurantapp2.Role.Customer

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.adapter.CartAdapter
import com.example.restaurantapp2.data.AppDatabase
import com.example.restaurantapp2.databinding.FragmentCartBinding
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.repository.CartRepository
import com.example.restaurantapp2.repository.OrderRepository
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.CartVMFactory
import com.example.restaurantapp2.viewmodels.ProductVM
import com.example.restaurantapp2.viewmodels.UserVM
import com.google.android.material.button.MaterialButton


class CartFragment: Fragment(R.layout.fragment_cart){
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartAdapter



    private val cartVM: CartVM by lazy {
        (requireActivity() as CustomerActivity).cartVM
    }
    private val productVM: ProductVM by activityViewModels()

    private val userVM: UserVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)

        adapter = CartAdapter(
            onDeleteClick = { cartItem ->
                cartVM.delete(cartItem)
            },
            onIncreaseClick = { cartItem ->
                cartVM.increaseQuantity(cartItem)
            },
            onDecreaseClick = { cartItem ->
                cartVM.decreaseQuantity(cartItem)
            },
            onNoteChange = { item, note ->
                cartVM.updateNote(item, note)
            }
        )
        binding.rvCartList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartList.adapter = adapter

        val userId = userVM.user.value?.userId ?: return

//        val cartLiveData = cartVM.getCart(userId)
//        val productLiveData = productVM.products
        cartVM.init(
            cartVM.getCart(userId),
            productVM.products
        )

        cartVM.cartUIList.observe(viewLifecycleOwner) {

            adapter.submitList(it)
        }

        binding.btnCreateOrder.setOnClickListener {
            if(cartVM.cartUIList.value.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Your Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, OrderInfoFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rvCartList.setOnTouchListener { _, event ->
            val focusedView = requireActivity().currentFocus

            if (focusedView is android.widget.EditText) {
                val outRect = android.graphics.Rect()
                focusedView.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    focusedView.clearFocus()
                }
            }

            false
        }
        cartVM.totalPrice.observe(viewLifecycleOwner) { total ->
            if (total != null) {
                binding.tvFinalPay.text = convertedPrice(total)
            }
        }
    }


}