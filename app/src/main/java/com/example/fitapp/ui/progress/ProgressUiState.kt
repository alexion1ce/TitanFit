package com.example.fitapp.ui.progress

import com.example.fitapp.data.repository.OverallStats
import com.example.fitapp.data.repository.PersonalRecord
import com.example.fitapp.data.repository.RecentWorkoutSummary
import com.example.fitapp.data.repository.WeeklyVolume

enum class ProgressPeriod(val weeksCount: Int) {
    WEEK(1),
    FOUR_WEEKS(4),
    TWELVE_WEEKS(12),
    YEAR(52)
}

data class ProgressUiState(
    val isLoading: Boolean = true,
    val stats: OverallStats = OverallStats(0, 0, 0.0, 0, 0),
    val weeklyVolume: List<WeeklyVolume> = emptyList(),
    val recentWorkouts: List<RecentWorkoutSummary> = emptyList(),
    val records: List<PersonalRecord> = emptyList(),
    val selectedPeriod: ProgressPeriod = ProgressPeriod.WEEK,
    val showAllRecent: Boolean = false,
    val showAllRecords: Boolean = false,
    val isResetting: Boolean = false,
    val errorMessage: String? = null
)
