package com.example.e_commerce.presentation.brands

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.CategoryItemBinding
import com.example.e_commerce.domain.brands.Brand
import com.example.e_commerce.domain.category.Category
class BrandAdapter : RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {
    var brandList = mutableListOf<Brand>()
    var onItemClick: ((String) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BrandViewHolder,
        position: Int
    ) {
        holder.bind(brandList[position])
    }

    override fun getItemCount(): Int {
        return brandList.size
    }

    inner class BrandViewHolder : RecyclerView.ViewHolder {
        lateinit var binding: CategoryItemBinding

        constructor(binding: CategoryItemBinding) : super(binding.root) {
            this.binding = binding
        }

        fun bind(brand: Brand) {
            binding.tvCategoryName.text = brand.name
            Glide.with(binding.root.context)
                .load(brand.image)
                .into(binding.ivCategoryImage)
        }

    }

}
