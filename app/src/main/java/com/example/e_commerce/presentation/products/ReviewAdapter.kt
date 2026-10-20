package com.example.e_commerce.presentation.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.ItemReviewBinding
import com.example.e_commerce.domain.products.models.Review

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val reviewList = mutableListOf<Review>()

    fun submitList(reviews: List<Review>) {
        reviewList.clear()
        reviewList.addAll(reviews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {

        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size

    class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {

            binding.tvReviewerName.text =
                review.user.name

            binding.tvReviewRating.text =
                "⭐ ${review.rating}"

            binding.tvReview.text =
                review.review
        }
    }
}