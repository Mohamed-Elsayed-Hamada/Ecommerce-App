package com.example.e_commerce.presentation.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.databinding.ProductItemBinding
import com.example.e_commerce.domain.products.models.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    var productList = mutableListOf<Product>()
    fun updateData(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }
    var onItemClick : ((String) -> Unit) ?=null
    var onItemAddToCartClick : ((String) -> Unit) ?=null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size ?: 0
    }

    inner class ProductViewHolder  : RecyclerView.ViewHolder {
        lateinit var binding : ProductItemBinding

            constructor(itemBinding : ProductItemBinding) : super(itemBinding.root)
            {
                binding= itemBinding
                binding.root.setOnClickListener {

                    val position = bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        val product = productList[position]
                        onItemClick?.invoke(product.id)

                    }
                }
                binding.btnAddCart.setOnClickListener {
//                    binding.btnAddCart.background ="#FFFfff"
                    val position = bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        val product = productList[position]
                        onItemAddToCartClick?.invoke(product.id)
                    }
                }

            }
        fun bind(product: Product) {
            binding.tvProductTitle.text = product.title
            binding.tvProductPrice.text = product.price.toString()
            binding.tvProductRating.text = product.rating.toString()
            Glide.with(binding.root.context)
                .load(product.imageCover)
                .into(binding.ivProductImage)
        }

   }
}