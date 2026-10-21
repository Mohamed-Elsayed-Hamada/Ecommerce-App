package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerce.R
import com.example.e_commerce.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApply.setOnClickListener {
            val minPrice = binding.etMinPrice.text
                ?.toString()
                ?.trim()
                ?.toIntOrNull()

            val maxPrice = binding.etMaxPrice.text
                ?.toString()
                ?.trim()
                ?.toIntOrNull()

            binding.tilMinPrice.error = null
            binding.tilMaxPrice.error = null

            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                binding.tilMinPrice.error = getString(R.string.error_min_greater_than_max)
                return@setOnClickListener
            }

            // Brand/category selectors are not part of this sheet yet; the
            // ViewModel already accepts them for the navigation-argument path.
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply {
                    putInt(ARG_MIN_PRICE, minPrice ?: NO_PRICE)
                    putInt(ARG_MAX_PRICE, maxPrice ?: NO_PRICE)
                }
            )

            dismiss()
        }

        binding.btnClear.setOnClickListener {
            binding.etMinPrice.text?.clear()
            binding.etMaxPrice.text?.clear()
            binding.tilMinPrice.error = null
            binding.tilMaxPrice.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "filter_request"
        const val ARG_MIN_PRICE = "min_price"
        const val ARG_MAX_PRICE = "max_price"
        const val NO_PRICE = -1
    }
}
