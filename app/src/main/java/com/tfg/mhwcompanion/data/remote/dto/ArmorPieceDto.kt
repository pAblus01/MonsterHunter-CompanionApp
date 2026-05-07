package com.tfg.mhwcompanion.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArmorPieceDto(
    val id: Int,
    val kind: String? = null,
    val name: String,
    val description: String? = null,
    val rank: String? = null,
    val rarity: Int? = null,
    val slots: List<Int>? = null,
    val defense: ArmorDefenseDto? = null,
    val armorSet: ArmorSetDto? = null,
    val skills: List<ArmorSkillEntryDto>? = null
)

@JsonClass(generateAdapter = true)
data class ArmorDefenseDto(
    val base: Int? = null,
    val max: Int? = null
)

@JsonClass(generateAdapter = true)
data class ArmorSetDto(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class ArmorSkillEntryDto(
    val level: Int? = null,
    val name: String? = null,
    val skill: ArmorSkillDto? = null
)

@JsonClass(generateAdapter = true)
data class ArmorSkillDto(
    val id: Int,
    val name: String,
    val kind: String? = null
)
