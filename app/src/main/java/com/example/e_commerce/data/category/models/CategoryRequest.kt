package com.example.e_commerce.data.category.models

import com.example.e_commerce.core.PagingRequest

class CategoryRequest(
    limit: Int? = null,
    page: Int? = null
) : PagingRequest(limit, page) {

}