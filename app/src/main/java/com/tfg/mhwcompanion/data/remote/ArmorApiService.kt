package com.tfg.mhwcompanion.data.remote

import com.tfg.mhwcompanion.data.remote.dto.ArmorPieceDto
import retrofit2.http.GET

interface ArmorApiService {

    @GET("armor")
    suspend fun getArmorPieces(): List<ArmorPieceDto>
}
