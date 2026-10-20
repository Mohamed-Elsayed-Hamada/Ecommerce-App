package com.example.e_commerce.domain.products.models

data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val imageCover: String,
    val rating: Double,
)
