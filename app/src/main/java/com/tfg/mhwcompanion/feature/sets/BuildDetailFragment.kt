package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.data.repository.RepositoryProvider
import com.tfg.mhwcompanion.databinding.FragmentBuildDetailBinding
import com.tfg.mhwcompanion.feature.consulta.ArmorPieceAdapter
import kotlinx.coroutines.launch

class BuildDetailFragment : Fragment() {

    private var _binding: FragmentBuildDetailBinding? = null
    private val binding get() = _binding!!
    private val args by lazy { BuildDetailFragmentArgs.fromBundle(requireArguments()) }
    private val viewModel: BuildDetailViewModel by viewModels {
        BuildDetailViewModel.factory(
            RepositoryProvider.armorRepository,
            args.armorPieceIds
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buildPiecesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (_binding == null) return@collect
                    binding.buildPiecesRecyclerView.adapter = ArmorPieceAdapter(state.pieces)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
