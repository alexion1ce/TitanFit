package com.example.fitapp.ui.catalog

import com.example.fitapp.data.local.entity.Difficulty

data class ExerciseDetailUiState(
    val isLoading: Boolean = true,
    val exerciseCode: String = "",
    val name: String = "",
    val description: String = "",
    val technique: String = "",
    val primaryMuscleCode: String = "",
    val primaryMuscle: String = "",
    val primaryMuscleEmoji: String = "",
    val secondaryMuscleCode: String? = null,
    val secondaryMuscle: String? = null,
    val equipment: String = "",
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val videoUrl: String? = null,
    val videoId: String? = null,
    val errorMessage: String? = null
)
