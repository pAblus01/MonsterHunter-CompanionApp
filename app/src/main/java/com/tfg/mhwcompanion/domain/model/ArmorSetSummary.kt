package com.tfg.mhwcompanion.domain.model

data class ArmorSetSummary(
    val name: String,
    val pieceCount: Int,
    val averageRarity: Int,
    val totalBaseDefense: Int,
    val kinds: List<String>,
    val topSkills: List<String>,
    val pieces: List<ArmorPiece>
)