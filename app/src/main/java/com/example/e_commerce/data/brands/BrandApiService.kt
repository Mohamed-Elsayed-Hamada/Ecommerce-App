package com.example.e_commerce.data.brands


import com.example.e_commerce.data.brands.models.BrandResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface BrandApiService {
    @GET("v1/brands")
    suspend fun getBrands(@QueryMap queries: Map<String, String>? = null): Response<BrandResponse>

}