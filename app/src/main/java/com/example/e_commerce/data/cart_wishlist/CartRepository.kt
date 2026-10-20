package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.AddToCartRequest
import com.example.e_commerce.data.cart_wishlist.models.CartResponseData
import com.example.e_commerce.data.cart_wishlist.models.UpdateCartRequest
import javax.inject.Inject

class CartRepository @Inject constructor(private val cartApiService: CartApiService) {

    suspend fun     addToCart(productId: String): CartResponseData? {
        val request = AddToCartRequest(productId) // Wraps it in JSON object
        val response = cartApiService.addToCart(request)
        if (response.isSuccessful) {
            return response.body()?.data
        }
        return null
    }

    suspend fun updateCart(productId: String, newCount: Int): CartResponseData? {
        val request = UpdateCartRequest(newCount) // Wraps new quantity in JSON object
        val response = cartApiService.updateCart(productId, request)
        if (response.isSuccessful) {
            return response.body()?.data
        }
        return null
    }

    suspend fun deleteCart(productId: String): CartResponseData? {
        val response = cartApiService.deleteCart(productId)
        if (response.isSuccessful) {
            return response.body()?.data
        }
        return null
    }

    suspend fun getUserCart(): CartResponseData? {
        val response = cartApiService.getUserCart()
        if (response.isSuccessful) {
            return response.body()?.data
        }
        return null
    }
}