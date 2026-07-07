package com.example.fitapp.ui.builder

import com.example.fitapp.data.local.entity.Workout

data class MyWorkoutCardUi(
    val workout: Workout,
    val exerciseCount: Int = 0,
    val exerciseCode: String = "",
    val primaryMuscleCode: String = "",
    val secondaryMuscleCode: String? = null,
    val muscleEmoji: String = "🏋️",
    val muscleSummary: String = "Без упражнений"
)

data class MyWorkoutsUiState(
    val isLoading: Boolean = true,
    val workoutCards: List<MyWorkoutCardUi> = emptyList(),
    val errorMessage: String? = null
)
