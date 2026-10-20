package com.example.e_commerce.data.category

import com.example.e_commerce.core.PagingRequest
import com.example.e_commerce.data.category.models.CategoryResponse
import javax.inject.Inject

class CategoryRepository @Inject constructor(val apiService: CategoryApiService) {
    suspend fun getCategories(request : PagingRequest? =null) : CategoryResponse? {
        val response = apiService.getCategories(request?.toQueryMap() ?:mapOf())
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}