package com.tfg.mhwcompanion.data.repository

import com.tfg.mhwcompanion.domain.model.ArmorPiece

interface ArmorRepository {
    suspend fun getArmorPieces(locale: String = "en", limit: Int = 20): Result<List<ArmorPiece>>
    suspend fun searchArmorPieces(
        locale: String = "en",
        query: String = "",
        kind: String? = null,
        limit: Int = 20
    ): Result<List<ArmorPiece>>

    fun clearCache()
}
