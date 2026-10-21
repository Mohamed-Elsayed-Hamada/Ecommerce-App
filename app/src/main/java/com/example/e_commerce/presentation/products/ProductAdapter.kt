package com.example.e_commerce.presentation.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.core.util.formatMoney
import com.example.e_commerce.core.util.formatRating
import com.example.e_commerce.databinding.ProductItemBinding
import com.example.e_commerce.domain.products.models.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    var onItemClick: ((String) -> Unit)? = null
    var onItemAddToCartClick: ((String) -> Unit)? = null
    var onItemFavoriteClick: ((Product) -> Unit)? = null

    private var favoriteIds: Set<String> = emptySet()

    fun setFavoriteIds(ids: Set<String>) {
        if (favoriteIds == ids) return
        favoriteIds = ids
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position).id)
                }
            }

            binding.btnAddCart.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemAddToCartClick?.invoke(getItem(position).id)
                }
            }

            binding.ivFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemFavoriteClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(product: Product) {
            val context = binding.root.context
            binding.tvProductTitle.text = product.title
            binding.tvProductPrice.text = formatMoney(context, product.price)
            binding.tvProductRating.text = formatRating(product.rating)
            binding.ivFavorite.setImageResource(
                if (product.id in favoriteIds) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
            Glide.with(context)
                .load(product.imageCover)
                .centerCrop()
                .into(binding.ivProductImage)
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
        }
    }
}
