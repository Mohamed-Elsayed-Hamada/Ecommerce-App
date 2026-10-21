package com.example.e_commerce.data.category

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.PagingRequest
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.category.models.CategoryResponse
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: CategoryApiService) {

    suspend fun getCategories(request: PagingRequest? = null): NetworkResult<CategoryResponse> =
        apiService.getCategories(request?.toQueryMap() ?: emptyMap()).toNetworkResult()
}
