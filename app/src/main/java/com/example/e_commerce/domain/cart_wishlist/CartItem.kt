package com.example.e_commerce.domain.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.CartResponseData

data class CartItem(
    val id: String,
    val productId: String,
    val title: String,
    val imageCover: String,
    val price: Int,
    val count: Int,
    val availableQuantity: Int,
    val rating: Double,
    val brandName: String
)
data class Cart(
    val cartId: String,
    val products: List<CartItem>,
    val totalPrice: Int,
    val numberOfItems: Int
)
fun CartResponseData.toDomain(): Cart {

    return Cart(
        cartId = id,
        products = products.map { product ->

            CartItem(
                id = product.id,
                productId = product.product.id,
                title = product.product.title,
                imageCover = product.product.imageCover,
                price = product.price,
                count = product.count,
                availableQuantity = product.product.quantity,
                rating = product.product.ratingsAverage,
                brandName = product.product.brand.name
            )
        },
        totalPrice = totalCartPrice,
        numberOfItems = products.sumOf { it.count }
    )
}