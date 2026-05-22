package com.tfg.mhwcompanion.feature.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tfg.mhwcompanion.data.repository.ArmorRepository
import com.tfg.mhwcompanion.domain.model.ArmorPiece
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuildDetailUiState(
    val isLoading: Boolean = false,
    val pieces: List<ArmorPiece> = emptyList(),
    val errorMessage: String? = null
)

class BuildDetailViewModel(
    private val repository: ArmorRepository,
    private val armorPieceIds: IntArray
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuildDetailUiState(isLoading = true))
    val uiState: StateFlow<BuildDetailUiState> = _uiState.asStateFlow()

    init {
        loadPieces()
    }

    private fun loadPieces() {
        viewModelScope.launch {
            val pieces = mutableListOf<ArmorPiece>()
            var hasError = false
            
            for (id in armorPieceIds) {
                repository.getArmorPieceById(id)
                    .onSuccess { pieces.add(it) }
                    .onFailure { hasError = true }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    pieces = pieces,
                    errorMessage = if (hasError && pieces.isEmpty()) "Error al cargar las piezas." else null
                )
            }
        }
    }

    companion object {
        fun factory(
            repository: ArmorRepository,
            armorPieceIds: IntArray
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BuildDetailViewModel(repository, armorPieceIds) as T
                }
            }
    }
}
