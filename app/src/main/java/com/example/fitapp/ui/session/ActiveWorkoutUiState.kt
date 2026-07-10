package com.example.fitapp.ui.session

import com.example.fitapp.data.local.entity.SetLog

/**
 * Группа подходов одного упражнения в активной тренировке.
 */
data class ExerciseSetGroup(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleName: String,
    val muscleEmoji: String,
    val restSeconds: Int,
    val sets: List<SetLog>
)

data class ActiveWorkoutUiState(
    val isLoading: Boolean = true,
    val logId: Long = 0,
    val workoutName: String = "",
    val startedAt: Long = 0,
    val groups: List<ExerciseSetGroup> = emptyList(),
    val restTimer: RestTimerState = RestTimerState(),
    val isFinished: Boolean = false,
    val shouldExit: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Состояние таймера отдыха между подходами.
 * @param totalSeconds длительность отдыха (0 = таймер не активен)
 * @param remainingSeconds сколько осталось
 */
data class RestTimerState(
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isActive: Boolean = false,
    val endsAtMillis: Long = 0
)
