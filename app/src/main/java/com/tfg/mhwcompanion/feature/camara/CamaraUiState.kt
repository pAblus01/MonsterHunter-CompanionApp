package com.tfg.mhwcompanion.feature.camara

import android.net.Uri

data class CamaraUiState(
    val isLoading: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val capturedImageUri: Uri? = null,
    val detectedArmors: List<DetectedArmorUiModel> = emptyList(),
    val statusMessage: String? = null,
    val errorMessage: String? = null
)
