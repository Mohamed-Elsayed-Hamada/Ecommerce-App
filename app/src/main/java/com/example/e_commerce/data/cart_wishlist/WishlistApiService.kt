package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.AddToWishlistRequest
import com.example.e_commerce.data.cart_wishlist.models.WishlistActionResponse
import com.example.e_commerce.data.cart_wishlist.models.WishlistResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WishlistApiService {

    @GET("v1/wishlist")
    suspend fun getWishlist(): Response<WishlistResponse>

    @POST("v1/wishlist")
    suspend fun addToWishlist(@Body request: AddToWishlistRequest): Response<WishlistActionResponse>

    @DELETE("v1/wishlist/{productId}")
    suspend fun removeFromWishlist(@Path("productId") productId: String): Response<WishlistActionResponse>
}
