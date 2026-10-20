package com.example.e_commerce.data.category

import com.example.e_commerce.data.category.models.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CategoryApiService {

    @GET("v1/categories")
    suspend fun getCategories(
        @QueryMap queries: Map<String, String>
    ): Response<CategoryResponse>

}