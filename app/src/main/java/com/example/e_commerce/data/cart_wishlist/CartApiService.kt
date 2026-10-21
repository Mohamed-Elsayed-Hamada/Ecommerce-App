package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.AddToCartRequest
import com.example.e_commerce.data.cart_wishlist.models.CartResponse
import com.example.e_commerce.data.cart_wishlist.models.UpdateCartRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {

    @POST("v2/cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<CartResponse>

    @PUT("v2/cart/{id}")
    suspend fun updateCart(
        @Path("id") id: String,
        @Body request: UpdateCartRequest
    ): Response<CartResponse>

    @DELETE("v2/cart/{id}")
    suspend fun deleteCart(@Path("id") id: String): Response<CartResponse>

    @GET("v2/cart")
    suspend fun getUserCart(): Response<CartResponse>

}