package com.example.e_commerce.presentation.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductsBinding
import com.example.e_commerce.databinding.FragmentReviewsBinding
import com.example.e_commerce.domain.products.models.Review
import com.example.e_commerce.domain.products.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReviewsFragment : Fragment() {
    //binding , adabter , viewModel , recycle , send data to this fragment
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       super.onViewCreated(view, savedInstanceState)
       adapter = ReviewAdapter()
       binding.rvReviews.adapter = adapter
       val reviews = ReviewsFragmentArgs
           .fromBundle(requireArguments())
           .reviews
       val reviewsJson = ReviewsFragmentArgs
           .fromBundle(requireArguments())
           .reviews

       val type = object : TypeToken<List<Review>>() {}.type

       val deserializedList: List<Review> =
           Gson().fromJson(reviewsJson, type)

       adapter.submitList(deserializedList)

       adapter.submitList(deserializedList)
       adapter.notifyDataSetChanged()
   }

}