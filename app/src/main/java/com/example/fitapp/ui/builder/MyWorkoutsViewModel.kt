package com.example.fitapp.ui.builder

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
class MyWorkoutsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MyWorkoutsUiState> =
        workoutRepository.observeCustomWorkouts()
            .map { workouts ->
                MyWorkoutsUiState(
                    isLoading = false,
                    workouts = workouts,
                    errorMessage = _errorMessage.value
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyWorkoutsUiState()
            )

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkout(id)
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось удалить: ${e.message}"
            }
        }
    }
}
