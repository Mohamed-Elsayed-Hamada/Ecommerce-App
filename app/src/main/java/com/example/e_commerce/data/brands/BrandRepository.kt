package com.example.e_commerce.data.brands

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.PagingRequest
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.brands.models.BrandResponse
import javax.inject.Inject

class BrandRepository @Inject constructor(private val apiService: BrandApiService) {

    suspend fun getBrands(pagingRequest: PagingRequest? = null): NetworkResult<BrandResponse> =
        apiService.getBrands(pagingRequest?.toQueryMap() ?: emptyMap()).toNetworkResult()
}
