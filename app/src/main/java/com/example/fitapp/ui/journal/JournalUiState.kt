package com.example.fitapp.ui.journal

import com.example.fitapp.data.local.entity.WorkoutLog

/** Карточка тренировки в списке журнала с человекочитаемой датой. */
data class JournalEntry(
    val log: WorkoutLog,
    val dateText: String,
    val durationText: String
)

data class JournalUiState(
    val isLoading: Boolean = true,
    val entries: List<JournalEntry> = emptyList(),
    val errorMessage: String? = null
)
