package com.example.fitapp.ui.builder

import com.example.fitapp.data.local.entity.Workout

data class MyWorkoutsUiState(
    val isLoading: Boolean = true,
    val workouts: List<Workout> = emptyList(),
    val errorMessage: String? = null
)
