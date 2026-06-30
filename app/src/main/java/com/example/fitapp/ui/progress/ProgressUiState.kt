package com.example.fitapp.ui.progress

import com.example.fitapp.data.repository.OverallStats
import com.example.fitapp.data.repository.PersonalRecord
import com.example.fitapp.data.repository.WeeklyVolume

data class ProgressUiState(
    val isLoading: Boolean = true,
    val stats: OverallStats = OverallStats(0, 0, 0.0, 0),
    val weeklyVolume: List<WeeklyVolume> = emptyList(),
    val records: List<PersonalRecord> = emptyList(),
    val errorMessage: String? = null
)
