package com.example.e_commerce.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.databinding.FragmentProductsBinding
import com.example.e_commerce.presentation.brands.BrandAdapter
import com.example.e_commerce.presentation.category.CategoryAdapter
import com.example.e_commerce.presentation.products.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel : HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit  var categoryAdapter : CategoryAdapter
    private lateinit var brandAdapter : BrandAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryAdapter()
        brandAdapter = BrandAdapter()
        productAdapter = ProductAdapter()
        binding.rvCategories.adapter = categoryAdapter
        binding.rvBrands.adapter = brandAdapter
        binding.rvLatestProducts.adapter = productAdapter

        productAdapter.onItemClick = { productId ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(productId)

            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(action)
            }
        }
        productAdapter.onItemAddToCartClick = { productId ->
            viewModel.addProductToCart(productId)
        }

        binding.imgCart.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCartFragment()
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(action)
            }
        }
        setUpObservers()
        viewModel.fetchData()
    }
    fun setUpObservers(){
        viewModel.categories.observe(viewLifecycleOwner){
            categoryAdapter.categoryList = it.toMutableList()
            categoryAdapter.notifyDataSetChanged()
        }
        viewModel.toastMessage.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.brands.observe(viewLifecycleOwner){
            brandAdapter.brandList = it.toMutableList()
            brandAdapter.notifyDataSetChanged()
        }
        viewModel.products.observe(viewLifecycleOwner){
            productAdapter.productList = it.toMutableList()
            productAdapter.notifyDataSetChanged()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


