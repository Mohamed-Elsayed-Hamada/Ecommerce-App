package com.example.e_commerce.presentation.products

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce.MainDispatcherRule
import com.example.e_commerce.R
import com.example.e_commerce.data.cart_wishlist.CartApiService
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.cart_wishlist.WishlistApiService
import com.example.e_commerce.data.cart_wishlist.WishlistRepository
import com.example.e_commerce.data.cart_wishlist.models.AddToCartRequest
import com.example.e_commerce.data.cart_wishlist.models.AddToWishlistRequest
import com.example.e_commerce.data.cart_wishlist.models.CartResponse
import com.example.e_commerce.data.cart_wishlist.models.CartResponseData
import com.example.e_commerce.data.cart_wishlist.models.Product
import com.example.e_commerce.data.cart_wishlist.models.ProductX
import com.example.e_commerce.data.cart_wishlist.models.UpdateCartRequest
import com.example.e_commerce.data.cart_wishlist.models.WishlistActionResponse
import com.example.e_commerce.data.cart_wishlist.models.WishlistResponse
import com.example.e_commerce.data.products.ProductsApiService
import com.example.e_commerce.data.products.ProductsRepository
import com.example.e_commerce.data.products.models.ProductDataDetails
import com.example.e_commerce.data.products.models.ProductDetailsResponse
import com.example.e_commerce.data.products.models.ProductsRequest
import com.example.e_commerce.data.products.models.ProductsResponse
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class ProductDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `adding two units reports the delivered cart count`() = runTest {
        val cartApi = FakeCartApi(
            emptyCart = emptyCart(),
            addedCart = cartWithCount(1),
            updatedCart = cartWithCount(2)
        )
        val viewModel = viewModel(cartApi)
        viewModel.productId = PRODUCT_ID

        viewModel.setCartCount(2)

        assertEquals(2, viewModel.cartLine.value?.count)
        assertEquals(2, viewModel.quantity.value)
        assertFalse(viewModel.cartLine.value?.isPending == true)
        assertEquals(R.string.item_added_to_cart, viewModel.message.value)
    }

    @Test
    fun `failed add clears pending and reports an error`() = runTest {
        val cartApi = FakeCartApi(
            emptyCart = emptyCart(),
            addedCart = Response.error(
                500,
                """{"message":"Server error"}""".toResponseBody("application/json".toMediaType())
            ),
            updatedCart = cartWithCount(1)
        )
        val viewModel = viewModel(cartApi)
        viewModel.productId = PRODUCT_ID

        viewModel.setCartCount(1)

        assertFalse(viewModel.cartLine.value?.isPending == true)
        assertEquals(R.string.error_add_to_cart, viewModel.message.value)
    }

    private fun viewModel(cartApi: CartApiService) = ProductDetailsViewModel(
        ProductsRepository(NoopProductsApi()),
        CartRepository(cartApi),
        WishlistRepository(NoopWishlistApi())
    )

    private fun emptyCart(): Response<CartResponse> = Response.success(
        CartResponse(
            data = CartResponseData(
                id = "cart-1",
                products = emptyList(),
                totalCartPrice = 0.0
            )
        )
    )

    private fun cartWithCount(count: Int): Response<CartResponse> = Response.success(
        CartResponse(
            data = CartResponseData(
                id = "cart-1",
                products = listOf(
                    Product(
                        count = count,
                        id = "line-1",
                        price = 149.0,
                        product = ProductX(
                            _id = PRODUCT_ID,
                            id = PRODUCT_ID,
                            title = "Woman Shawl",
                            imageCover = "cover.jpeg",
                            quantity = 10,
                            ratingsAverage = 4.1
                        )
                    )
                ),
                totalCartPrice = 149.0 * count
            )
        )
    )

    private companion object {
        const val PRODUCT_ID = "p1"
    }
}

private class FakeCartApi(
    private val emptyCart: Response<CartResponse>,
    private val addedCart: Response<CartResponse>,
    private val updatedCart: Response<CartResponse>
) : CartApiService {

    override suspend fun addToCart(request: AddToCartRequest): Response<CartResponse> = addedCart

    override suspend fun updateCart(
        id: String,
        request: UpdateCartRequest
    ): Response<CartResponse> = updatedCart

    override suspend fun deleteCart(id: String): Response<CartResponse> = emptyCart

    override suspend fun getUserCart(): Response<CartResponse> = emptyCart
}

private class NoopProductsApi : ProductsApiService {
    override suspend fun getProducts(
        queries: Map<String, String>,
        categories: List<String>?
    ): Response<ProductsResponse> = error("not used")

    override suspend fun getProduct(id: String): Response<ProductDetailsResponse> = error("not used")
}

private class NoopWishlistApi : WishlistApiService {
    override suspend fun getWishlist(): Response<WishlistResponse> = error("not used")

    override suspend fun addToWishlist(
        request: AddToWishlistRequest
    ): Response<WishlistActionResponse> = error("not used")

    override suspend fun removeFromWishlist(
        productId: String
    ): Response<WishlistActionResponse> = error("not used")
}
