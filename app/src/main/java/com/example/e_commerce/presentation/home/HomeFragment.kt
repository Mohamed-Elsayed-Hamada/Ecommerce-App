package com.example.e_commerce.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce.R
import com.example.e_commerce.core.util.navigateSafely
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.presentation.brands.BrandAdapter
import com.example.e_commerce.presentation.category.CategoryAdapter
import com.example.e_commerce.presentation.products.ProductAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var productAdapter: ProductAdapter

    private val categoryDots = mutableListOf<View>()
    private val categoryPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateCategoryIndicator(position)
            updateCategoryNavigation(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        setupObservers()

        binding.vpCategories.registerOnPageChangeCallback(categoryPageChangeCallback)
        viewModel.fetchData()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter()
        brandAdapter = BrandAdapter()
        productAdapter = ProductAdapter()

        binding.vpCategories.adapter = categoryAdapter
        binding.vpCategories.offscreenPageLimit = 1
        binding.rvBrands.adapter = brandAdapter
        binding.rvLatestProducts.adapter = productAdapter

        productAdapter.onItemClick = { productId ->
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(productId),
                R.id.homeFragment
            )
        }

        productAdapter.onItemAddToCartClick = { productId ->
            viewModel.addProductToCart(productId)
        }

        productAdapter.onItemFavoriteClick = { product ->
            viewModel.toggleFavorite(product)
        }

        categoryAdapter.onItemClick = { categoryId ->
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToProductsFragment(categoryId = categoryId),
                R.id.homeFragment
            )
        }

        brandAdapter.onItemClick = { brandId ->
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToProductsFragment(brandId = brandId),
                R.id.homeFragment
            )
        }
    }

    private fun setupListeners() {
        binding.imgCart.setOnClickListener {
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToCartFragment(),
                R.id.homeFragment
            )
        }

        binding.imgLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnViewAllCategories.setOnClickListener {
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToProductsFragment(),
                R.id.homeFragment
            )
        }

        binding.btnCategoryPrev.setOnClickListener {
            val target = binding.vpCategories.currentItem - 1
            if (target >= 0) {
                binding.vpCategories.setCurrentItem(target, true)
            }
        }

        binding.btnCategoryNext.setOnClickListener {
            val target = binding.vpCategories.currentItem + 1
            if (target < categoryAdapter.itemCount) {
                binding.vpCategories.setCurrentItem(target, true)
            }
        }

        binding.btnViewAllBrands.setOnClickListener {
            navigateSafely(
                HomeFragmentDirections.actionHomeFragmentToProductsFragment(),
                R.id.homeFragment
            )
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.etSearch.text
                    ?.toString()
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }

                navigateSafely(
                    HomeFragmentDirections.actionHomeFragmentToProductsFragment(keyword = keyword),
                    R.id.homeFragment
                )
                true
            } else {
                false
            }
        }

        binding.layoutError.btnRetry.setOnClickListener {
            viewModel.fetchData(force = true)
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories) {
                if (_binding == null) return@submitList

                setupCategoryIndicator(categories.size)

                if (categories.isNotEmpty()) {
                    val target = binding.vpCategories.currentItem.coerceAtMost(categories.lastIndex)
                    if (binding.vpCategories.currentItem != target) {
                        binding.vpCategories.setCurrentItem(target, false)
                    }
                    updateCategoryIndicator(target)
                    updateCategoryNavigation(target)
                } else {
                    updateCategoryNavigation(0)
                }

                syncCategorySectionVisibility()
            }
        }

        viewModel.brands.observe(viewLifecycleOwner) {
            brandAdapter.submitList(it)
        }

        viewModel.products.observe(viewLifecycleOwner) {
            productAdapter.submitList(it)
        }

        viewModel.favoriteIds.observe(viewLifecycleOwner) {
            productAdapter.setFavoriteIds(it)
        }

        viewModel.message.observe(viewLifecycleOwner) { messageRes ->
            Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.groupContent.visibility =
                if (isLoading || viewModel.isError.value == true) View.GONE else View.VISIBLE
            syncCategorySectionVisibility()
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.layoutError.root.visibility = if (isError) View.VISIBLE else View.GONE
            if (isError) {
                binding.groupContent.visibility = View.GONE
            }
            syncCategorySectionVisibility()
        }
    }

    private fun syncCategorySectionVisibility() {
        val contentVisible = viewModel.isLoading.value != true && viewModel.isError.value != true
        binding.categoryCarousel.isVisible = contentVisible && categoryAdapter.itemCount > 0
    }

    private fun setupCategoryIndicator(count: Int) {
        binding.indicatorCategories.removeAllViews()
        categoryDots.clear()

        if (count <= 1) {
            binding.indicatorCategories.isVisible = false
            return
        }

        binding.indicatorCategories.isVisible = true
        val dotSize = resources.getDimensionPixelSize(R.dimen.carousel_dot_size)
        val selectedDotWidth = resources.getDimensionPixelSize(R.dimen.carousel_dot_width_selected)
        val spacing = resources.getDimensionPixelSize(R.dimen.carousel_dot_spacing)

        repeat(count) { index ->
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    if (index == 0) selectedDotWidth else dotSize,
                    dotSize
                ).apply {
                    setMargins(spacing, 0, spacing, 0)
                }
                setBackgroundResource(
                    if (index == 0) {
                        R.drawable.bg_carousel_dot_selected
                    } else {
                        R.drawable.bg_carousel_dot_unselected
                    }
                )
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            binding.indicatorCategories.addView(dot)
            categoryDots += dot
        }
    }

    private fun updateCategoryIndicator(position: Int) {
        if (categoryDots.isEmpty()) return

        val dotSize = resources.getDimensionPixelSize(R.dimen.carousel_dot_size)
        val selectedDotWidth = resources.getDimensionPixelSize(R.dimen.carousel_dot_width_selected)

        categoryDots.forEachIndexed { index, dot ->
            val selected = index == position
            dot.setBackgroundResource(
                if (selected) {
                    R.drawable.bg_carousel_dot_selected
                } else {
                    R.drawable.bg_carousel_dot_unselected
                }
            )
            val params = dot.layoutParams as LinearLayout.LayoutParams
            params.width = if (selected) selectedDotWidth else dotSize
            dot.layoutParams = params
        }
    }

    private fun updateCategoryNavigation(position: Int) {
        val hasPrevious = position > 0
        val hasNext = position < categoryAdapter.itemCount - 1

        binding.btnCategoryPrev.isVisible = hasPrevious
        binding.btnCategoryPrev.isEnabled = hasPrevious
        binding.btnCategoryNext.isVisible = hasNext
        binding.btnCategoryNext.isEnabled = hasNext
    }

    override fun onDestroyView() {
        binding.vpCategories.unregisterOnPageChangeCallback(categoryPageChangeCallback)
        binding.vpCategories.adapter = null
        binding.indicatorCategories.removeAllViews()
        categoryDots.clear()

        super.onDestroyView()
        _binding = null
    }
}
