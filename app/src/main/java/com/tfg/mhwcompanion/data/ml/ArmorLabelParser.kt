package com.tfg.mhwcompanion.data.ml

data class ParsedArmorLabel(
    val armorClassId: Int?,
    val armorId: Int?,
    val armorSlug: String,
    val displayName: String
)

object ArmorLabelParser {
    fun parse(label: String): ParsedArmorLabel {
        val normalized = label.trim()
        val segments = normalized.split("__")
        val armorClassId = segments.getOrNull(0)?.toIntOrNull() ?: normalized.toIntOrNull()
        val armorId = segments.getOrNull(2)?.toIntOrNull() ?: armorClassId
        val slug = if (segments.size >= 3) segments[1] else normalized
        val displayName = slug
            .split('-')
            .filter { it.isNotBlank() }
            .joinToString(" ") { token -> token.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
            .ifBlank { if (armorClassId != null) "Clase $armorClassId" else normalized }

        return ParsedArmorLabel(
            armorClassId = armorClassId,
            armorId = armorId,
            armorSlug = slug,
            displayName = displayName
        )
    }
}
