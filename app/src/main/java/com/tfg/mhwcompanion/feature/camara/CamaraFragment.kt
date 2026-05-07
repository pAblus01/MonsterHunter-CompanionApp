package com.tfg.mhwcompanion.feature.camara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tfg.mhwcompanion.databinding.FragmentCamaraBinding

class CamaraFragment : Fragment() {

    private var _binding: FragmentCamaraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionTitle.text = getString(com.tfg.mhwcompanion.R.string.camara_title)
        binding.sectionDescription.text = getString(com.tfg.mhwcompanion.R.string.camara_description)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
