package com.example.e_commerce.data.products.models
import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("data")
    val data: List<ProductData>,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("results")
    val results: Int
)

data class ProductData(
    @SerializedName("availableColors")
    val availableColors: List<Any>,
    @SerializedName("brand")
    val brand: Brand,
    @SerializedName("category")
    val category: Category,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("id")
    val _id: String,
    @SerializedName("imageCover")
    val imageCover: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("price")
    val price: Double,
    @SerializedName("priceAfterDiscount")
    val priceAfterDiscount: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("ratingsAverage")
    val ratingsAverage: Double,
    @SerializedName("ratingsQuantity")
    val ratingsQuantity: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("sold")
    val sold: Int,
    @SerializedName("subcategory")
    val subcategory: List<Subcategory>,
    @SerializedName("title")
    val title: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class Metadata(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("nextPage")
    val nextPage: Int,
    @SerializedName("numberOfPages")
    val numberOfPages: Int
)

data class Brand(
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

data class Category(
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

data class Subcategory(
    @SerializedName("category")
    val category: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)


