package com.example.fitapp.ui.journal

import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.LoggedExerciseRow

data class LogDetailUiState(
    val isLoading: Boolean = true,
    val workoutName: String = "",
    val dateText: String = "",
    val durationText: String = "",
    val totalSets: Int = 0,
    val doneSets: Int = 0,
    val totalVolume: Double = 0.0,
    val exercises: List<LoggedExerciseRow> = emptyList(),
    val errorMessage: String? = null
)
