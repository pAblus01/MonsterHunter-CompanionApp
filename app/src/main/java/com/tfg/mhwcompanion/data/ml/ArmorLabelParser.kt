package com.tfg.mhwcompanion.data.ml

data class ParsedArmorLabel(
    val slotType: String,
    val armorId: Int?,
    val armorSlug: String,
    val displayName: String
)

object ArmorLabelParser {
    fun parse(label: String): ParsedArmorLabel {
        val segments = label.trim().split("__")
        val slotType = segments.getOrNull(0).orEmpty()
        val armorId = segments.getOrNull(2)?.toIntOrNull()
        val slug = segments.getOrNull(1).orEmpty()
        val displayName = slug
            .split('-')
            .filter { it.isNotBlank() }
            .joinToString(" ") { token -> token.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
            .ifBlank { label }

        return ParsedArmorLabel(
            slotType = slotType,
            armorId = armorId,
            armorSlug = slug,
            displayName = displayName
        )
    }
}
