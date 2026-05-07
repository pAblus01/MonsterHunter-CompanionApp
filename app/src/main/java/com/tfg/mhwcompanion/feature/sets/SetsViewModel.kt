package com.tfg.mhwcompanion.feature.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tfg.mhwcompanion.data.repository.ArmorRepository
import com.tfg.mhwcompanion.domain.model.ArmorPiece
import com.tfg.mhwcompanion.domain.model.FavoriteArmorSet
import com.tfg.mhwcompanion.domain.model.ArmorSetSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetsViewModel(
    private val repository: ArmorRepository,
    private val favoriteSetStore: LocalFavoriteSetStore
) : ViewModel() {

    private val buildTypes = listOf("head", "chest", "arms", "waist", "legs")

    private val _uiState = MutableStateFlow(SetsUiState(isLoading = true))
    val uiState: StateFlow<SetsUiState> = _uiState.asStateFlow()

    init {
        loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getArmorPieces(limit = 100)
                .onSuccess { armorPieces ->
                    val sets = armorPieces
                        .filter { it.setName.isNotBlank() }
                        .groupBy { it.setName }
                        .map { (setName, pieces) ->
                            ArmorSetSummary(
                                name = setName,
                                pieceCount = pieces.size,
                                averageRarity = pieces.map { it.rarity }.average().toInt(),
                                totalBaseDefense = pieces.sumOf { it.defense },
                                kinds = pieces.map { it.type }.distinct().sorted(),
                                topSkills = pieces
                                    .flatMap { piece ->
                                        piece.skillSummary
                                            .split("•")
                                            .map { it.trim() }
                                            .filter { it.isNotBlank() }
                                    }
                                    .groupingBy { it }
                                    .eachCount()
                                    .entries
                                    .sortedByDescending { it.value }
                                    .take(3)
                                    .map { it.key },
                                pieces = pieces.sortedBy { it.type }
                            )
                        }
                        .sortedByDescending { it.averageRarity }

                    val favoriteSets = favoriteSetStore.getFavorites()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sets = sets,
                            availableArmor = armorPieces,
                            buildSlots = buildTypes.map { type ->
                                BuildSlotUiModel(
                                    type = type,
                                    selectedPiece = null
                                )
                            },
                            favoriteSets = favoriteSets,
                            errorMessage = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sets = emptyList(),
                            errorMessage = "No se pudieron cargar los sets."
                        )
                    }
                }
        }
    }

    fun selectArmorPiece(type: String, armorPiece: ArmorPiece?) {
        _uiState.update { state ->
            val updatedSlots = state.buildSlots.map { slot ->
                if (slot.type == type) {
                    slot.copy(selectedPiece = armorPiece)
                } else {
                    slot
                }
            }
            val stats = buildStats(updatedSlots)

            state.copy(
                buildSlots = updatedSlots,
                buildDefense = stats.defense,
                buildAverageRarity = stats.averageRarity,
                buildSkills = stats.skills
            )
        }
    }

    fun applyFavoriteToBuild(favoriteArmorSet: FavoriteArmorSet) {
        val updatedSlots = listOf(
            BuildSlotUiModel("head", favoriteArmorSet.head),
            BuildSlotUiModel("chest", favoriteArmorSet.chest),
            BuildSlotUiModel("arms", favoriteArmorSet.arms),
            BuildSlotUiModel("waist", favoriteArmorSet.waist),
            BuildSlotUiModel("legs", favoriteArmorSet.legs)
        )
        val stats = buildStats(updatedSlots)

        _uiState.update { state ->
            state.copy(
                buildSlots = updatedSlots,
                buildDefense = stats.defense,
                buildAverageRarity = stats.averageRarity,
                buildSkills = stats.skills
            )
        }
    }

    fun deleteFavorite(name: String) {
        val updatedFavorites = _uiState.value.favoriteSets.filterNot { it.name == name }
        favoriteSetStore.saveFavorites(updatedFavorites)
        _uiState.update { it.copy(favoriteSets = updatedFavorites) }
    }

    fun renameFavorite(oldName: String, newName: String) {
        val updatedFavorites = _uiState.value.favoriteSets.map { favorite ->
            if (favorite.name == oldName) favorite.copy(name = newName) else favorite
        }
        favoriteSetStore.saveFavorites(updatedFavorites)
        _uiState.update { it.copy(favoriteSets = updatedFavorites) }
    }

    private data class BuildStats(
        val defense: Int,
        val averageRarity: Int,
        val skills: List<String>
    )

    private fun buildStats(buildSlots: List<BuildSlotUiModel>): BuildStats {
        val selectedPieces = buildSlots.mapNotNull { it.selectedPiece }
        val defense = selectedPieces.sumOf { it.defense }
        val averageRarity = if (selectedPieces.isEmpty()) 0 else selectedPieces.sumOf { it.rarity } / selectedPieces.size
        val skills = selectedPieces
            .flatMap { piece ->
                piece.skillSummary
                    .split("•")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(4)
            .map { it.key }

        return BuildStats(defense, averageRarity, skills)
    }

    fun saveCurrentBuildAsFavorite(name: String) {
        val state = _uiState.value
        val currentFavorite = FavoriteArmorSet(
            name = name,
            head = state.buildSlots.firstOrNull { it.type == "head" }?.selectedPiece,
            chest = state.buildSlots.firstOrNull { it.type == "chest" }?.selectedPiece,
            arms = state.buildSlots.firstOrNull { it.type == "arms" }?.selectedPiece,
            waist = state.buildSlots.firstOrNull { it.type == "waist" }?.selectedPiece,
            legs = state.buildSlots.firstOrNull { it.type == "legs" }?.selectedPiece
        )

        val updatedFavorites = state.favoriteSets + currentFavorite
        favoriteSetStore.saveFavorites(updatedFavorites)

        _uiState.update {
            it.copy(favoriteSets = updatedFavorites)
        }
    }

    fun getArmorOptionsForType(type: String): List<ArmorPiece> {
        return _uiState.value.availableArmor
            .filter { it.type.equals(type, ignoreCase = true) }
            .sortedByDescending { it.rarity }
    }

    companion object {
        fun factory(
            repository: ArmorRepository,
            favoriteSetStore: LocalFavoriteSetStore
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SetsViewModel(repository, favoriteSetStore) as T
                }
            }
    }
}