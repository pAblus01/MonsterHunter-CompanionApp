package com.tfg.mhwcompanion.feature.consulta

import com.tfg.mhwcompanion.domain.model.ArmorPiece

data class ConsultaUiState(
    val isLoading: Boolean = false,
    val armorPieces: List<ArmorPiece> = emptyList(),
    val query: String = "",
    val selectedKind: String = KIND_ALL,
    val availableKinds: List<String> = listOf(KIND_ALL),
    val errorMessage: String? = null
) {
    companion object {
        const val KIND_ALL = "all"
    }
}