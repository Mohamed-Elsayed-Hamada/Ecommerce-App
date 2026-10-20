package com.example.e_commerce.presentation.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.domain.products.models.Brand
import com.example.e_commerce.domain.products.models.Category
import com.example.e_commerce.domain.products.models.Product
import com.example.e_commerce.domain.products.models.ProductDetails
import com.example.e_commerce.domain.products.models.Review
import com.example.e_commerce.domain.products.models.Subcategory
import com.example.e_commerce.domain.products.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    var productId: String = ""
    private val _productCount = MutableLiveData<Int>(0)
    val productCount: LiveData<Int> = _productCount

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _product = MutableLiveData<ProductDetails>()
    val product: LiveData<ProductDetails> = _product

    private val _similarProducts = MutableLiveData<List<Product>>()
    val similarProducts: LiveData<List<Product>> = _similarProducts
    fun fetchData() {
        viewModelScope.launch {
            try {
                val response = repository.getProductDetails(productId)

                response?.let {
                    val mappedProduct = ProductDetails(
                        id = it.id,
                        title = it.title,
                        price = it.price,
                        imageCover = it.imageCover,
                        images = it.images,
                        rating = it.ratingsAverage,
                        ratingsQuantity = it.ratingsQuantity,
                        description = it.description,
                        brand = Brand(it.brand.id, it.brand.name, it.brand.image),
                        category = Category(it.category.id, it.category.name, it.category.image),
                        subcategory = it.subcategory.map { subcategory ->
                            Subcategory(subcategory.id, subcategory.name)
                        },
                        quantity = it.quantity,
                        reviews = it.reviews.map { review ->
                            Review(
                                review.id,
                                review.review,
                                review.rating,
                                User(review.user.id, review.user.name)
                            )
                        }.subList(0,2)
                    )
                    _product.value = mappedProduct
                    _reviews.value = response.reviews.map { review ->
                        Review(
                            review.id,
                            review.review,
                            review.rating,
                            User(review.user.id, review.user.name)
                        )};
                    getProductCount()
                    fetchSimilarProducts(it.category.id)

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    suspend  fun getProductCount(): Int {
        val cartResponse = cartRepository.getUserCart()
            ?: return 0

        val cartProduct = cartResponse.products
            ?.find { it.product.id == productId }
            ?: return 0
        val count = cartProduct.count
        _productCount.value = count
        return count
    }

    fun addToCart(productId: String, count: Int) {
        viewModelScope.launch {
            try {
                val cartResponse = cartRepository.getUserCart()
                    ?: return@launch

                val cartProduct = cartResponse.products
                    ?.find { it.product.id == productId }

                if (cartProduct != null) {

                    val availableQuantity = cartProduct.product.quantity
                    val currentCount = cartProduct.count

                    val newCount = minOf(
                        currentCount + count,
                        availableQuantity
                    )

                    val response = cartRepository.updateCart(
                        productId,
                        newCount
                    )

                    if (response != null) {
                        _productCount.value = newCount
                    }

                } else {

                    val response = cartRepository.addToCart(productId)

                    if (response != null) {

                        if (count > 1) {
                            cartRepository.updateCart(
                                productId,
                                count
                            )
                        }

                        _productCount.value = count
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchSimilarProducts(categoryId: String) {
        viewModelScope.launch {
            try {
                val products = repository.getProducts(categories = listOf(categoryId))

                val currentProductId = product.value?.id

                // Filter out the currently viewed product from the similar list
                val filteredProducts = products?.data?.filter {
                    it.id != currentProductId
                }

                val productList = filteredProducts?.map { item ->
                    Product(
                        id = item.id,
                        title = item.title,
                        price = item.price,
                        imageCover = item.imageCover,
                        rating = item.ratingsAverage,
                    )
                }

                _similarProducts.value = productList ?: emptyList()

            } catch (e: Exception) {
                e.printStackTrace()
                _similarProducts.value = emptyList()
            }
        }
    }
}