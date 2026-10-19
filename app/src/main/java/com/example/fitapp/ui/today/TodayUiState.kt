package com.example.fitapp.ui.today

import com.example.fitapp.data.repository.TodaySnapshot

data class TodayUiState(
    val isLoading: Boolean = true,
    val snapshot: TodaySnapshot? = null,
    val errorMessage: String? = null
)
