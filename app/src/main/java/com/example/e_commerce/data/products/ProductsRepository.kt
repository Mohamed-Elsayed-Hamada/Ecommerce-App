package com.example.e_commerce.data.products

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.products.models.ProductDataDetails
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.data.products.models.ProductsResponse
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val apiService: ProductsApiService) {

    suspend fun getProducts(
        queries: ProductsRequest? = null,
        categories: List<String>? = null
    ): NetworkResult<ProductsResponse> =
        apiService.getProducts(queries?.toQueryMap() ?: emptyMap(), categories).toNetworkResult()

    suspend fun getProductDetails(id: String): NetworkResult<ProductDataDetails> {
        return when (val result = apiService.getProduct(id).toNetworkResult()) {
            is NetworkResult.Success -> {
                val details = result.data.data
                if (details == null) {
                    NetworkResult.HttpError(200, "Empty product response")
                } else {
                    NetworkResult.Success(details)
                }
            }

            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }
}
