package com.example.e_commerce.data.brands.models
import com.google.gson.annotations.SerializedName

data class BrandResponse(
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
    val currentPage: Int = 1,
    @SerializedName("limit")
    val limit: Int = 0,
    @SerializedName("nextPage")
    val nextPage: Int? = null,
    @SerializedName("numberOfPages")
    val numberOfPages: Int = 1
)

