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
import com.example.e_commerce.data.cart_wishlist.models.CartResponseData
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.domain.products.models.Brand
import com.example.e_commerce.domain.products.models.Category
import com.example.e_commerce.domain.products.models.Product
import com.example.e_commerce.domain.products.models.ProductDetails
import com.example.e_commerce.domain.products.models.Review
import com.example.e_commerce.domain.products.models.Subcategory
import com.example.e_commerce.domain.products.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class CartLineUiState(
    val count: Int = 0,
    val maxQuantity: Int = 0,
    val isPending: Boolean = false
)

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    var productId: String = ""

    private val _product = MutableLiveData<ProductDetails>()
    val product: LiveData<ProductDetails> = _product

    private val _reviews = MutableLiveData<List<Review>>(emptyList())
    val reviews: LiveData<List<Review>> = _reviews

    private val _similarProducts = MutableLiveData<List<Product>>(emptyList())
    val similarProducts: LiveData<List<Product>> = _similarProducts

    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int> = _quantity

    private val _cartLine = MutableLiveData(CartLineUiState())
    val cartLine: LiveData<CartLineUiState> = _cartLine

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _message = SingleLiveEvent<Int>()
    val message: LiveData<Int> = _message

    private val mutationMutex = Mutex()
    private var hasLoadedOnce = false

    fun fetchData() {
        if (productId.isBlank() || hasLoadedOnce) return

        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            when (val result = repository.getProductDetails(productId)) {
                is NetworkResult.Success -> {
                    val details = result.data
                    _product.value = details.toDomain()

                    _reviews.value = details.reviews.map { review ->
                        Review(
                            review.id,
                            review.review,
                            review.rating,
                            User(review.user.id, review.user.name)
                        )
                    }

                    _quantity.value = 1
                    _isError.value = false
                    hasLoadedOnce = true
                    loadCartLine()
                    loadFavorite()
                    fetchSimilarProducts(details.category.id)
                }

                is NetworkResult.HttpError -> _isError.value = true
                is NetworkResult.NetworkError -> _isError.value = true
            }

            _isLoading.value = false
        }
    }

    /** Used by the reviews screen: only the reviews are needed, no JSON through nav args. */
    fun fetchReviews(id: String) {
        productId = id
        viewModelScope.launch {
            when (val result = repository.getProductDetails(id)) {
                is NetworkResult.Success -> {
                    _reviews.value = result.data.reviews.map { review ->
                        Review(
                            review.id,
                            review.review,
                            review.rating,
                            User(review.user.id, review.user.name)
                        )
                    }
                }

                else -> _reviews.value = emptyList()
            }
        }
    }

    fun setQuantity(quantity: Int) {
        val max = maxQuantity()
        _quantity.value = quantity.coerceIn(1, max.coerceAtLeast(1))
    }

    fun setCartCount(count: Int) {
        mutateCart(productId, count, updateLineState = true)
    }

    fun addProductToCart(productId: String, count: Int = 1) {
        mutateCart(productId, count, updateLineState = false)
    }

    fun toggleFavorite() {
        val newValue = _isFavorite.value != true
        _isFavorite.value = newValue

        viewModelScope.launch {
            val result = if (newValue) {
                wishlistRepository.addToWishlist(productId)
            } else {
                wishlistRepository.removeFromWishlist(productId)
            }

            if (result !is NetworkResult.Success) {
                _isFavorite.value = !newValue
                _message.value = R.string.error_wishlist_update
            }
        }
    }

    /** Used by the similar-products rail, where the header favourite state does not apply. */
    fun addToWishlist(productId: String) {
        viewModelScope.launch {
            _message.value = if (wishlistRepository.addToWishlist(productId) is NetworkResult.Success) {
                R.string.item_saved_to_wishlist
            } else {
                R.string.error_wishlist_update
            }
        }
    }

    private fun loadCartLine() {
        viewModelScope.launch {
            when (val result = cartRepository.getUserCart()) {
                is NetworkResult.Success -> applyCartLine(result.data)
                else -> Unit
            }
        }
    }

    private fun applyCartLine(data: CartResponseData) {
        val line = data.products.orEmpty().firstOrNull {
            it.product.id == productId || it.product._id == productId
        }
        val count = line?.count ?: 0
        val max = line?.product?.quantity ?: _product.value?.quantity ?: 0
        _cartLine.value = CartLineUiState(count = count, maxQuantity = max, isPending = false)

        if (count > 0) {
            _quantity.value = count
        }
    }

    private fun loadFavorite() {
        viewModelScope.launch {
            when (val result = wishlistRepository.getWishlist()) {
                is NetworkResult.Success -> {
                    _isFavorite.value = result.data.any { it.id == productId }
                }

                else -> Unit
            }
        }
    }

    private fun mutateCart(productId: String, count: Int, updateLineState: Boolean) {
        if (productId.isBlank()) return

        viewModelScope.launch {
            mutationMutex.withLock {
                if (updateLineState) {
                    _cartLine.value = _cartLine.value?.copy(isPending = true)
                }

                val currentCart = cartRepository.getUserCart()
                val result = when (currentCart) {
                    is NetworkResult.Success -> {
                        val existing = currentCart.data.products.orEmpty().firstOrNull {
                            it.product.id == productId || it.product._id == productId
                        }

                        if (existing != null) {
                            cartRepository.updateCart(productId, count)
                        } else {
                            when (val added = cartRepository.addToCart(productId)) {
                                is NetworkResult.Success -> {
                                    if (count > 1) {
                                        cartRepository.updateCart(productId, count)
                                    } else {
                                        added
                                    }
                                }

                                else -> added
                            }
                        }
                    }

                    is NetworkResult.HttpError -> currentCart
                    is NetworkResult.NetworkError -> currentCart
                }

                when (result) {
                    is NetworkResult.Success -> {
                        if (updateLineState) {
                            val line = result.data.products.orEmpty().firstOrNull {
                                it.product.id == productId || it.product._id == productId
                            }
                            val newCount = line?.count ?: count
                            _cartLine.value = CartLineUiState(
                                count = newCount,
                                maxQuantity = line?.product?.quantity ?: maxQuantity(),
                                isPending = false
                            )
                            _quantity.value = newCount
                        }
                        _message.value = R.string.item_added_to_cart
                    }

                    else -> {
                        if (updateLineState) {
                            _cartLine.value = _cartLine.value?.copy(isPending = false)
                        }
                        _message.value = R.string.error_add_to_cart
                    }
                }
            }
        }
    }

    private fun fetchSimilarProducts(categoryId: String) {
        viewModelScope.launch {
            when (val result = repository.getProducts(
                queries = ProductsRequest(
                    limit = SIMILAR_PRODUCTS_LIMIT,
                    page = 1
                ),
                categories = listOf(categoryId)
            )) {
                is NetworkResult.Success -> {
                    val currentProductId = _product.value?.id
                    _similarProducts.value = result.data.data
                        .filter { it.id != currentProductId }
                        .map { item ->
                            Product(
                                id = item.id,
                                title = item.title,
                                price = item.price,
                                imageCover = item.imageCover,
                                rating = item.ratingsAverage
                            )
                        }
                }

                else -> _similarProducts.value = emptyList()
            }
        }
    }

    private fun maxQuantity(): Int =
        _cartLine.value?.maxQuantity?.takeIf { it > 0 }
            ?: _product.value?.quantity
            ?: 1

    fun maxQuantityForStepper(): Int = maxQuantity()

    /** Consume the transient load-error event so rotation does not replay the snackbar. */
    fun consumeError() {
        if (_isError.value == true) _isError.value = false
    }

    private companion object {
        const val SIMILAR_PRODUCTS_LIMIT = 10
    }

    private fun com.example.e_commerce.data.products.models.ProductDataDetails.toDomain() =
        ProductDetails(
            id = id,
            title = title,
            price = price,
            imageCover = imageCover,
            images = images,
            rating = ratingsAverage,
            ratingsQuantity = ratingsQuantity,
            description = description,
            brand = Brand(brand.id, brand.name, brand.image),
            category = Category(category.id, category.name, category.image),
            subcategory = subcategory.map { Subcategory(it.id, it.name) },
            quantity = quantity,
            reviews = reviews.take(2).map { review ->
                Review(
                    review.id,
                    review.review,
                    review.rating,
                    User(review.user.id, review.user.name)
                )
            }
        )
}
