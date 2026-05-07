package com.tfg.mhwcompanion.feature.consulta

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

class ConsultaViewModel(
    private val repository: ArmorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultaUiState(isLoading = true))
    val uiState: StateFlow<ConsultaUiState> = _uiState.asStateFlow()

    init {
        refreshArmorPieces()
    }

    fun refreshArmorPieces() {
        viewModelScope.launch {
            loadArmorPieces()
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { state -> state.copy(query = query) }
        viewModelScope.launch {
            loadArmorPieces()
        }
    }

    fun onKindSelected(kind: String) {
        _uiState.update { state -> state.copy(selectedKind = kind) }
        viewModelScope.launch {
            loadArmorPieces()
        }
    }

    private suspend fun loadArmorPieces() {
        val state = _uiState.value
        _uiState.update { current ->
            current.copy(isLoading = true, errorMessage = null)
        }

        val selectedKind = state.selectedKind
            .takeUnless { it == ConsultaUiState.KIND_ALL }

        repository.searchArmorPieces(
            query = state.query,
            kind = selectedKind,
            limit = 30
        ).onSuccess { armorPieces ->
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    armorPieces = armorPieces,
                    availableKinds = buildKinds(armorPieces),
                    errorMessage = null
                )
            }
        }.onFailure {
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    armorPieces = emptyList(),
                    errorMessage = "No se pudo cargar el catálogo de armaduras."
                )
            }
        }
    }

    private fun buildKinds(items: List<ArmorPiece>): List<String> {
        return listOf(ConsultaUiState.KIND_ALL) + items
            .map { it.type }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    companion object {
        fun factory(repository: ArmorRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ConsultaViewModel(repository) as T
                }
            }
    }
}