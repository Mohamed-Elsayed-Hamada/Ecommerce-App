package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.cart_wishlist.models.AddToCartRequest
import com.example.e_commerce.data.cart_wishlist.models.CartResponse
import com.example.e_commerce.data.cart_wishlist.models.CartResponseData
import com.example.e_commerce.data.cart_wishlist.models.UpdateCartRequest
import retrofit2.Response
import javax.inject.Inject

class CartRepository @Inject constructor(private val cartApiService: CartApiService) {

    suspend fun addToCart(productId: String): NetworkResult<CartResponseData> {
        val request = AddToCartRequest(productId)
        return cartApiService.addToCart(request).toCartData()
    }

    suspend fun updateCart(productId: String, newCount: Int): NetworkResult<CartResponseData> {
        val request = UpdateCartRequest(newCount)
        return cartApiService.updateCart(productId, request).toCartData()
    }

    suspend fun deleteCart(productId: String): NetworkResult<CartResponseData> {
        return cartApiService.deleteCart(productId).toCartData()
    }

    suspend fun getUserCart(): NetworkResult<CartResponseData> {
        return cartApiService.getUserCart().toCartData()
    }

    private fun Response<CartResponse>.toCartData(): NetworkResult<CartResponseData> {
        return when (val result = toNetworkResult()) {
            is NetworkResult.Success -> {
                val data = result.data.data
                if (data == null) {
                    NetworkResult.HttpError(200, "Empty cart response")
                } else {
                    NetworkResult.Success(data)
                }
            }

            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }
}
