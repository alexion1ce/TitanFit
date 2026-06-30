package com.example.fitapp.ui.programs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    private val _uiState = MutableStateFlow(ProgramDetailUiState())
    val uiState: StateFlow<ProgramDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val detail = workoutRepository.getDetail(workoutId)
                if (detail == null) {
                    _uiState.value = ProgramDetailUiState(
                        isLoading = false,
                        errorMessage = "Программа не найдена"
                    )
                    return@launch
                }
                _uiState.value = ProgramDetailUiState(
                    isLoading = false,
                    workoutId = detail.id,
                    name = detail.name,
                    description = detail.notes ?: "",
                    exercises = detail.exercises,
                    totalSets = detail.exercises.sumOf { it.sets }
                )
            } catch (e: Exception) {
                _uiState.value = ProgramDetailUiState(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }
}
