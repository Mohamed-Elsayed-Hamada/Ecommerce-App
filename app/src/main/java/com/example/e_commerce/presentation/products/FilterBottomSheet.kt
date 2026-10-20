package com.example.e_commerce.presentation.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerce.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    var onApplyFilters: (
        minPrice: Int?,
        maxPrice: Int?,
        brand: String?,
        categories: List<String>?
    ) -> Unit = { _, _, _, _ -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetFilterBinding.inflate(
            inflater,
            container,
            false
        )

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

            // We will replace these with the actual
            // selected brand/category IDs later.
            val selectedBrand: String? = null

            val selectedCategories: List<String>? = null

            onApplyFilters(
                minPrice,
                maxPrice,
                selectedBrand,
                selectedCategories
            )

            dismiss()
        }

        binding.btnClear.setOnClickListener {

            binding.etMinPrice.text?.clear()
            binding.etMaxPrice.text?.clear()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}