package com.tfg.mhwcompanion.feature.sets

import com.tfg.mhwcompanion.domain.model.FavoriteArmorSet
import com.tfg.mhwcompanion.domain.model.ArmorSetSummary

data class SetsUiState(
    val isLoading: Boolean = false,
    val sets: List<ArmorSetSummary> = emptyList(),
    val availableArmor: List<com.tfg.mhwcompanion.domain.model.ArmorPiece> = emptyList(),
    val buildSlots: List<BuildSlotUiModel> = emptyList(),
    val favoriteSets: List<FavoriteArmorSet> = emptyList(),
    val buildDefense: Int = 0,
    val buildAverageRarity: Int = 0,
    val buildSkills: List<String> = emptyList(),
    val errorMessage: String? = null
)