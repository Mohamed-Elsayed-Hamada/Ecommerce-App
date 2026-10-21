package com.example.e_commerce.presentation.cart_wishlist

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.R
import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.util.SingleLiveEvent
import com.example.e_commerce.data.cart_wishlist.CartRepository
import com.example.e_commerce.data.cart_wishlist.models.CartResponseData
import com.example.e_commerce.domain.cart_wishlist.Cart
import com.example.e_commerce.domain.cart_wishlist.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

sealed interface CartUiState {
    data object Loading : CartUiState
    data object Empty : CartUiState
    data class Content(val cart: Cart) : CartUiState
    data class Error(@StringRes val messageRes: Int) : CartUiState
}

sealed interface CartEvent {
    data class ItemRemoved(val productId: String, val count: Int) : CartEvent
    data class Error(@StringRes val messageRes: Int) : CartEvent
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableLiveData<CartUiState>(CartUiState.Loading)
    val state: LiveData<CartUiState> = _state

    private val _events = SingleLiveEvent<CartEvent>()
    val events: LiveData<CartEvent> = _events

    private val _pendingProductIds = MutableLiveData<Set<String>>(emptySet())
    val pendingProductIds: LiveData<Set<String>> = _pendingProductIds

    private val locks = mutableMapOf<String, Mutex>()

    fun getCart() {
        _state.value = CartUiState.Loading

        viewModelScope.launch {
            when (val result = cartRepository.getUserCart()) {
                is NetworkResult.Success -> publish(result.data)
                is NetworkResult.HttpError -> _state.value = CartUiState.Error(R.string.error_loading_title)
                is NetworkResult.NetworkError -> _state.value = CartUiState.Error(R.string.error_loading_title)
            }
        }
    }

    fun updateCart(productId: String, newCount: Int) {
        mutate(productId) {
            cartRepository.updateCart(productId, newCount)
        }
    }

    fun removeFromCart(productId: String, count: Int) {
        mutate(productId, onSuccess = { _events.value = CartEvent.ItemRemoved(productId, count) }) {
            cartRepository.deleteCart(productId)
        }
    }

    /**
     * Undo is implemented as add-then-update because PUT-after-DELETE is not
     * guaranteed to recreate the line (see REL-004).
     */
    fun restoreItem(productId: String, count: Int) {
        mutate(productId) {
            when (val added = cartRepository.addToCart(productId)) {
                is NetworkResult.Success -> {
                    if (count > 1) {
                        cartRepository.updateCart(productId, count)
                    } else {
                        added
                    }
                }

                else -> added
            }
        }
    }

    private fun mutate(
        productId: String,
        onSuccess: () -> Unit = {},
        operation: suspend () -> NetworkResult<CartResponseData>
    ) {
        viewModelScope.launch {
            lockFor(productId).withLock {
                markPending(productId, true)

                when (val result = operation()) {
                    is NetworkResult.Success -> {
                        publish(result.data)
                        onSuccess()
                    }

                    is NetworkResult.HttpError -> _events.value = CartEvent.Error(R.string.error_cart_update)
                    is NetworkResult.NetworkError -> _events.value = CartEvent.Error(R.string.error_cart_update)
                }

                markPending(productId, false)
            }
        }
    }

    private fun publish(data: CartResponseData) {
        val cart = data.toDomain()
        _state.value = if (cart.products.isEmpty()) {
            CartUiState.Empty
        } else {
            CartUiState.Content(cart)
        }
    }

    private fun lockFor(productId: String): Mutex =
        locks.getOrPut(productId) { Mutex() }

    private fun markPending(productId: String, pending: Boolean) {
        val current = _pendingProductIds.value.orEmpty()
        _pendingProductIds.value = if (pending) current + productId else current - productId
    }
}
