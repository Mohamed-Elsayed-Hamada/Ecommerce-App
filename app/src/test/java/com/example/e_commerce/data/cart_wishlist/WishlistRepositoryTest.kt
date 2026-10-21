package com.example.e_commerce.data.cart_wishlist

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.data.cart_wishlist.models.AddToWishlistRequest
import com.example.e_commerce.data.cart_wishlist.models.WishlistActionResponse
import com.example.e_commerce.data.cart_wishlist.models.WishlistResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class WishlistRepositoryTest {

    @Test
    fun `add sends product id in the request body`() = runTest {
        val api = FakeWishlistApi()
        val repository = WishlistRepository(api)

        val result = repository.addToWishlist("p1")

        assertTrue(result is NetworkResult.Success)
        assertEquals("p1", api.lastAddRequest?.productId)
    }

    @Test
    fun `get maps wishlist products to the domain model`() = runTest {
        val api = FakeWishlistApi()
        val repository = WishlistRepository(api)

        val result = repository.getWishlist()

        assertTrue(result is NetworkResult.Success)
        val products = (result as NetworkResult.Success).data
        assertEquals(1, products.size)
        assertEquals("p1", products.single().id)
        assertEquals(149.0, products.single().price, 0.0)
    }
}

private class FakeWishlistApi : WishlistApiService {

    var lastAddRequest: AddToWishlistRequest? = null

    override suspend fun getWishlist(): Response<WishlistResponse> = Response.success(
        WishlistResponse(
            status = "success",
            count = 1,
            data = listOf(
                com.example.e_commerce.data.cart_wishlist.models.WishlistProduct(
                    id = "p1",
                    title = "Woman Shawl",
                    price = 149.0,
                    imageCover = "cover.jpeg",
                    ratingsAverage = 4.1,
                    quantity = 10
                )
            )
        )
    )

    override suspend fun addToWishlist(
        request: AddToWishlistRequest
    ): Response<WishlistActionResponse> {
        lastAddRequest = request
        return Response.success(
            WishlistActionResponse(
                status = "success",
                message = "Product added successfully to your wishlist",
                data = listOf("p1")
            )
        )
    }

    override suspend fun removeFromWishlist(
        productId: String
    ): Response<WishlistActionResponse> = Response.success(
        WishlistActionResponse(status = "success", message = "removed", data = emptyList())
    )
}
