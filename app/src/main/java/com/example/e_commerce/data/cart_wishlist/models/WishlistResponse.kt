package com.example.e_commerce.data.cart_wishlist.models

import com.google.gson.annotations.SerializedName

/**
 * Lenient wishlist DTOs: the GET endpoint returns full product objects while the
 * POST/DELETE endpoints return only the affected product ids, so the two shapes
 * are modelled separately and unknown fields are ignored by Gson.
 */
data class WishlistResponse(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("data")
    val data: List<WishlistProduct>? = null
)

data class WishlistActionResponse(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val data: List<String> = emptyList()
)

data class WishlistProduct(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("imageCover")
    val imageCover: String = "",
    @SerializedName("ratingsAverage")
    val ratingsAverage: Double = 0.0,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("brand")
    val brand: WishlistBrand? = null
)

data class WishlistBrand(
    @SerializedName("name")
    val name: String = ""
)
