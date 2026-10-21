package com.example.e_commerce.data.products.models

data class ProductsRequest(
    val limit: Int? = null,
    val page: Int? = null,
    val sort: String? = null,
    val fields: String? = null,
    val keyword: String? = null,
    val brand: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val categories: List<String>? = null
) {
    // This function converts your object into the Map that Retrofit needs
    fun toQueryMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        limit?.let { map["limit"] = it.toString() }
        page?.let { map["page"] = it.toString() }
        sort?.let { map["sort"] = it }
        fields?.let { map["fields"] = it }
        keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { map["keyword"] = it }
        brand?.let { map["brand"] = it }
        minPrice?.let { map["price[gte]"] = it.toString() }
        maxPrice?.let { map["price[lte]"] = it.toString() }

        return map
    }
}