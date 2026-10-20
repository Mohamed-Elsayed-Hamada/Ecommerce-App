package com.example.e_commerce.presentation.products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.domain.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Int

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: ProductsRepository
) : ViewModel() {

    private var currentPage = 1

    private var currentFilter = ProductsRequest(
        limit = 16
    )

    val isInitialLoading = MutableLiveData(false)
    val isPaginationLoading = MutableLiveData(false)
    val isLastPage = MutableLiveData(false)

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    fun getProducts() {

        if (isInitialLoading.value == true ||
            isPaginationLoading.value == true ||
            isLastPage.value == true
        ) {
            return
        }

        if (currentPage == 1) {
            isInitialLoading.value = true
        } else {
            isPaginationLoading.value = true
        }

        val request = currentFilter.copy(
            page = currentPage
        )

        viewModelScope.launch {

            try {

                val response = repo.getProducts(request)

                if (response != null) {

                    val mappedProducts = response.data.map { item ->

                        Product(
                            id = item.id,
                            title = item.title,
                            price = item.price,
                            imageCover = item.imageCover,
                            rating = item.ratingsAverage
                        )
                    }

                    val currentList =
                        if (currentPage == 1) {
                            emptyList()
                        } else {
                            _products.value ?: emptyList()
                        }

                    _products.value = currentList + mappedProducts

                    if (response.metadata.numberOfPages <= currentPage) {

                        isLastPage.value = true

                    } else {

                        currentPage++
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "ProductViewModel",
                    "Error loading products",
                    e
                )

            } finally {

                isInitialLoading.value = false
                isPaginationLoading.value = false
            }
        }
    }
    fun applySort(sort: String?) {

        currentFilter = currentFilter.copy(
            sort = sort
        )

        currentPage = 1

        isLastPage.value = false

        _products.value = emptyList()

        getProducts()
    }
    fun applyFilters(
        keyword: String? = currentFilter.keyword,
        brand: String? = currentFilter.brand,
        minPrice: Int? = currentFilter.minPrice,
        maxPrice: Int? = currentFilter.maxPrice,
        sort: String? = currentFilter.sort,
        categories: List<String>? = currentFilter.categories
    ) {

        currentFilter = ProductsRequest(
            limit = 16,
            page = 1,
            keyword = keyword,
            brand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            categories = categories
        )

        currentPage = 1

        isLastPage.value = false
        _products.value?.filter {
            it.title.contains(keyword ?: "")
        }
//        _products.value = emptyList()
//
//        getProducts()
    }

    fun clearFilters() {

        currentFilter = ProductsRequest(
            limit = 16,
            page = 1
        )

        currentPage = 1

        isLastPage.value = false

        _products.value = emptyList()

        getProducts()
    }
//    fun applyFilters(
//        keyword: String? = null,
//        brand: String? = null,
//        minPrice: Int? = null,
//        maxPrice: Int? = null,
//        sort: String? = null,
//        categories: List<String>? = null
//    ) {
//
//        currentFilter = ProductsRequest(
//
//            limit = 16,
//
//            page = 1,
//
//            sort = sort,
//
//            keyword = keyword,
//
//            brand = brand,
//
//            minPrice = minPrice,
//
//            maxPrice = maxPrice,
//
//            categories = categories
//        )
//
//        currentPage = 1
//
//        isLastPage.value = false
//
//        _products.value = emptyList()
//
//        getProducts()
//    }


}