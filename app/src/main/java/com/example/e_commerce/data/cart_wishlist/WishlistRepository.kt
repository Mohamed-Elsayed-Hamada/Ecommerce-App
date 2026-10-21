package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.cart_wishlist.models.AddToWishlistRequest
import com.example.e_commerce.data.cart_wishlist.models.WishlistActionResponse
import com.example.e_commerce.data.cart_wishlist.models.WishlistResponse
import com.example.e_commerce.domain.products.models.Product
import javax.inject.Inject

class WishlistRepository @Inject constructor(private val apiService: WishlistApiService) {

    suspend fun getWishlist(): NetworkResult<List<Product>> {
        return when (val result = apiService.getWishlist().toNetworkResult()) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.data.orEmpty().map { item ->
                Product(
                    id = item.id,
                    title = item.title,
                    price = item.price,
                    imageCover = item.imageCover,
                    rating = item.ratingsAverage
                )
            })

            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }

    suspend fun addToWishlist(productId: String): NetworkResult<Unit> =
        apiService.addToWishlist(AddToWishlistRequest(productId)).toUnitResult()

    suspend fun removeFromWishlist(productId: String): NetworkResult<Unit> =
        apiService.removeFromWishlist(productId).toUnitResult()

    private fun retrofit2.Response<WishlistActionResponse>.toUnitResult(): NetworkResult<Unit> {
        return when (val result = toNetworkResult()) {
            is NetworkResult.Success -> {
                val status = result.data.status
                if (status.isBlank() || status.equals("success", ignoreCase = true)) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.HttpError(200, result.data.message.ifBlank { "Wishlist request failed" })
                }
            }

            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }
}
