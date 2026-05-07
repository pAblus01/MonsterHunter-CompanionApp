package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.data.repository.RepositoryProvider
import com.tfg.mhwcompanion.databinding.FragmentSetsBinding
import kotlinx.coroutines.launch

class SetsFragment : Fragment() {

    private var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SetsViewModel by viewModels {
        SetsViewModel.factory(
            RepositoryProvider.armorRepository,
            LocalFavoriteSetStore(requireContext())
        )
    }

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
        binding.setsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteSetsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bindBuilderActions()
        observeUiState()
    }

    private fun bindBuilderActions() {
        binding.headSelector.setOnClickListener { showArmorPicker("head") }
        binding.chestSelector.setOnClickListener { showArmorPicker("chest") }
        binding.armsSelector.setOnClickListener { showArmorPicker("arms") }
        binding.waistSelector.setOnClickListener { showArmorPicker("waist") }
        binding.legsSelector.setOnClickListener { showArmorPicker("legs") }
        binding.saveFavoriteButton.setOnClickListener {
            val favoriteName = binding.favoriteNameInput.text?.toString().orEmpty().trim()
            if (favoriteName.isNotBlank()) {
                viewModel.saveCurrentBuildAsFavorite(favoriteName)
                binding.favoriteNameInput.setText("")
            }
        }
    }

    private fun showArmorPicker(type: String) {
        val options = viewModel.getArmorOptionsForType(type)
        if (options.isEmpty()) return

        val labels = options.map { option -> "${option.name} · R${option.rarity}" }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(getString(com.tfg.mhwcompanion.R.string.sets_picker_title, type))
            .setItems(labels) { _, which ->
                viewModel.selectArmorPiece(type, options[which])
            }
            .show()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (_binding == null) return@collect
                    binding.progressBar.isVisible = state.isLoading
                    binding.setsRecyclerView.adapter = ArmorSetSummaryAdapter(state.sets) { selectedSet ->
                        val action = SetsFragmentDirections.actionSetsFragmentToSetDetailFragment(selectedSet.name)
                        findNavController().navigate(action)
                    }
                    binding.emptyStateText.text = state.errorMessage
                        ?: getString(com.tfg.mhwcompanion.R.string.sets_empty_state)
                    binding.emptyStateText.isVisible = !state.isLoading && state.sets.isEmpty()
                    binding.favoriteSetsRecyclerView.adapter = FavoriteArmorSetAdapter(
                        state.favoriteSets,
                        onOpen = { favorite ->
                            viewModel.applyFavoriteToBuild(favorite)
                            val action = SetsFragmentDirections.actionSetsFragmentToFavoriteSetDetailFragment(favorite.name)
                            findNavController().navigate(action)
                        },
                        onEdit = { favorite -> showRenameDialog(favorite.name) },
                        onDelete = { favorite -> viewModel.deleteFavorite(favorite.name) }
                    )
                    renderSelectedBuild(state)
                }
            }
        }
    }

    private fun showRenameDialog(currentName: String) {
        val input = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            setText(currentName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(com.tfg.mhwcompanion.R.string.favorite_rename_title))
            .setView(input)
            .setPositiveButton(com.tfg.mhwcompanion.R.string.favorite_rename_confirm) { _, _ ->
                val updatedName = input.text?.toString().orEmpty().trim()
                if (updatedName.isNotBlank()) {
                    viewModel.renameFavorite(currentName, updatedName)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun renderSelectedBuild(state: SetsUiState) {
        binding.headSelector.text = state.buildSlots.firstOrNull { it.type == "head" }?.selectedPiece?.name
            ?: getString(com.tfg.mhwcompanion.R.string.sets_select_piece, "Head")
        binding.chestSelector.text = state.buildSlots.firstOrNull { it.type == "chest" }?.selectedPiece?.name
            ?: getString(com.tfg.mhwcompanion.R.string.sets_select_piece, "Chest")
        binding.armsSelector.text = state.buildSlots.firstOrNull { it.type == "arms" }?.selectedPiece?.name
            ?: getString(com.tfg.mhwcompanion.R.string.sets_select_piece, "Arms")
        binding.waistSelector.text = state.buildSlots.firstOrNull { it.type == "waist" }?.selectedPiece?.name
            ?: getString(com.tfg.mhwcompanion.R.string.sets_select_piece, "Waist")
        binding.legsSelector.text = state.buildSlots.firstOrNull { it.type == "legs" }?.selectedPiece?.name
            ?: getString(com.tfg.mhwcompanion.R.string.sets_select_piece, "Legs")
        binding.currentBuildStats.text = getString(
            com.tfg.mhwcompanion.R.string.sets_current_build_stats,
            state.buildDefense,
            state.buildAverageRarity,
            state.buildSkills.joinToString(separator = " • ").ifBlank {
                getString(com.tfg.mhwcompanion.R.string.armor_skills_empty)
            }
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
