package com.example.e_commerce.core


open class PagingRequest(
    val limit: Int? = null,
    val page: Int? = null

) {
    fun toQueryMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // سيتم إضافة القيم فقط إذا لم تكن null
        limit?.let { map["limit"] = it.toString() }
        page?.let { map["page"] = it.toString() }

        return map
    }
}