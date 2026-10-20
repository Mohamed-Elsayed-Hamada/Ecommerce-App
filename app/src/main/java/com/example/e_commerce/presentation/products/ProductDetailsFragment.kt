package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.e_commerce.domain.products.models.Review
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailsViewModel by viewModels()
    private lateinit var similarProductsAdapter: ProductAdapter
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var reviewsAdapter: ReviewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId =
            ProductDetailsFragmentArgs
                .fromBundle(requireArguments())
                .id

        viewModel.productId = productId
        similarProductsAdapter = ProductAdapter()
        binding.recyclerSimilarProducts.adapter = similarProductsAdapter
        similarProductsAdapter.onItemClick = { id ->
            val action =
                ProductDetailsFragmentDirections
                    .actionProductDetailsFragmentSelf(id)
            findNavController().navigate(action)
        }
        reviewsAdapter = ReviewAdapter()
        binding.recyclerReviews.adapter = reviewsAdapter
        productImageAdapter = ProductImageAdapter()

        setupClicks()
        setupObservers()

        viewModel.fetchData()
    }
    private fun setUpslider( images : List<String>) {
        productImageAdapter.submitList(images)
        binding.productImageSlider.adapter = productImageAdapter

        binding.productImageSlider.orientation =
            androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
    }
    private fun setupObservers() {
        viewModel.productCount.observe(viewLifecycleOwner){
            binding.tvQuantity.text = it.toString()
        }
        viewModel.similarProducts.observe(viewLifecycleOwner){
            similarProductsAdapter.updateData(it)
        }
        viewModel.reviews.observe(viewLifecycleOwner){
            if (it.count() >2)
                binding.btnViewAllReviews.visibility = View.VISIBLE
        }
        viewModel.product.observe(viewLifecycleOwner) { product ->

            // Basic information
            binding.tvProductTitle.text = product.title

            binding.tvBrand.text = product.brand.name

            binding.tvPrice.text =
                "${product.price} EGP"

            binding.tvDescription.text =
                product.description


            // Rating
            binding.tvRating.text =
                "⭐ ${product.rating}"

            binding.tvReviewsCount.text =
                "(${product.ratingsQuantity} reviews)"


            // Product information
            binding.tvCategory.text =
                "Category: ${product.category.name}"

            binding.tvSubCategory.text =
                "Subcategory: ${product.subcategory.firstOrNull()?.name ?: "N/A"}"

            binding.tvStock.text =
                "Available: ${product.quantity}"


            // Reviews summary
            binding.tvReviewsRating.text =
                product.rating.toString()

            binding.tvRatingsQuantity.text =
                "${product.ratingsQuantity} ratings"


            // Quantity
            binding.tvQuantity.text = "1"

            reviewsAdapter.submitList(product.reviews)
            setUpslider(product.images)
            setupImageIndicator()
            // TODO:
            // Setup image slider
            // Setup reviews RecyclerView
            // Setup similar products RecyclerView
        }
    }
    private fun setupImageIndicator() {

        TabLayoutMediator(
            binding.imageIndicator,
            binding.productImageSlider
        ) { tab, _ ->
            tab.text = "•"
        }.attach()
    }

    private fun setupClicks() {
        binding.btnViewAllReviews.setOnClickListener {

            val reviewsJson = Gson().toJson(
                viewModel.reviews.value ?: emptyList<Review>()
            )

            val action =
                ProductDetailsFragmentDirections
                    .actionProductDetailsFragmentToReviewsFragment(
                        reviewsJson
                    )

            findNavController().navigate(action)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFavorite.setOnClickListener {
            // TODO: Add/remove product from wishlist
        }

        binding.btnDecrease.setOnClickListener {
            decreaseQuantity()
        }

        binding.btnIncrease.setOnClickListener {
            increaseQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
           val count = binding.tvQuantity.text.toString().toInt()
            viewModel.addToCart(viewModel.productId, count)
        }
    }

    private fun decreaseQuantity() {

        val currentQuantity =
            binding.tvQuantity.text
                .toString()
                .toInt()

        if (currentQuantity > 1) {
            binding.tvQuantity.text =
                (currentQuantity - 1).toString()
        }
    }

    private fun increaseQuantity() {

        val currentQuantity =
            binding.tvQuantity.text
                .toString()
                .toInt()

        val maxQuantity =
            viewModel.product.value?.quantity ?: 1

        if (currentQuantity < maxQuantity) {
            binding.tvQuantity.text =
                (currentQuantity + 1).toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}