package com.example.fitapp.ui.builder

import com.example.fitapp.data.repository.WorkoutExerciseItem

data class WorkoutEditorUiState(
    val isLoading: Boolean = true,
    val isNewWorkout: Boolean = true,
    val workoutId: Long? = null,
    val workoutName: String = "",
    val workoutNotes: String = "",
    val exercises: List<WorkoutExerciseItem> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)
