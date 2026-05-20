package com.tfg.mhwcompanion.feature.camara

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tfg.mhwcompanion.data.ml.ArmorRecognitionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CamaraViewModel(
    private val recognitionRepository: ArmorRecognitionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CamaraUiState())
    val uiState: StateFlow<CamaraUiState> = _uiState.asStateFlow()

    fun onCameraPermissionChanged(isGranted: Boolean) {
        _uiState.update {
            it.copy(
                hasCameraPermission = isGranted,
                errorMessage = if (isGranted) it.errorMessage else "Activa el permiso de camara para hacer una foto directa."
            )
        }
    }

    fun onImageCaptured(uri: Uri) {
        _uiState.update {
            it.copy(
                capturedImageUri = uri,
                errorMessage = null,
                statusMessage = "Foto lista para analizar."
            )
        }
    }

    fun analyzeCapturedImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    statusMessage = "Analizando armadura..."
                )
            }

            recognitionRepository.recognize(bitmap)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detectedArmors = result.detections.map { detection ->
                                DetectedArmorUiModel(
                                    armorClassId = detection.armorClassId,
                                    primaryLabel = detection.label,
                                    confidence = detection.detectorConfidence,
                                    alternatives = emptyList(),
                                    armorName = detection.predictedArmor?.name,
                                    armorSetName = detection.predictedArmor?.setName,
                                    defense = detection.predictedArmor?.defense,
                                    rarity = detection.predictedArmor?.rarity
                                )
                            },
                            statusMessage = result.warningMessage ?: if (result.detections.isEmpty()) "No se detectó ninguna armadura." else "Análisis completado.",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detectedArmors = emptyList(),
                            errorMessage = throwable.message ?: "No se pudo analizar la imagen.",
                            statusMessage = null
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(recognitionRepository: ArmorRecognitionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CamaraViewModel(recognitionRepository) as T
                }
            }
    }
}
