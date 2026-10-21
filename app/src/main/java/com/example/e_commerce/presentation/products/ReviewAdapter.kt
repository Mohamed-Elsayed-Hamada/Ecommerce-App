package com.example.e_commerce.presentation.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.core.util.formatRating
import com.example.e_commerce.databinding.ItemReviewBinding
import com.example.e_commerce.domain.products.models.Review

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            val name = review.user.name
            binding.tvReviewerName.text = name
            binding.tvAvatarInitial.text = name.firstOrNull()?.uppercase()
                ?: binding.root.context.getString(R.string.avatar_fallback)
            binding.tvReviewRating.text = formatRating(review.rating.toDouble())
            binding.tvReview.text = review.review
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Review, newItem: Review) = oldItem == newItem
        }
    }
}
