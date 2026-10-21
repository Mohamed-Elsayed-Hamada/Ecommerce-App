package com.example.e_commerce.presentation.cart_wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.core.util.navigateSafely
import com.example.e_commerce.databinding.FragmentWishlistBinding
import com.example.e_commerce.presentation.products.ProductAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WishlistViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter()
        binding.rvWishlist.adapter = adapter

        adapter.onItemClick = { productId ->
            navigateSafely(
                WishlistFragmentDirections.actionWishlistFragmentToProductDetailsFragment(productId),
                R.id.wishlistFragment
            )
        }

        adapter.onItemFavoriteClick = { product ->
            viewModel.remove(product.id)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutEmpty.btnEmptyAction.setOnClickListener {
            navigateSafely(
                WishlistFragmentDirections.actionWishlistFragmentToProductsFragment(),
                R.id.wishlistFragment
            )
        }

        binding.layoutError.btnRetry.setOnClickListener {
            viewModel.load()
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            adapter.setFavoriteIds(products.map { it.id }.toSet())

            val isEmpty = products.isEmpty() &&
                viewModel.isError.value != true &&
                viewModel.isLoading.value != true
            binding.layoutEmpty.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.layoutEmpty.tvEmptyTitle.text = getString(R.string.wishlist_empty_title)
            binding.layoutEmpty.tvEmptySubtitle.text = getString(R.string.wishlist_empty_subtitle)
            binding.layoutEmpty.btnEmptyAction.text = getString(R.string.action_browse_products)
            binding.layoutEmpty.ivEmptyIcon.setImageResource(R.drawable.ic_favorite_filled)
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.layoutError.root.visibility = if (isError) View.VISIBLE else View.GONE
            if (isError) {
                binding.layoutEmpty.root.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.layoutEmpty.root.visibility = View.GONE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { messageRes ->
            Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
