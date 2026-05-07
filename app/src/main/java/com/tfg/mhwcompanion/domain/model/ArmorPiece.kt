package com.tfg.mhwcompanion.domain.model

data class ArmorPiece(
    val id: Int,
    val name: String,
    val setName: String,
    val rank: String,
    val rarity: Int,
    val type: String,
    val slots: List<Int>,
    val defense: Int,
    val skillSummary: String,
    val imageUrl: String?
)
