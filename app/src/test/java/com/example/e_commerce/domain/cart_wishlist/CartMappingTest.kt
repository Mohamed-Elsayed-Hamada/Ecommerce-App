package com.example.e_commerce.domain.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.CartResponse
import com.example.e_commerce.data.products.models.ProductsResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CartMappingTest {

    private val gson = Gson()

    @Test
    fun `empty cart response maps to an empty domain cart`() {
        val response = gson.fromJson(EMPTY_CART, CartResponse::class.java)

        val cart = response.data!!.toDomain()

        assertTrue(cart.products.isEmpty())
        assertEquals(0.0, cart.totalPrice, 0.0)
        assertEquals(0, cart.numberOfItems)
    }

    @Test
    fun `cart counts units, not distinct lines`() {
        val response = gson.fromJson(CART_WITH_TWO_UNITS, CartResponse::class.java)

        val cart = response.data!!.toDomain()

        assertEquals(1, cart.products.size)
        assertEquals(2, cart.numberOfItems)
        assertEquals(298.0, cart.totalPrice, 0.0)
        assertEquals("p1", cart.products.single().productId)
        assertEquals(149.0, cart.products.single().price, 0.0)
    }

    @Test
    fun `missing nextPage on the last page parses as null`() {
        val response = gson.fromJson(LAST_PAGE, ProductsResponse::class.java)

        assertNull(response.metadata.nextPage)
        assertEquals(28, response.metadata.numberOfPages)
    }

    private companion object {
        const val EMPTY_CART = """
            {
              "status":"success",
              "message":"Cart retrieved",
              "numOfCartItems":0,
              "cartId":null,
              "data":{"products":[],"totalCartPrice":0}
            }
        """

        const val CART_WITH_TWO_UNITS = """
            {
              "status":"success",
              "numOfCartItems":1,
              "data":{
                "_id":"cart-1",
                "products":[{
                  "count":2,
                  "_id":"line-1",
                  "price":149,
                  "product":{
                    "_id":"p1",
                    "id":"p1",
                    "title":"Woman Shawl",
                    "imageCover":"cover.jpeg",
                    "quantity":220,
                    "ratingsAverage":4.1,
                    "subcategory":[]
                  }
                }],
                "totalCartPrice":298
              }
            }
        """

        const val LAST_PAGE = """
            {
              "results":56,
              "metadata":{"currentPage":28,"limit":2,"numberOfPages":28},
              "data":[]
            }
        """
    }
}
