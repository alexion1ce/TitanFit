package com.example.fitapp.ui.builder

import com.example.fitapp.data.repository.WorkoutExerciseItem
import com.example.fitapp.data.repository.parseTargetCount

data class WorkoutEditorUiState(
    val isLoading: Boolean = true,
    val isNewWorkout: Boolean = true,
    val workoutId: Long? = null,
    val workoutName: String = "",
    val workoutNotes: String = "",
    val exercises: List<WorkoutExerciseItem> = emptyList(),
    val drafts: Map<Long, ExerciseParameterDraft> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class ExerciseParameterDraft(val sets: String, val reps: String, val rest: String) {
    val validSets: Boolean get() = sets.toIntOrNull()?.let { it in 1..100 } == true
    val validReps: Boolean get() = parseTargetCount(reps) != null
    val validRest: Boolean get() = rest.toIntOrNull()?.let { it in 0..3600 } == true
    val isValid: Boolean get() = validSets && validReps && validRest
}

fun WorkoutExerciseItem.parameterDraft() = ExerciseParameterDraft(sets.toString(), reps, restSeconds.toString())
