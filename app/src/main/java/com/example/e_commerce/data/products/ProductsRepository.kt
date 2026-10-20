package com.example.e_commerce.data.products

import com.example.e_commerce.data.products.models.ProductDataDetails
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.data.products.models.ProductsResponse
import javax.inject.Inject // Changed from jakarta to javax

class ProductsRepository @Inject constructor(private val apiService: ProductsApiService) {

    suspend fun getProducts(
        queries: ProductsRequest? = null,
        categories: List<String>? = null
    ): ProductsResponse? {
        val response = apiService.getProducts(queries?.toQueryMap() ?: mapOf(), categories)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    // Renamed for clarity to avoid confusion with the list fetcher
    suspend fun getProductDetails(id: String): ProductDataDetails? {
        val response = apiService.getProduct(id)
        if (response.isSuccessful) {
            return response.body()?.data
        }
        return null
    }
}