package com.example.e_commerce.data.cart_wishlist.models

data class AddToCartRequest(
    val productId: String
)

data class UpdateCartRequest(
    val count: Int
)