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
                            detectedSlots = result.slots.map { slot ->
                                DetectedArmorSlotUiModel(
                                    slotType = slot.slotType,
                                    primaryLabel = slot.topPredictions.firstOrNull()?.label ?: slot.slotType,
                                    confidence = slot.topPredictions.firstOrNull()?.confidence ?: slot.detectorConfidence,
                                    alternatives = slot.topPredictions.drop(1).map { prediction -> prediction.label },
                                    armorName = slot.predictedArmor?.name,
                                    armorSetName = slot.predictedArmor?.setName,
                                    defense = slot.predictedArmor?.defense,
                                    rarity = slot.predictedArmor?.rarity
                                )
                            },
                            statusMessage = result.warningMessage ?: if (result.slots.isEmpty()) "No se detectaron piezas." else "Analisis completado.",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detectedSlots = emptyList(),
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
