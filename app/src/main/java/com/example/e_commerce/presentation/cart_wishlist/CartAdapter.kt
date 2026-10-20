package com.example.e_commerce.presentation.cart_wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.CartItemBinding
import com.example.e_commerce.domain.cart_wishlist.CartItem

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartItems = mutableListOf<CartItem>()

    var onProductClick: ((String) -> Unit)? = null

    var onIncreaseClick: ((CartItem) -> Unit)? = null

    var onDecreaseClick: ((CartItem) -> Unit)? = null

    var onDeleteClick: ((CartItem) -> Unit)? = null

    fun submitList(items: List<CartItem>) {

        cartItems.clear()
        cartItems.addAll(items)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartViewHolder(
        private val binding: CartItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {

            binding.tvProductTitle.text =
                item.title

            binding.tvBrand.text =
                item.brandName

            binding.tvPrice.text =
                "${item.price} EGP"

            binding.tvRating.text =
                "⭐ ${item.rating}"

            binding.tvQuantity.text =
                item.count.toString()

            Glide.with(binding.root.context)
                .load(item.imageCover)
                .into(binding.ivProductImage)


            // Product click

            binding.root.setOnClickListener {

                onProductClick?.invoke(
                    item.productId
                )
            }


            // Increase

            binding.btnIncrease.setOnClickListener {

                onIncreaseClick?.invoke(item)
            }


            // Decrease

            binding.btnDecrease.setOnClickListener {

                onDecreaseClick?.invoke(item)
            }


            // Delete

            binding.btnDelete.setOnClickListener {

                onDeleteClick?.invoke(item)
            }
        }
    }
}