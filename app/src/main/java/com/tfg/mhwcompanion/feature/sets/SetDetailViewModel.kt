package com.tfg.mhwcompanion.feature.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tfg.mhwcompanion.data.repository.ArmorRepository
import com.tfg.mhwcompanion.domain.model.ArmorSetSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetDetailViewModel(
    private val repository: ArmorRepository,
    private val setName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetDetailUiState(isLoading = true))
    val uiState: StateFlow<SetDetailUiState> = _uiState.asStateFlow()

    init {
        loadSetDetail()
    }

    private fun loadSetDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getArmorPieces(limit = 100)
                .onSuccess { armorPieces ->
                    val pieces = armorPieces
                        .filter { it.setName == setName }
                        .sortedBy { it.type }

                    val summary = if (pieces.isEmpty()) {
                        null
                    } else {
                        ArmorSetSummary(
                            name = setName,
                            pieceCount = pieces.size,
                            averageRarity = pieces.map { it.rarity }.average().toInt(),
                            totalBaseDefense = pieces.sumOf { it.defense },
                            kinds = pieces.map { it.type }.distinct().sorted(),
                            topSkills = pieces
                                .flatMap { piece ->
                                    piece.skillSummary
                                        .split("•")
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() }
                                }
                                .groupingBy { it }
                                .eachCount()
                                .entries
                                .sortedByDescending { it.value }
                                .take(4)
                                .map { it.key },
                            pieces = pieces
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            setSummary = summary,
                            errorMessage = if (summary == null) "No se encontró el set solicitado." else null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            setSummary = null,
                            errorMessage = "No se pudo cargar el detalle del set."
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(repository: ArmorRepository, setName: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SetDetailViewModel(repository, setName) as T
                }
            }
    }
}