package com.example.e_commerce.presentation.cart_wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAdapterClicks()
        observeCart()

        viewModel.getCart()
    }

    private fun setupRecyclerView() {

        cartAdapter = CartAdapter()

        binding.recyclerCart.apply {

            adapter = cartAdapter


        }
    }

    private fun setupAdapterClicks() {

        cartAdapter.onProductClick = { productId ->

          val action = CartFragmentDirections.actionCartFragmentToProductDetailsFragment(productId)
            findNavController().navigate(action)

        }

        cartAdapter.onIncreaseClick = { item ->

            viewModel.updateCart(
                item.productId,
                item.count + 1
            )

        }

        cartAdapter.onDecreaseClick = { item ->

            if (item.count > 1) {

                viewModel.updateCart(
                    item.productId,
                    item.count - 1
                )
            }
        }
//
        cartAdapter.onDeleteClick = { item ->

            viewModel.removeFromCart(
                item.productId
            )
        }
    }

    private fun observeCart() {

        viewModel.cart.observe(
            viewLifecycleOwner
        ) {
            cart ->

            cartAdapter.submitList(
                cart.products
            )

            binding.tvTotalPrice.text =
                "${cart.totalPrice} EGP"

            binding.tvItemsCount.text =
                "${cart.numberOfItems} items"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}