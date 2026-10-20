package com.example.e_commerce.data.category.models
import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("results")
    val results: Int
)

data class Data(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class Metadata(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("numberOfPages")
    val numberOfPages: Int
)


