package com.example.e_commerce.presentation.cart_wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.domain.cart_wishlist.Cart
import com.example.e_commerce.domain.cart_wishlist.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(val cartRepository: CartRepository) : ViewModel() {

    private val _cart : MutableLiveData<Cart> = MutableLiveData()
    val cart : LiveData<Cart> = _cart
    fun removeFromCart(productId: String){
        viewModelScope.launch {
            try {
                val response = cartRepository.deleteCart(productId)
                if(response != null){
                    val cartId = response?.id
//                val numOfItems = response?.
                    response?.let {

                        val cartItems = it.products.map { cartProduct ->
                            CartItem(
                                id = it.id,
                                productId = cartProduct.product.id,
                                title = cartProduct.product.title,
                                imageCover = cartProduct.product.imageCover,
                                price = cartProduct.price,
                                count = cartProduct.count,
                                availableQuantity = cartProduct.product.quantity,
                                rating = cartProduct.product.ratingsAverage,
                                brandName = cartProduct.product.brand.name
                            )
                        }

                        _cart.value = Cart(
                            cartId = cartId!!,
                            products = cartItems,
                            totalPrice = it.totalCartPrice,
                            numberOfItems = it.products.count()
                        )
                    }
                }
                }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
    fun updateCart(productId: String, newCount: Int){
        viewModelScope.launch {
            try {
                val response = cartRepository.updateCart(productId, newCount)
                if(response != null){
                    val cartId = response?.id
//                val numOfItems = response?.
                    response?.let {

                        val cartItems = it.products.map { cartProduct ->
                            CartItem(
                                id = it.id,
                                productId = cartProduct.product.id,
                                title = cartProduct.product.title,
                                imageCover = cartProduct.product.imageCover,
                                price = cartProduct.price,
                                count = cartProduct.count,
                                availableQuantity = cartProduct.product.quantity,
                                rating = cartProduct.product.ratingsAverage,
                                brandName = cartProduct.product.brand.name
                            )
                        }

                        _cart.value = Cart(
                            cartId = cartId!!,
                            products = cartItems,
                            totalPrice = it.totalCartPrice,
                            numberOfItems = it.products.count()
                        )
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
    fun getCart() {

        viewModelScope.launch {

            try {

                val response = cartRepository.getUserCart()
                val cartId = response?.id
//                val numOfItems = response?.
                response?.let {

                    val cartItems = it.products.map { cartProduct ->

                        CartItem(
                            id = it.id,
                            productId = cartProduct.product.id,
                            title = cartProduct.product.title,
                            imageCover = cartProduct.product.imageCover,
                            price = cartProduct.price,
                            count = cartProduct.count,
                            availableQuantity = cartProduct.product.quantity,
                            rating = cartProduct.product.ratingsAverage,
                            brandName = cartProduct.product.brand.name
                        )
                    }

                    _cart.value = Cart(
                        cartId = cartId!!,
                        products = cartItems,
                        totalPrice = it.totalCartPrice,
                        numberOfItems = it.products.count()
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}

