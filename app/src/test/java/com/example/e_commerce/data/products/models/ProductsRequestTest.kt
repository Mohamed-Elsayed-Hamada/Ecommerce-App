package com.example.e_commerce.data.products.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductsRequestTest {

    @Test
    fun `query map contains only the active filters`() {
        val request = ProductsRequest(
            limit = 16,
            page = 2,
            keyword = "shawl",
            brand = "brand-1",
            minPrice = 100,
            maxPrice = 300,
            sort = "price"
        )

        val query = request.toQueryMap()

        assertEquals("16", query["limit"])
        assertEquals("2", query["page"])
        assertEquals("shawl", query["keyword"])
        assertEquals("brand-1", query["brand"])
        assertEquals("100", query["price[gte]"])
        assertEquals("300", query["price[lte]"])
        assertEquals("price", query["sort"])
    }

    @Test
    fun `blank keyword is omitted`() {
        val query = ProductsRequest(keyword = "   ").toQueryMap()

        assertNull(query["keyword"])
    }
}
