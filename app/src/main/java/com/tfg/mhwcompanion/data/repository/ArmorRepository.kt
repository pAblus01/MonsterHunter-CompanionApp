package com.tfg.mhwcompanion.data.repository

import com.tfg.mhwcompanion.domain.model.ArmorPiece

interface ArmorRepository {
    suspend fun getArmorPieces(): Result<List<ArmorPiece>>
}
