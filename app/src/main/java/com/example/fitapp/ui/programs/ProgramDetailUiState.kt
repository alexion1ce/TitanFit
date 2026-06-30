package com.example.fitapp.ui.programs

import com.example.fitapp.data.repository.WorkoutExerciseItem

data class ProgramDetailUiState(
    val isLoading: Boolean = true,
    val workoutId: Long = 0,
    val name: String = "",
    val description: String = "",
    val exercises: List<WorkoutExerciseItem> = emptyList(),
    val totalSets: Int = 0,
    val errorMessage: String? = null
)
