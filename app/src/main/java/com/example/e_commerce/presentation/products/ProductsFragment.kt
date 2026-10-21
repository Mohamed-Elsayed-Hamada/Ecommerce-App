package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.core.util.navigateSafely
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.databinding.FragmentProductsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        adapter = ProductAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setObservers()
        setupListeners()
        seedFromNavigation()

        viewModel.refreshFavorites()
    }

    private fun seedFromNavigation() {
        // Returning from details / rotation must not fetch the current page again.
        if (viewModel.hasLoaded()) return

        val args = arguments?.let { ProductsFragmentArgs.fromBundle(it) }
        if (args == null) {
            viewModel.getProducts()
            return
        }
        viewModel.seedFilters(
            keyword = args.keyword,
            brandId = args.brandId,
            categoryId = args.categoryId
        )
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }

        childFragmentManager.setFragmentResultListener(
            FilterBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val minPrice = result.getInt(FilterBottomSheet.ARG_MIN_PRICE, FilterBottomSheet.NO_PRICE)
                .takeIf { it != FilterBottomSheet.NO_PRICE }
            val maxPrice = result.getInt(FilterBottomSheet.ARG_MAX_PRICE, FilterBottomSheet.NO_PRICE)
                .takeIf { it != FilterBottomSheet.NO_PRICE }

            // Brand/category are seeded from Home; the sheet only edits price.
            viewModel.applyFilters(minPrice = minPrice, maxPrice = maxPrice)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.applyFilters(keyword = currentKeyword())
                true
            } else {
                false
            }
        }

        binding.searchLayout.setEndIconOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.applyFilters(keyword = null)
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }

        binding.layoutEmpty.btnEmptyAction.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearFilters()
        }

        binding.layoutError.btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun currentKeyword(): String? =
        binding.etSearch.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }

    private fun showSortDialog() {
        val options = arrayOf(
            getString(R.string.sort_default),
            getString(R.string.sort_price_low_high),
            getString(R.string.sort_price_high_low)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.sort_products_title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.applySort(null)
                    1 -> viewModel.applySort("price")
                    2 -> viewModel.applySort("-price")
                }
            }
            .show()
    }

    private fun showFilterBottomSheet() {
        FilterBottomSheet().show(
            childFragmentManager,
            "FilterBottomSheet"
        )
    }

    private fun setupRecyclerView() {
        binding.rvProducts.adapter = adapter

        adapter.onItemClick = { productId ->
            navigateSafely(
                ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(productId),
                R.id.productsFragment
            )
        }

        adapter.onItemAddToCartClick = { productId ->
            viewModel.addToCart(productId)
        }

        adapter.onItemFavoriteClick = { product ->
            viewModel.toggleFavorite(product.id)
        }

        binding.rvProducts.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    if (lastVisibleItem >= totalItemCount - 4 && totalItemCount > 0) {
                        viewModel.getProducts()
                    }
                }
            }
        )
    }

    private fun setObservers() {
        viewModel.products.observe(viewLifecycleOwner) { productsList ->
            adapter.submitList(productsList)

            val isEmpty = productsList.isEmpty() &&
                viewModel.isInitialLoading.value != true &&
                viewModel.isError.value != true
            binding.layoutEmpty.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.layoutEmpty.tvEmptyTitle.text = getString(R.string.empty_products_title)
            binding.layoutEmpty.tvEmptySubtitle.text = getString(R.string.empty_products_subtitle)
            binding.layoutEmpty.btnEmptyAction.text = getString(R.string.action_clear)
        }

        viewModel.favoriteIds.observe(viewLifecycleOwner) {
            adapter.setFavoriteIds(it)
        }

        viewModel.message.observe(viewLifecycleOwner) { messageRes ->
            Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isInitialLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.initialProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.layoutEmpty.root.visibility = View.GONE
            }
        }

        viewModel.isPaginationLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.paginationProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isLastPage.observe(viewLifecycleOwner) { isLastPage ->
            binding.lastPage.visibility = if (isLastPage) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.layoutError.root.visibility = if (isError) View.VISIBLE else View.GONE
            binding.rvProducts.visibility = if (isError) View.GONE else View.VISIBLE
            if (isError) {
                binding.layoutEmpty.root.visibility = View.GONE
            }
        }

        viewModel.activeFilter.observe(viewLifecycleOwner) { filter ->
            binding.tvFilterStatus.text = buildFilterStatus(filter)
        }
    }

    private fun buildFilterStatus(filter: ProductsRequest): String {
        val parts = mutableListOf<String>()

        filter.keyword?.let {
            parts += getString(R.string.filter_status_search, it)
        }
        filter.brand?.let {
            parts += getString(R.string.filter_status_brand)
        }
        filter.categories?.takeIf { it.isNotEmpty() }?.let {
            parts += getString(R.string.filter_status_category)
        }
        when {
            filter.minPrice != null && filter.maxPrice != null -> parts += getString(
                R.string.filter_status_price_range,
                filter.minPrice,
                filter.maxPrice
            )

            filter.minPrice != null -> parts += getString(
                R.string.filter_status_price_from,
                filter.minPrice
            )

            filter.maxPrice != null -> parts += getString(
                R.string.filter_status_price_up_to,
                filter.maxPrice
            )
        }
        when (filter.sort) {
            "price" -> parts += getString(R.string.sort_price_low_high)
            "-price" -> parts += getString(R.string.sort_price_high_low)
        }

        return if (parts.isEmpty()) {
            getString(R.string.all_products)
        } else {
            parts.joinToString(" · ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
