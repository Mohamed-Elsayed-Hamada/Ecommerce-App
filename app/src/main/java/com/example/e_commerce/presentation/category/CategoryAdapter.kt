package com.example.e_commerce.presentation.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.CategoryItemBinding
import com.example.e_commerce.databinding.ProductItemBinding
import com.example.e_commerce.domain.category.Category
import com.example.e_commerce.domain.products.models.Product

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    var categoryList = mutableListOf<Category>()
    var onItemClick : ((String) -> Unit) ?=null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoryViewHolder : RecyclerView.ViewHolder {
        lateinit var binding : CategoryItemBinding
        constructor(binding: CategoryItemBinding) : super(binding.root)
        {
            this.binding = binding
        }
        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name
            Glide.with(binding.root.context)
                .load(category.image)
                .into(binding.ivCategoryImage)
        }

    }



}
