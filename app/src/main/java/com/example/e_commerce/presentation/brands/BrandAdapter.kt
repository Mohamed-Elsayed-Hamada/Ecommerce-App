package com.example.e_commerce.presentation.brands

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.CategoryItemBinding
import com.example.e_commerce.domain.brands.Brand

class BrandAdapter : ListAdapter<Brand, BrandAdapter.BrandViewHolder>(DiffCallback) {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = CategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BrandViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position).id)
                }
            }
        }

        fun bind(brand: Brand) {
            binding.tvCategoryName.text = brand.name
            Glide.with(binding.root.context)
                .load(brand.image)
                .fitCenter()
                .into(binding.ivCategoryImage)
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Brand>() {
            override fun areItemsTheSame(oldItem: Brand, newItem: Brand) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Brand, newItem: Brand) = oldItem == newItem
        }
    }
}
