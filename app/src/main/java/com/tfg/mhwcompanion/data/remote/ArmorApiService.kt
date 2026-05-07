package com.tfg.mhwcompanion.data.remote

import com.tfg.mhwcompanion.data.remote.dto.ArmorPieceDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArmorApiService {

    @GET("{locale}/armor")
    suspend fun getArmorPieces(
        @Path("locale") locale: String = DEFAULT_LOCALE,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE
    ): List<ArmorPieceDto>

    @GET("{locale}/armor")
    suspend fun searchArmorPieces(
        @Path("locale") locale: String = DEFAULT_LOCALE,
        @Query("q") query: String
    ): List<ArmorPieceDto>

    companion object {
        const val DEFAULT_LOCALE = "en"
        const val DEFAULT_PAGE_SIZE = 20
    }
}
