package com.tfg.mhwcompanion.feature.sets

import com.tfg.mhwcompanion.domain.model.ArmorSetSummary

data class SetDetailUiState(
    val isLoading: Boolean = false,
    val setSummary: ArmorSetSummary? = null,
    val errorMessage: String? = null
)