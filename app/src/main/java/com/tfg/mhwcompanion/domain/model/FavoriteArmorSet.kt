package com.tfg.mhwcompanion.domain.model

data class FavoriteArmorSet(
    val name: String,
    val head: ArmorPiece? = null,
    val chest: ArmorPiece? = null,
    val arms: ArmorPiece? = null,
    val waist: ArmorPiece? = null,
    val legs: ArmorPiece? = null
) {
    val pieces: List<ArmorPiece>
        get() = listOfNotNull(head, chest, arms, waist, legs)

    val totalDefense: Int
        get() = pieces.sumOf { it.defense }

    val averageRarity: Int
        get() = if (pieces.isEmpty()) 0 else pieces.sumOf { it.rarity } / pieces.size

    val topSkills: List<String>
        get() = pieces
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
}