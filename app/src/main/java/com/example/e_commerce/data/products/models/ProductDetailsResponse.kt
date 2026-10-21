package com.example.e_commerce.data.products.models
import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("data")
    val data: ProductDataDetails? = null
)

data class ProductDataDetails(
    @SerializedName("brand")
    val brand: Brand,
    @SerializedName("category")
    val category: Category,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val _id: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("imageCover")
    val imageCover: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("ratingsAverage")
    val ratingsAverage: Double,
    @SerializedName("ratingsQuantity")
    val ratingsQuantity: Int,
    @SerializedName("reviews")
    val reviews: List<Review>,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("sold")
    val sold: Int,
    @SerializedName("subcategory")
    val subcategory: List<Subcategory>,
    @SerializedName("title")
    val title: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("__v")
    val v: Int
)

data class Review(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("product")
    val product: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("review")
    val review: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("__v")
    val v: Int
)

data class User(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String
)


//data class Brand(
//    @SerializedName("_id")
//    val id: String,
//    @SerializedName("image")
//    val image: String,
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("slug")
//    val slug: String
//)
//
//data class Category(
//    @SerializedName("_id")
//    val id: String,
//    @SerializedName("image")
//    val image: String,
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("slug")
//    val slug: String
//)

//data class Subcategory(
//    @SerializedName("category")
//    val category: String,
//    @SerializedName("_id")
//    val id: String,
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("slug")
//    val slug: String
//)


