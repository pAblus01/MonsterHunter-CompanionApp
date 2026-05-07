package com.tfg.mhwcompanion.data.remote.dto

data class ArmorPieceDto(
    val id: Int,
    val name: String,
    val rank: String? = null,
    val rarity: Int? = null,
    val type: String? = null,
    val imageUrl: String? = null
)
