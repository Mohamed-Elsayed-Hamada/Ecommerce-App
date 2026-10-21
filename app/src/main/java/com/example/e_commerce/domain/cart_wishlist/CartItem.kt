package com.example.e_commerce.domain.cart_wishlist

import com.example.e_commerce.data.cart_wishlist.models.CartResponseData

data class CartItem(
    val id: String,
    val productId: String,
    val title: String,
    val imageCover: String,
    val price: Double,
    val count: Int,
    val availableQuantity: Int,
    val rating: Double,
    val brandName: String
)

data class Cart(
    val cartId: String,
    val products: List<CartItem>,
    val totalPrice: Double,
    val numberOfItems: Int
)

/**
 * The single cart mapper. `numberOfItems` is the sum of units (not the number of
 * lines) — one definition shared by every screen.
 */
fun CartResponseData.toDomain(): Cart {
    val items = products.orEmpty().map { product ->
        CartItem(
            id = product.id,
            productId = product.product.id.ifBlank { product.product._id },
            title = product.product.title,
            imageCover = product.product.imageCover,
            price = product.price,
            count = product.count,
            availableQuantity = product.product.quantity,
            rating = product.product.ratingsAverage,
            brandName = product.product.brand?.name.orEmpty()
        )
    }

    return Cart(
        cartId = id,
        products = items,
        totalPrice = totalCartPrice,
        numberOfItems = items.sumOf { it.count }
    )
}
