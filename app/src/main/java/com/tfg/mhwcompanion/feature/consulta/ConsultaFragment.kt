package com.tfg.mhwcompanion.feature.consulta

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.data.repository.RepositoryProvider
import com.tfg.mhwcompanion.databinding.FragmentConsultaBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ConsultaFragment : Fragment() {

    private var _binding: FragmentConsultaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConsultaViewModel by viewModels {
        ConsultaViewModel.factory(RepositoryProvider.armorRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionTitle.text = getString(com.tfg.mhwcompanion.R.string.consulta_title)
        binding.sectionDescription.text = getString(com.tfg.mhwcompanion.R.string.consulta_description)

        binding.armorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bindSearch()
        observeUiState()
    }

    private fun bindSearch() {
        binding.searchInputLayout.editText?.setOnEditorActionListener { textView, actionId, event ->
            val shouldSubmit = actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)

            if (shouldSubmit) {
                viewModel.onQueryChanged(textView.text?.toString().orEmpty())
            }

            shouldSubmit
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (_binding == null) return@collect
                    binding.progressBar.isVisible = state.isLoading
                    binding.armorRecyclerView.adapter = ArmorPieceAdapter(state.armorPieces)

                    val emptyMessage = state.errorMessage ?: getString(com.tfg.mhwcompanion.R.string.consulta_empty_state)
                    binding.emptyStateText.text = emptyMessage
                    binding.emptyStateText.isVisible = !state.isLoading && state.armorPieces.isEmpty()

                    renderKindFilters(state)
                }
            }
        }
    }

    private fun renderKindFilters(state: ConsultaUiState) {
        binding.kindChipGroup.removeAllViews()

        state.availableKinds.forEach { kind ->
            val chip = Chip(requireContext()).apply {
                text = if (kind == ConsultaUiState.KIND_ALL) {
                    getString(com.tfg.mhwcompanion.R.string.consulta_filter_all)
                } else {
                    kind.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    }
                }
                isCheckable = true
                isChecked = kind == state.selectedKind
                setOnClickListener { viewModel.onKindSelected(kind) }
            }

            binding.kindChipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
