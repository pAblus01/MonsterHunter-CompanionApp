package com.tfg.mhwcompanion.domain.model

data class ArmorPiece(
    val id: Int,
    val name: String,
    val rank: String,
    val rarity: Int,
    val type: String,
    val imageUrl: String?
)
