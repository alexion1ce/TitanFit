package com.example.fitapp.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProgramsUiState> =
        workoutRepository.observePresets()
            .map { workouts ->
                // Для каждой программы получаем детали, чтобы посчитать кол-во упражнений и подходов.
                val cards = workouts.map { w ->
                    val detail = workoutRepository.getDetail(w.id)
                    ProgramCard(
                        workout = w,
                        exerciseCount = detail?.exercises?.size ?: 0,
                        totalSets = detail?.exercises?.sumOf { it.sets } ?: 0
                    )
                }
                ProgramsUiState(
                    isLoading = false,
                    programs = cards,
                    errorMessage = _errorMessage.value
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProgramsUiState()
            )
}
