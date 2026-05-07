package com.tfg.mhwcompanion.data.repository

import com.tfg.mhwcompanion.data.remote.ArmorApiService
import com.tfg.mhwcompanion.domain.model.ArmorPiece

class ArmorRepositoryImpl(
    private val apiService: ArmorApiService
) : ArmorRepository {

    override suspend fun getArmorPieces(): Result<List<ArmorPiece>> {
        return runCatching {
            apiService.getArmorPieces().map { dto ->
                ArmorPiece(
                    id = dto.id,
                    name = dto.name,
                    rank = dto.rank.orEmpty(),
                    rarity = dto.rarity ?: 0,
                    type = dto.type.orEmpty(),
                    imageUrl = dto.imageUrl
                )
            }
        }
    }
}
