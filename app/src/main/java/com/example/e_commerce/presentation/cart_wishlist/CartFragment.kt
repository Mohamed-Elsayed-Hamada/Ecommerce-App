package com.example.e_commerce.presentation.cart_wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.core.util.formatMoney
import com.example.e_commerce.core.util.navigateSafely
import com.example.e_commerce.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar
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
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClicks()
        observeCart()

        binding.btnCheckout.isEnabled = false
        binding.btnCheckout.setText(R.string.action_checkout_unavailable)

        viewModel.getCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter()
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        cartAdapter.onProductClick = { productId ->
            navigateSafely(
                CartFragmentDirections.actionCartFragmentToProductDetailsFragment(productId),
                R.id.cartFragment
            )
        }

        cartAdapter.onIncreaseClick = { item ->
            viewModel.updateCart(item.productId, item.count + 1)
        }

        cartAdapter.onDecreaseClick = { item ->
            if (item.count > 1) {
                viewModel.updateCart(item.productId, item.count - 1)
            }
        }

        cartAdapter.onDeleteClick = { item ->
            viewModel.removeFromCart(item.productId, item.count)
        }

        binding.layoutEmpty.btnEmptyAction.setOnClickListener {
            navigateSafely(
                CartFragmentDirections.actionCartFragmentToProductsFragment(),
                R.id.cartFragment
            )
        }

        binding.layoutError.btnRetry.setOnClickListener {
            viewModel.getCart()
        }
    }

    private fun observeCart() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility =
                if (state is CartUiState.Loading) View.VISIBLE else View.GONE

            binding.layoutError.root.visibility =
                if (state is CartUiState.Error) View.VISIBLE else View.GONE
            binding.recyclerCart.visibility =
                if (state is CartUiState.Error) View.GONE else View.VISIBLE

            when (state) {
                is CartUiState.Loading -> {
                    binding.layoutEmpty.root.visibility = View.GONE
                    binding.cartSummary.visibility = View.GONE
                }

                is CartUiState.Empty -> {
                    binding.layoutEmpty.root.visibility = View.VISIBLE
                    binding.layoutEmpty.tvEmptyTitle.text = getString(R.string.cart_empty_title)
                    binding.layoutEmpty.tvEmptySubtitle.text = getString(R.string.cart_empty_subtitle)
                    binding.layoutEmpty.btnEmptyAction.text = getString(R.string.action_browse_products)
                    binding.layoutEmpty.ivEmptyIcon.setImageResource(R.drawable.cart_icon)
                    binding.cartSummary.visibility = View.GONE
                    cartAdapter.submitList(emptyList())
                }

                is CartUiState.Content -> {
                    binding.layoutEmpty.root.visibility = View.GONE
                    binding.cartSummary.visibility = View.VISIBLE

                    cartAdapter.submitList(state.cart.products)
                    binding.tvTotalPrice.text = formatMoney(requireContext(), state.cart.totalPrice)
                    binding.tvItemsCount.text = resources.getQuantityString(
                        R.plurals.items_count,
                        state.cart.numberOfItems,
                        state.cart.numberOfItems
                    )
                }

                is CartUiState.Error -> {
                    binding.layoutEmpty.root.visibility = View.GONE
                    binding.cartSummary.visibility = View.GONE
                    binding.layoutError.tvErrorTitle.text = getString(state.messageRes)
                    binding.layoutError.tvErrorSubtitle.text = getString(R.string.error_loading_subtitle)
                }
            }
        }

        viewModel.pendingProductIds.observe(viewLifecycleOwner) {
            cartAdapter.setPendingProductIds(it)
        }

        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                is CartEvent.ItemRemoved -> {
                    Snackbar.make(binding.root, R.string.item_removed_from_cart, Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_undo) {
                            viewModel.restoreItem(event.productId, event.count)
                        }
                        .show()
                }

                is CartEvent.Error -> {
                    Snackbar.make(binding.root, event.messageRes, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
