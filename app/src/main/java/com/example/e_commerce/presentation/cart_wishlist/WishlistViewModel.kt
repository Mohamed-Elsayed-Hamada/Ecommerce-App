package com.example.e_commerce.presentation.cart_wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.R
import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.util.SingleLiveEvent
import com.example.e_commerce.data.cart_wishlist.WishlistRepository
import com.example.e_commerce.domain.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _message = SingleLiveEvent<Int>()
    val message: LiveData<Int> = _message

    fun load() {
        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            when (val result = wishlistRepository.getWishlist()) {
                is NetworkResult.Success -> _products.value = result.data
                is NetworkResult.HttpError -> _isError.value = true
                is NetworkResult.NetworkError -> _isError.value = true
            }

            _isLoading.value = false
        }
    }

    fun remove(productId: String) {
        val current = _products.value.orEmpty()
        _products.value = current.filterNot { it.id == productId }

        viewModelScope.launch {
            val result = wishlistRepository.removeFromWishlist(productId)
            if (result !is NetworkResult.Success) {
                _products.value = current
                _message.value = R.string.error_wishlist_update
            }
        }
    }
}
