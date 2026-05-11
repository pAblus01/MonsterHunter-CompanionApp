package com.tfg.mhwcompanion.feature.camara

data class DetectedArmorSlotUiModel(
    val slotType: String,
    val primaryLabel: String,
    val confidence: Float,
    val alternatives: List<String>,
    val armorName: String? = null,
    val armorSetName: String? = null,
    val defense: Int? = null,
    val rarity: Int? = null
)
