package com.example.e_commerce.data.cart_wishlist.models

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("cartId")
    val cartId: String? = null,
    @SerializedName("data")
    val data: CartResponseData? = null,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("numOfCartItems")
    val numOfCartItems: Int = 0,
    @SerializedName("status")
    val status: String = ""
)

data class CartResponseData(
    @SerializedName("cartOwner")
    val cartOwner: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("products")
    val products: List<Product>? = null,
    @SerializedName("totalCartPrice")
    val totalCartPrice: Double = 0.0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("__v")
    val v: Int = 0
)

data class Product(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("product")
    val product: ProductX
)

data class ProductX(
    @SerializedName("brand")
    val brand: Brand? = null,
    @SerializedName("category")
    val category: Category? = null,
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("imageCover")
    val imageCover: String = "",
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("ratingsAverage")
    val ratingsAverage: Double = 0.0,
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("subcategory")
    val subcategory: List<Subcategory> = emptyList(),
    @SerializedName("title")
    val title: String = ""
)

data class Brand(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = ""
)

data class Category(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = ""
)

data class Subcategory(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = ""
)
