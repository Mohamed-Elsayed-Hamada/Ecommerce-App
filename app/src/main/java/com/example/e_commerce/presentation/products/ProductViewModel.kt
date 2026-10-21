package com.example.e_commerce.presentation.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.R
import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.util.SingleLiveEvent
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.cart_wishlist.WishlistRepository
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.domain.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: ProductsRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private var currentPage = 1
    private var currentFilter = ProductsRequest(limit = PAGE_SIZE)
    private var requestGeneration = 0
    private var hasLoadedOnce = false

    val isInitialLoading = MutableLiveData(false)
    val isPaginationLoading = MutableLiveData(false)
    val isLastPage = MutableLiveData(false)

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _activeFilter = MutableLiveData(currentFilter)
    val activeFilter: LiveData<ProductsRequest> = _activeFilter

    private val _favoriteIds = MutableLiveData<Set<String>>(emptySet())
    val favoriteIds: LiveData<Set<String>> = _favoriteIds

    private val _message = SingleLiveEvent<Int>()
    val message: LiveData<Int> = _message

    fun getProducts() {
        if (isInitialLoading.value == true ||
            isPaginationLoading.value == true ||
            isLastPage.value == true
        ) {
            return
        }

        val page = currentPage
        val generation = requestGeneration

        if (page == 1) {
            isInitialLoading.value = true
        } else {
            isPaginationLoading.value = true
        }

        val request = currentFilter.copy(page = page)

        viewModelScope.launch {
            val result = repo.getProducts(request)

            if (generation != requestGeneration) {
                return@launch
            }

            when (result) {
                is NetworkResult.Success -> {
                    val mappedProducts = result.data.data.map { item ->
                        Product(
                            id = item.id,
                            title = item.title,
                            price = item.price,
                            imageCover = item.imageCover,
                            rating = item.ratingsAverage
                        )
                    }

                    val currentList = if (page == 1) emptyList() else _products.value.orEmpty()
                    _products.value = currentList + mappedProducts
                    _isError.value = false
                    hasLoadedOnce = true

                    val numberOfPages = result.data.metadata.numberOfPages
                    if (numberOfPages <= page) {
                        isLastPage.value = true
                    } else {
                        currentPage++
                    }
                }

                is NetworkResult.HttpError -> {
                    if (page == 1) {
                        _isError.value = true
                    } else {
                        _message.value = R.string.error_load_more
                    }
                }

                is NetworkResult.NetworkError -> {
                    if (page == 1) {
                        _isError.value = true
                    } else {
                        _message.value = R.string.error_load_more
                    }
                }
            }

            isInitialLoading.value = false
            isPaginationLoading.value = false
        }
    }

    fun hasLoaded(): Boolean = hasLoadedOnce

    fun retry() {
        requestGeneration++
        currentPage = 1
        isLastPage.value = false
        isInitialLoading.value = false
        isPaginationLoading.value = false
        _isError.value = false
        _products.value = emptyList()
        getProducts()
    }

    fun applySort(sort: String?) {
        applyFilters(sort = sort)
    }

    fun applyFilters(
        keyword: String? = currentFilter.keyword,
        brand: String? = currentFilter.brand,
        minPrice: Int? = currentFilter.minPrice,
        maxPrice: Int? = currentFilter.maxPrice,
        sort: String? = currentFilter.sort,
        categories: List<String>? = currentFilter.categories
    ) {
        val newFilter = ProductsRequest(
            limit = PAGE_SIZE,
            page = 1,
            keyword = keyword?.trim()?.takeIf { it.isNotBlank() },
            brand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            categories = categories
        )

        if (newFilter == currentFilter && hasLoadedOnce) {
            return
        }

        currentFilter = newFilter
        resetAndReload()
    }

    /** Seeds the screen from Home navigation arguments. */
    fun seedFilters(keyword: String?, brandId: String?, categoryId: String?) {
        if (keyword.isNullOrBlank() && brandId.isNullOrBlank() && categoryId.isNullOrBlank()) {
            getProducts()
            return
        }

        applyFilters(
            keyword = keyword,
            brand = brandId,
            categories = categoryId?.let { listOf(it) }
        )
    }

    fun clearFilters() {
        applyFilters(keyword = null, brand = null, minPrice = null, maxPrice = null, sort = null, categories = null)
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            _message.value = when (cartRepository.addToCart(productId)) {
                is NetworkResult.Success -> R.string.item_added_to_cart
                else -> R.string.error_add_to_cart
            }
        }
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            when (val result = wishlistRepository.getWishlist()) {
                is NetworkResult.Success -> _favoriteIds.value = result.data.map { it.id }.toSet()
                else -> Unit
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val current = _favoriteIds.value.orEmpty()
        val isFavorite = productId in current
        _favoriteIds.value = if (isFavorite) current - productId else current + productId

        viewModelScope.launch {
            val result = if (isFavorite) {
                wishlistRepository.removeFromWishlist(productId)
            } else {
                wishlistRepository.addToWishlist(productId)
            }

            if (result !is NetworkResult.Success) {
                _favoriteIds.value = current
                _message.value = R.string.error_wishlist_update
            }
        }
    }

    private fun resetAndReload() {
        requestGeneration++
        currentPage = 1
        isLastPage.value = false
        isInitialLoading.value = false
        isPaginationLoading.value = false
        _isError.value = false
        _products.value = emptyList()
        _activeFilter.value = currentFilter
        getProducts()
    }

    private companion object {
        const val PAGE_SIZE = 16
    }
}
