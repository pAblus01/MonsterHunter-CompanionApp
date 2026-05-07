package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.data.repository.RepositoryProvider
import com.tfg.mhwcompanion.databinding.FragmentSetDetailBinding
import kotlinx.coroutines.launch

class SetDetailFragment : Fragment() {

    private var _binding: FragmentSetDetailBinding? = null
    private val binding get() = _binding!!
    private val args by lazy { SetDetailFragmentArgs.fromBundle(requireArguments()) }
    private val viewModel: SetDetailViewModel by viewModels {
        SetDetailViewModel.factory(
            RepositoryProvider.armorRepository,
            args.setName
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.piecesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (_binding == null) return@collect

                    binding.progressBar.isVisible = state.isLoading
                    binding.errorText.isVisible = !state.isLoading && state.setSummary == null
                    binding.errorText.text = state.errorMessage

                    val summary = state.setSummary
                    binding.contentGroup.isVisible = !state.isLoading && summary != null

                    if (summary != null) {
                        binding.setTitle.text = summary.name
                        binding.setMeta.text = getString(
                            R.string.sets_item_meta,
                            summary.pieceCount,
                            summary.averageRarity,
                            summary.totalBaseDefense
                        )
                        binding.setKinds.text = getString(
                            R.string.sets_item_kinds,
                            summary.kinds.joinToString(separator = " · ")
                        )
                        binding.setSkills.text = summary.topSkills.joinToString(separator = " • ")
                            .ifBlank { getString(R.string.armor_skills_empty) }
                        binding.piecesRecyclerView.adapter = com.tfg.mhwcompanion.feature.consulta.ArmorPieceAdapter(summary.pieces)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}