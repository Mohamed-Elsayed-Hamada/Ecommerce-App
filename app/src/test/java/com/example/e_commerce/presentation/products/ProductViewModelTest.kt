package com.example.e_commerce.presentation.products

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce.MainDispatcherRule
import com.example.e_commerce.data.cart_wishlist.CartApiService
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.cart_wishlist.WishlistApiService
import com.example.e_commerce.data.cart_wishlist.WishlistRepository
import com.example.e_commerce.data.cart_wishlist.models.AddToCartRequest
import com.example.e_commerce.data.cart_wishlist.models.AddToWishlistRequest
import com.example.e_commerce.data.cart_wishlist.models.CartResponse
import com.example.e_commerce.data.cart_wishlist.models.UpdateCartRequest
import com.example.e_commerce.data.cart_wishlist.models.WishlistActionResponse
import com.example.e_commerce.data.cart_wishlist.models.WishlistResponse
import com.example.e_commerce.data.products.ProductsApiService
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.Brand
import com.example.e_commerce.data.products.models.Category
import com.example.e_commerce.data.products.models.Metadata
import com.example.e_commerce.data.products.models.ProductData
import com.example.e_commerce.data.products.models.ProductDetailsResponse
import com.example.e_commerce.data.products.models.ProductsResponse
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `pagination appends pages and stops at the last page`() = runTest {
        val api = FakeProductsApi(
            page(listOf("p1", "p2"), numberOfPages = 2),
            page(listOf("p3"), numberOfPages = 2)
        )
        val viewModel = viewModel(api)

        viewModel.getProducts()
        assertEquals(2, viewModel.products.value?.size)

        viewModel.getProducts()
        assertEquals(3, viewModel.products.value?.size)
        assertEquals(true, viewModel.isLastPage.value)

        viewModel.getProducts()
        assertEquals(2, api.requests.size)
    }

    @Test
    fun `applying a filter resets the list and sends page one`() = runTest {
        val api = FakeProductsApi(
            page(listOf("p1", "p2"), numberOfPages = 2),
            page(listOf("p3"), numberOfPages = 2),
            page(listOf("filtered"), numberOfPages = 1)
        )
        val viewModel = viewModel(api)

        viewModel.getProducts()
        viewModel.getProducts()
        viewModel.applyFilters(keyword = "shawl")

        assertEquals("shawl", api.requests.last()["keyword"])
        assertEquals("1", api.requests.last()["page"])
        assertEquals(1, viewModel.products.value?.size)
        assertEquals("shawl", viewModel.activeFilter.value?.keyword)
    }

    @Test
    fun `first page error exposes retry state instead of an empty result`() = runTest {
        val api = FakeProductsApi(
            Response.error(
                500,
                """{"message":"Server error"}""".toResponseBody("application/json".toMediaType())
            )
        )
        val viewModel = viewModel(api)

        viewModel.getProducts()

        assertEquals(true, viewModel.isError.value)
        assertFalse(viewModel.isInitialLoading.value == true)
        assertFalse(viewModel.hasLoaded())
    }

    private fun viewModel(api: ProductsApiService) = ProductViewModel(
        ProductsRepository(api),
        CartRepository(UnusedCartApi()),
        WishlistRepository(UnusedWishlistApi())
    )

    private fun page(ids: List<String>, numberOfPages: Int): Response<ProductsResponse> {
        val response = ProductsResponse(
            data = ids.map { product(it) },
            metadata = Metadata(
                currentPage = 1,
                limit = 16,
                numberOfPages = numberOfPages
            ),
            results = ids.size
        )
        return Response.success(response)
    }

    private fun product(id: String) = ProductData(
        availableColors = emptyList(),
        brand = Brand("b1", "", "Brand", "brand"),
        category = Category("c1", "", "Category", "category"),
        createdAt = "",
        description = "",
        id = id,
        _id = id,
        imageCover = "",
        images = emptyList(),
        price = 100.0,
        quantity = 10,
        ratingsAverage = 4.0,
        ratingsQuantity = 1,
        slug = id,
        sold = 0,
        subcategory = emptyList(),
        title = "Product $id",
        updatedAt = ""
    )
}

private class FakeProductsApi(
    vararg responses: Response<ProductsResponse>
) : ProductsApiService {

    val requests = mutableListOf<Map<String, String>>()
    private val queue = ArrayDeque(responses.toList())

    override suspend fun getProducts(
        queries: Map<String, String>,
        categories: List<String>?
    ): Response<ProductsResponse> {
        requests += queries
        return queue.removeFirst()
    }

    override suspend fun getProduct(id: String): Response<ProductDetailsResponse> =
        error("not used")
}

private class UnusedCartApi : CartApiService {
    override suspend fun addToCart(request: AddToCartRequest): Response<CartResponse> =
        error("not used")

    override suspend fun updateCart(
        id: String,
        request: UpdateCartRequest
    ): Response<CartResponse> = error("not used")

    override suspend fun deleteCart(id: String): Response<CartResponse> = error("not used")

    override suspend fun getUserCart(): Response<CartResponse> = error("not used")
}

private class UnusedWishlistApi : WishlistApiService {
    override suspend fun getWishlist(): Response<WishlistResponse> = error("not used")

    override suspend fun addToWishlist(
        request: AddToWishlistRequest
    ): Response<WishlistActionResponse> = error("not used")

    override suspend fun removeFromWishlist(
        productId: String
    ): Response<WishlistActionResponse> = error("not used")
}
