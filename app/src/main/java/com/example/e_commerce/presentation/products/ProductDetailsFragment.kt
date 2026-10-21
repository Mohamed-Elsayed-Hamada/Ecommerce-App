package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.core.util.formatMoney
import com.example.e_commerce.core.util.formatRating
import com.example.e_commerce.core.util.navigateSafely
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailsViewModel by viewModels()
    private lateinit var similarProductsAdapter: ProductAdapter
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var reviewsAdapter: ReviewAdapter
    private var imageMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = ProductDetailsFragmentArgs.fromBundle(requireArguments()).id
        viewModel.productId = productId

        setupAdapters()
        setupClicks()
        setupObservers()

        viewModel.fetchData()
    }

    private fun setupAdapters() {
        similarProductsAdapter = ProductAdapter()
        binding.recyclerSimilarProducts.adapter = similarProductsAdapter
        similarProductsAdapter.onItemClick = { id ->
            navigateSafely(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentSelf(id),
                R.id.productDetailsFragment
            )
        }
        similarProductsAdapter.onItemAddToCartClick = { id ->
            viewModel.addProductToCart(id)
        }
        similarProductsAdapter.onItemFavoriteClick = { product ->
            viewModel.addToWishlist(product.id)
        }

        reviewsAdapter = ReviewAdapter()
        binding.recyclerReviews.adapter = reviewsAdapter

        productImageAdapter = ProductImageAdapter()
        binding.productImageSlider.adapter = productImageAdapter
        binding.productImageSlider.orientation =
            androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun setupImageIndicator() {
        imageMediator?.detach()
        imageMediator = TabLayoutMediator(binding.imageIndicator, binding.productImageSlider) { tab, _ ->
            tab.text = null
        }.also { it.attach() }
    }

    private fun setupObservers() {
        viewModel.similarProducts.observe(viewLifecycleOwner) {
            similarProductsAdapter.submitList(it)
        }

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            binding.btnViewAllReviews.visibility =
                if (reviews.size > 2) View.VISIBLE else View.GONE
        }

        viewModel.quantity.observe(viewLifecycleOwner) { quantity ->
            binding.tvQuantity.text = getString(R.string.quantity_value_format, quantity)
            updateStepperButtons()
        }

        viewModel.cartLine.observe(viewLifecycleOwner) { line ->
            binding.btnAddToCart.isEnabled =
                !line.isPending && viewModel.isLoading.value != true
            updateStepperButtons()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAddToCart.isEnabled =
                !isLoading && viewModel.cartLine.value?.isPending != true
            updateStepperButtons()
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
        }

        viewModel.message.observe(viewLifecycleOwner) { messageRes ->
            Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Snackbar.make(binding.root, R.string.error_loading_title, Snackbar.LENGTH_LONG).show()
                viewModel.consumeError()
            }
        }

        viewModel.product.observe(viewLifecycleOwner) { product ->
            val context = requireContext()
            binding.tvProductTitle.text = product.title
            binding.tvBrand.text = product.brand.name
            binding.tvPrice.text = formatMoney(context, product.price)
            binding.tvDescription.text = product.description

            binding.tvRating.text = formatRating(product.rating)
            binding.tvReviewsCount.text = resources.getQuantityString(
                R.plurals.reviews_count,
                product.ratingsQuantity,
                product.ratingsQuantity
            )

            binding.tvCategory.text = getString(R.string.label_category, product.category.name)
            binding.tvSubCategory.text = getString(
                R.string.label_subcategory,
                product.subcategory.firstOrNull()?.name ?: getString(R.string.not_available)
            )
            binding.tvStock.text = getString(
                R.string.label_stock_available,
                product.quantity.toString()
            )

            binding.tvReviewsRating.text = formatRating(product.rating)
            binding.tvRatingsQuantity.text = resources.getQuantityString(
                R.plurals.ratings_count,
                product.ratingsQuantity,
                product.ratingsQuantity
            )

            bindRatingStars(product.rating)

            reviewsAdapter.submitList(product.reviews)
            productImageAdapter.submitList(product.images)
            setupImageIndicator()
        }
    }

    private fun bindRatingStars(rating: Double) {
        val stars = listOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )
        stars.forEachIndexed { index, star ->
            val fill = (rating - index).coerceIn(0.0, 1.0).toFloat()
            star.alpha = fill
        }
    }

    private fun setupClicks() {
        binding.btnViewAllReviews.setOnClickListener {
            navigateSafely(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToReviewsFragment(
                    viewModel.productId
                ),
                R.id.productDetailsFragment
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnDecrease.setOnClickListener {
            val current = viewModel.quantity.value ?: 1
            viewModel.setQuantity(current - 1)
        }

        binding.btnIncrease.setOnClickListener {
            val current = viewModel.quantity.value ?: 1
            viewModel.setQuantity(current + 1)
        }

        binding.btnAddToCart.setOnClickListener {
            val count = viewModel.quantity.value ?: 1
            viewModel.setCartCount(count)
        }
    }

    private fun updateStepperButtons() {
        val quantity = viewModel.quantity.value ?: 1
        val isPending = viewModel.cartLine.value?.isPending == true ||
            viewModel.isLoading.value == true
        binding.btnDecrease.isEnabled = !isPending && quantity > 1
        binding.btnIncrease.isEnabled = !isPending && quantity < viewModel.maxQuantityForStepper()
    }

    override fun onDestroyView() {
        imageMediator?.detach()
        imageMediator = null
        super.onDestroyView()
        _binding = null
    }
}
