package com.example.e_commerce.data.cart_wishlist.models

import com.google.gson.annotations.SerializedName

data class AddToWishlistRequest(
    @SerializedName("productId")
    val productId: String
)
