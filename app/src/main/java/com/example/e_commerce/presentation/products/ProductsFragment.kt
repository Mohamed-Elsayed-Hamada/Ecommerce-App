package com.example.e_commerce.presentation.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.FragmentProductsBinding
import com.example.e_commerce.presentation.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    val viewModel : ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        adapter = ProductAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setObservers()
        viewModel.getProducts()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
        binding.etSearch.setOnEditorActionListener { _, _, _ ->

            val keyword = binding.etSearch.text
                ?.toString()
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

            viewModel.applyFilters(
                keyword = keyword
            )

            true
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }
    }
    private fun showSortDialog() {

        val options = arrayOf(
            "Default",
            "Price: Low → High",
            "Price: High → Low"
        )

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sort Products")
            .setItems(options) { _, which ->

                when (which) {

                    0 -> {
                        viewModel.applySort(null)
                    }

                    1 -> {
                        viewModel.applySort("price")
                    }

                    2 -> {
                        viewModel.applySort("-price")
                    }
                }
            }
            .show()
    }
    private fun showFilterBottomSheet() {

        val bottomSheet = FilterBottomSheet()

        bottomSheet.onApplyFilters = {
                minPrice,
                maxPrice,
                brand,
                categories ->

            viewModel.applyFilters(
                minPrice = minPrice,
                maxPrice = maxPrice,
                brand = brand,
                categories = categories
            )
        }

        bottomSheet.show(
            childFragmentManager,
            "FilterBottomSheet"
        )
    }
    fun setupRecyclerView(){
        binding.rvProducts.adapter = adapter
        adapter.onItemClick = { productId ->
            val action = ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(productId)

            if (findNavController().currentDestination?.id == com.example.e_commerce.R.id.productsFragment) {
                findNavController().navigate(action)
            }
        }
        binding.rvProducts.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {

                    super.onScrolled(
                        recyclerView,
                        dx,
                        dy
                    )

                    val layoutManager =
                        recyclerView.layoutManager
                                as LinearLayoutManager

                    val lastVisibleItem =
                        layoutManager.findLastVisibleItemPosition()

                    val totalItemCount =
                        layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 3) {
                        viewModel.getProducts()

                    }
//                    if (lastVisibleItem >= totalItemCount - 1) {
//                        binding.lastPage.visibility = View.VISIBLE
//
//                    }else{
//                        binding.paginationProgressBar.visibility = View.GONE
//
//                    }
                }
            }
        )
    }
    fun setObservers() {
        viewModel.products.observe(viewLifecycleOwner) { productsList ->
            adapter.productList =  productsList.toMutableList()
            adapter.notifyDataSetChanged()
        }
        viewModel.isInitialLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.initialProgressBar.visibility = View.VISIBLE
            } else {
                binding.initialProgressBar.visibility = View.GONE
            }
        }
        viewModel.isPaginationLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.paginationProgressBar.visibility = View.VISIBLE
            } else {
                binding.paginationProgressBar.visibility = View.GONE
            }
        }
        viewModel.isLastPage.observe(viewLifecycleOwner) { isLastPage ->
            if (isLastPage) {
                binding.lastPage.visibility = View.VISIBLE
            } else {
                binding.lastPage.visibility = View.GONE
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}