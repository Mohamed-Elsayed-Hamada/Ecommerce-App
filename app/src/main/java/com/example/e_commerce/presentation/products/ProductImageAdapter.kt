package com.example.e_commerce.presentation.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.ItemProductImageBinding
import com.example.e_commerce.domain.products.models.Review

class ProductImageAdapter(
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    private val images = mutableListOf<String>()
    fun submitList(imageList: List<String>) {
        images.clear()
        images.addAll(imageList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {

        val binding = ItemProductImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int
    ) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(
        private val binding: ItemProductImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {

            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.ivProductImage)
        }
    }
}