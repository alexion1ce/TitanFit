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
                val cards = workouts.map { workout ->
                    val detail = workoutRepository.getDetail(workout.id)
                    val exercises = detail?.exercises.orEmpty()
                    val firstExercise = exercises.firstOrNull()
                    val muscleNames = exercises.map { it.muscleName }
                        .filter { it.isNotBlank() && it != "—" }
                        .distinct()
                    val muscleSummary = when {
                        muscleNames.isEmpty() -> "Без упражнений"
                        muscleNames.size == 1 -> muscleNames.first()
                        muscleNames.size == 2 -> muscleNames.joinToString(" + ")
                        else -> "${muscleNames.take(2).joinToString(" + ")} +${muscleNames.size - 2}"
                    }

                    MyWorkoutCardUi(
                        workout = workout,
                        exerciseCount = exercises.size,
                        exerciseCode = firstExercise?.exerciseCode.orEmpty(),
                        primaryMuscleCode = firstExercise?.primaryMuscleCode.orEmpty(),
                        secondaryMuscleCode = firstExercise?.secondaryMuscleCode,
                        muscleEmoji = firstExercise?.muscleEmoji ?: "🏋️",
                        muscleSummary = muscleSummary
                    )
                }
                MyWorkoutsUiState(
                    isLoading = false,
                    workoutCards = cards,
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
