package com.tfg.mhwcompanion.data.repository

import com.tfg.mhwcompanion.data.remote.ArmorApiService
import com.tfg.mhwcompanion.domain.model.ArmorPiece
import org.json.JSONObject
import java.util.Locale

class ArmorRepositoryImpl(
    private val apiService: ArmorApiService
) : ArmorRepository {

    private val cache = mutableMapOf<String, List<ArmorPiece>>()

    override suspend fun getArmorPieces(locale: String, limit: Int): Result<List<ArmorPiece>> {
        return runCatching {
            val cacheKey = "$locale-$limit"
            cache.getOrPut(cacheKey) {
                apiService.getArmorPieces(locale = locale, limit = limit).map(::mapArmorPiece)
            }
        }
    }

    override suspend fun getArmorPieceById(id: Int, locale: String, limit: Int): Result<ArmorPiece> {
        return getArmorPieces(locale = locale, limit = limit).mapCatching { pieces ->
            pieces.firstOrNull { it.id == id }
                ?: throw NoSuchElementException("No se encontró una armadura con id=$id")
        }
    }

    override suspend fun searchArmorPieces(
        locale: String,
        query: String,
        kind: String?,
        limit: Int
    ): Result<List<ArmorPiece>> {
        val trimmedQuery = query.trim()
        val normalizedKind = kind?.takeUnless { it.equals("all", ignoreCase = true) || it.isBlank() }

        if (trimmedQuery.isBlank() && normalizedKind == null) {
            return getArmorPieces(locale = locale, limit = limit)
        }

        return runCatching {
            val remoteQuery = buildRemoteQuery(trimmedQuery, normalizedKind, limit)
            apiService.searchArmorPieces(locale = locale, query = remoteQuery)
                .map(::mapArmorPiece)
        }.recoverCatching {
            val fallbackItems = getArmorPieces(locale = locale, limit = 100).getOrThrow()
            fallbackItems.filter { armorPiece ->
                matchesRemoteFallbackQuery(armorPiece, trimmedQuery) &&
                    matchesRemoteFallbackKind(armorPiece, normalizedKind)
            }.take(limit)
        }
    }

    override fun clearCache() {
        cache.clear()
    }

    private fun mapArmorPiece(dto: com.tfg.mhwcompanion.data.remote.dto.ArmorPieceDto): ArmorPiece {
        return ArmorPiece(
            id = dto.id,
            name = dto.name,
            setName = dto.armorSet?.name.orEmpty(),
            rank = dto.rank
                ?.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
                }
                .orEmpty(),
            rarity = dto.rarity ?: 0,
            type = dto.kind.orEmpty(),
            slots = dto.slots.orEmpty(),
            defense = dto.defense?.base ?: 0,
            skillSummary = dto.skills
                .orEmpty()
                .take(2)
                .joinToString(separator = " • ") { skill ->
                    skill.skill?.name ?: skill.name.orEmpty()
                },
            imageUrl = null
        )
    }

    private fun buildRemoteQuery(query: String, kind: String?, limit: Int): String {
        val json = JSONObject()
        if (kind != null) {
            json.put("kind", kind)
        }
        json.put("limit", limit)
        if (query.isNotBlank()) {
            json.put("name", query)
        }
        return json.toString()
    }

    private fun matchesRemoteFallbackQuery(item: ArmorPiece, query: String): Boolean {
        if (query.isBlank()) return true
        return item.name.contains(query, ignoreCase = true) ||
            item.setName.contains(query, ignoreCase = true) ||
            item.skillSummary.contains(query, ignoreCase = true)
    }

    private fun matchesRemoteFallbackKind(item: ArmorPiece, kind: String?): Boolean {
        return kind == null || item.type.equals(kind, ignoreCase = true)
    }
}
