package com.example.e_commerce.presentation.home

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.brands.BrandRepository
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.category.CategoryRepository
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.domain.brands.Brand
import com.example.e_commerce.domain.category.Category
import com.example.e_commerce.domain.products.models.Product
import android.app.Application
import android.util.Log
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productsRepository: ProductsRepository,
    private val brandRepository: BrandRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _brands = MutableLiveData<List<Brand>>()
    val brands: LiveData<List<Brand>> = _brands
    fun addProductToCart(productId : String){
        viewModelScope.launch {
            try {
                val response = cartRepository.addToCart(productId)
//                Toast.makeText(getApplication(), response.toString(), Toast.LENGTH_SHORT).show()
                _toastMessage.value =response.toString()
                Log.d("addProductToCart", response.toString())

            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
    fun fetchData() {
        viewModelScope.launch {
            try {
                val categoriesResponse = categoryRepository.getCategories()
                categoriesResponse?.let {
                    val mappedCategories = categoriesResponse.data.map { item ->
                        Category(
                            id = item.id,
                            name = item.name,
                            image = item.image
                        )
                    }
                    _categories.value = mappedCategories
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val productsResponse = productsRepository.getProducts()
                productsResponse?.let {
                    val mappedProducts = productsResponse.data.map { item ->
                        Product(
                            id = item.id,
                            title = item.title,
                            price = item.price,
                            imageCover = item.imageCover,
                            rating = item.ratingsAverage
                        )
                    }
                    _products.value = mappedProducts
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModelScope.launch {
                try {
                    val brandResponse = brandRepository.getBrands()
                    brandResponse?.let {
                        val mappedBrands = brandResponse.data.map { item ->
                            Brand(
                                id = item.id,
                                name = item.name,
                                image = item.image
                            )
                        }
                        _brands.value = mappedBrands
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
