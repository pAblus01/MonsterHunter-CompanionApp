package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tfg.mhwcompanion.databinding.FragmentSetsBinding

class SetsFragment : Fragment() {

    private var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionTitle.text = getString(com.tfg.mhwcompanion.R.string.sets_title)
        binding.sectionDescription.text = getString(com.tfg.mhwcompanion.R.string.sets_description)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
