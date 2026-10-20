package com.example.e_commerce.data.products

import com.example.e_commerce.data.products.models.ProductDetailsResponse
import com.example.e_commerce.data.products.models.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ProductsApiService {

    @GET("v1/products")
    suspend fun getProducts(
        @QueryMap queries: Map<String, String>,
        @Query("category[in]") categories: List<String>? = null
    ): Response<ProductsResponse>

    @GET("v1/products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): Response<ProductDetailsResponse>

}