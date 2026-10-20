package com.example.e_commerce.domain.products.models

data class ProductDetails(
    val id: String,
    val title: String,
    val price: Double,
    val imageCover: String,
    val images: List<String>,

    val rating: Double,
    val ratingsQuantity: Int,

    val description: String,

    val brand: Brand,
    val category: Category,
    val subcategory: List<Subcategory>,

    val quantity: Int,

    val reviews: List<Review>
)

data class Brand(
    val id: String,
    val name: String,
    val image: String
)

data class Category(
    val id: String,
    val name: String,
    val image: String
)

data class Subcategory(
    val id: String,
    val name: String
)

data class Review(
    val id: String,
    val review: String,
    val rating: Int,
    val user: User
)

data class User(
    val id: String,
    val name: String
)