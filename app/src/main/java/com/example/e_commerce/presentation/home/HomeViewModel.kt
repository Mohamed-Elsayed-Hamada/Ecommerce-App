package com.example.e_commerce.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.R
import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.PagingRequest
import com.example.e_commerce.core.SessionManager
import com.example.e_commerce.core.util.SingleLiveEvent
import com.example.e_commerce.data.brands.BrandRepository
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.cart_wishlist.WishlistRepository
import com.example.e_commerce.data.category.CategoryRepository
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.domain.brands.Brand
import com.example.e_commerce.domain.category.Category
import com.example.e_commerce.domain.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productsRepository: ProductsRepository,
    private val brandRepository: BrandRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>(emptyList())
    val categories: LiveData<List<Category>> = _categories

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _brands = MutableLiveData<List<Brand>>(emptyList())
    val brands: LiveData<List<Brand>> = _brands

    private val _favoriteIds = MutableLiveData<Set<String>>(emptySet())
    val favoriteIds: LiveData<Set<String>> = _favoriteIds

    private val _message = SingleLiveEvent<Int>()
    val message: LiveData<Int> = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private var favoritesLoaded = false
    private var hasLoadedOnce = false

    fun addProductToCart(productId: String) {
        viewModelScope.launch {
            _message.value = when (cartRepository.addToCart(productId)) {
                is NetworkResult.Success -> R.string.item_added_to_cart
                else -> R.string.error_add_to_cart
            }
        }
    }

    fun toggleFavorite(product: Product) {
        val current = _favoriteIds.value.orEmpty()
        val isFavorite = product.id in current
        val updated = if (isFavorite) current - product.id else current + product.id
        _favoriteIds.value = updated

        viewModelScope.launch {
            val result = if (isFavorite) {
                wishlistRepository.removeFromWishlist(product.id)
            } else {
                wishlistRepository.addToWishlist(product.id)
            }

            if (result !is NetworkResult.Success) {
                _favoriteIds.value = current
                _message.value = R.string.error_wishlist_update
            }
        }
    }

    fun logout() {
        sessionManager.logout()
    }

    fun fetchData(force: Boolean = false) {
        if (!force && (hasLoadedOnce || _isLoading.value == true)) return

        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            val categoriesDeferred = async {
                categoryRepository.getCategories(PagingRequest(limit = HOME_LIMIT, page = 1))
            }
            val productsDeferred = async {
                productsRepository.getProducts(ProductsRequest(limit = HOME_LIMIT, page = 1))
            }
            val brandsDeferred = async {
                brandRepository.getBrands(PagingRequest(limit = HOME_LIMIT, page = 1))
            }

            val categoriesResult = categoriesDeferred.await()
            val productsResult = productsDeferred.await()
            val brandsResult = brandsDeferred.await()

            if (categoriesResult is NetworkResult.Success) {
                _categories.value = categoriesResult.data.data.map { item ->
                    Category(id = item.id, name = item.name, image = item.image)
                }
            }

            if (productsResult is NetworkResult.Success) {
                _products.value = productsResult.data.data.map { item ->
                    Product(
                        id = item.id,
                        title = item.title,
                        price = item.price,
                        imageCover = item.imageCover,
                        rating = item.ratingsAverage
                    )
                }
            }

            if (brandsResult is NetworkResult.Success) {
                _brands.value = brandsResult.data.data.map { item ->
                    Brand(id = item.id, name = item.name, image = item.image)
                }
            }

            _isError.value = categoriesResult !is NetworkResult.Success &&
                productsResult !is NetworkResult.Success &&
                brandsResult !is NetworkResult.Success

            _isLoading.value = false
            hasLoadedOnce = true

            if (!favoritesLoaded) {
                favoritesLoaded = true
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            when (val result = wishlistRepository.getWishlist()) {
                is NetworkResult.Success -> _favoriteIds.value = result.data.map { it.id }.toSet()
                else -> Unit
            }
        }
    }

    private companion object {
        const val HOME_LIMIT = 10
    }
}
