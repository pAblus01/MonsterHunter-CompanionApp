package com.tfg.mhwcompanion.feature.sets

import com.tfg.mhwcompanion.domain.model.ArmorPiece

data class BuildSlotUiModel(
    val type: String,
    val selectedPiece: ArmorPiece?
)