package com.example.e_commerce.data.brands

import com.example.e_commerce.core.PagingRequest
import com.example.e_commerce.data.brands.models.BrandResponse
import javax.inject.Inject

class BrandRepository @Inject constructor(private val apiService: BrandApiService)  {
    suspend fun getBrands(bagingRequest: PagingRequest? = null): BrandResponse? {
        val response = apiService.getBrands(bagingRequest?.toQueryMap() ?: mapOf())
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}