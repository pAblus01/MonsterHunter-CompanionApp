package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tfg.mhwcompanion.R
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
        binding.sectionTitle.text = getString(R.string.sets_title)
        binding.sectionDescription.text = getString(R.string.sets_description)
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
        
        binding.saveSetButton.setOnClickListener {
            showSaveSetDialog()
        }
        
        binding.loadSetButton.setOnClickListener {
            showLoadSetDialog()
        }

        binding.viewBuildDetailsButton.setOnClickListener {
            val selectedIds = viewModel.uiState.value.buildSlots
                .mapNotNull { it.selectedPiece?.id }
                .toIntArray()
            
            if (selectedIds.isNotEmpty()) {
                val action = SetsFragmentDirections.actionSetsFragmentToBuildDetailFragment(selectedIds)
                findNavController().navigate(action)
            }
        }
    }

    private fun showSaveSetDialog() {
        val context = requireContext()
        val textInputLayout = TextInputLayout(context).apply {
            setPadding(48, 16, 48, 0)
            hint = getString(R.string.sets_save_prompt_message)
        }
        val input = TextInputEditText(context)
        textInputLayout.addView(input)

        AlertDialog.Builder(context)
            .setTitle(R.string.sets_save_prompt_title)
            .setView(textInputLayout)
            .setPositiveButton(R.string.favorite_rename_confirm) { _, _ ->
                val name = input.text?.toString().orEmpty().trim()
                if (name.isNotBlank()) {
                    viewModel.saveCurrentBuildAsFavorite(name)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showLoadSetDialog() {
        val favorites = viewModel.uiState.value.favoriteSets
        if (favorites.isEmpty()) {
            Toast.makeText(requireContext(), R.string.sets_load_no_favorites, Toast.LENGTH_SHORT).show()
            return
        }

        val names = favorites.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.sets_load_prompt_title)
            .setItems(names) { _, which ->
                viewModel.applyFavoriteToBuild(favorites[which])
            }
            .show()
    }

    private fun showArmorPicker(type: String) {
        val options = viewModel.getArmorOptionsForType(type)
        if (options.isEmpty()) return

        val labels = options.map { option -> "${option.name} · R${option.rarity}" }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sets_picker_title, type))
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
                        ?: getString(R.string.sets_empty_state)
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

                    binding.saveSetButton.isEnabled = !state.isBuildEmpty
                    binding.loadSetButton.isEnabled = state.favoriteSets.isNotEmpty()
                    binding.viewBuildDetailsButton.isEnabled = !state.isBuildEmpty

                    renderSelectedBuild(state)
                }
            }
        }
    }

    private fun showRenameDialog(currentName: String) {
        val input = TextInputEditText(requireContext()).apply {
            setText(currentName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.favorite_rename_title))
            .setView(input)
            .setPositiveButton(R.string.favorite_rename_confirm) { _, _ ->
                val updatedName = input.text?.toString().orEmpty().trim()
                if (updatedName.isNotBlank()) {
                    viewModel.renameFavorite(currentName, updatedName)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun renderSelectedBuild(state: SetsUiState) {
        binding.headSelector.text = state.buildSlots.find { it.type == "head" }?.selectedPiece?.name
            ?: getString(R.string.sets_select_piece, "Head")
        binding.chestSelector.text = state.buildSlots.find { it.type == "chest" }?.selectedPiece?.name
            ?: getString(R.string.sets_select_piece, "Chest")
        binding.armsSelector.text = state.buildSlots.find { it.type == "arms" }?.selectedPiece?.name
            ?: getString(R.string.sets_select_piece, "Arms")
        binding.waistSelector.text = state.buildSlots.find { it.type == "waist" }?.selectedPiece?.name
            ?: getString(R.string.sets_select_piece, "Waist")
        binding.legsSelector.text = state.buildSlots.find { it.type == "legs" }?.selectedPiece?.name
            ?: getString(R.string.sets_select_piece, "Legs")
        
        binding.currentBuildStats.text = getString(
            R.string.sets_current_build_stats,
            state.buildDefense,
            state.buildAverageRarity,
            state.buildSkills.joinToString(separator = " • ").ifBlank {
                getString(R.string.armor_skills_empty)
            }
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
