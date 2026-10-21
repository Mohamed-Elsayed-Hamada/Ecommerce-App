package com.example.e_commerce.presentation.cart_wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.core.util.formatMoney
import com.example.e_commerce.core.util.formatRating
import com.example.e_commerce.databinding.CartItemBinding
import com.example.e_commerce.domain.cart_wishlist.CartItem

class CartAdapter : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    var onProductClick: ((String) -> Unit)? = null
    var onIncreaseClick: ((CartItem) -> Unit)? = null
    var onDecreaseClick: ((CartItem) -> Unit)? = null
    var onDeleteClick: ((CartItem) -> Unit)? = null

    private var pendingProductIds: Set<String> = emptySet()

    fun setPendingProductIds(ids: Set<String>) {
        if (pendingProductIds == ids) return
        pendingProductIds = ids
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProductClick?.invoke(getItem(position).productId)
                }
            }

            binding.btnIncrease.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onIncreaseClick?.invoke(getItem(position))
                }
            }

            binding.btnDecrease.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDecreaseClick?.invoke(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(item: CartItem) {
            val context = binding.root.context
            val isPending = item.productId in pendingProductIds

            binding.tvProductTitle.text = item.title
            binding.tvBrand.text = item.brandName
            binding.tvPrice.text = formatMoney(context, item.price)
            binding.tvRating.text = formatRating(item.rating)
            binding.tvQuantity.text = context.getString(R.string.quantity_value_format, item.count)

            binding.btnIncrease.isEnabled = !isPending && item.count < item.availableQuantity
            binding.btnDecrease.isEnabled = !isPending && item.count > 1
            binding.btnDelete.isEnabled = !isPending

            Glide.with(context)
                .load(item.imageCover)
                .centerCrop()
                .into(binding.ivProductImage)
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
                oldItem.productId == newItem.productId

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
                oldItem == newItem
        }
    }
}
