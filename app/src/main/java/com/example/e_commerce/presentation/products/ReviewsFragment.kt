package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentReviewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReviewAdapter
    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReviewAdapter()
        binding.rvReviews.adapter = adapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val productId = ReviewsFragmentArgs.fromBundle(requireArguments()).productId
        viewModel.fetchReviews(productId)

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            adapter.submitList(reviews)

            if (reviews.isEmpty()) {
                binding.layoutEmpty.root.visibility = View.VISIBLE
                binding.layoutEmpty.tvEmptyTitle.text = getString(R.string.empty_reviews_title)
                binding.layoutEmpty.tvEmptySubtitle.text = getString(R.string.empty_reviews_subtitle)
                binding.layoutEmpty.btnEmptyAction.visibility = View.GONE
            } else {
                binding.layoutEmpty.root.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
