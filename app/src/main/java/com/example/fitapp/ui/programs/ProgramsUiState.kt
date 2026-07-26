package com.example.fitapp.ui.programs

import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.repository.WorkoutExerciseItem

/**
 * Карточка готовой программы с краткой сводкой.
 */
data class ProgramCard(
    val workout: Workout,
    val exerciseCount: Int,
    val totalSets: Int,
    val exercises: List<WorkoutExerciseItem> = emptyList()
)

data class ProgramsUiState(
    val isLoading: Boolean = true,
    val programs: List<ProgramCard> = emptyList(),
    val errorMessage: String? = null
)
