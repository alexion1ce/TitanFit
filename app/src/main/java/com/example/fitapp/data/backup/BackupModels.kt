package com.example.fitapp.data.backup

import com.example.fitapp.data.local.entity.UserProfile

/**
 * Portable copy of the user's own data. Exercises are referenced by their stable
 * code, not by database id, so a backup can be restored into a fresh install
 * (including one with a different application id).
 */
data class BackupData(
    val version: Int = CURRENT_VERSION,
    val exportedAt: Long,
    val appVersion: String,
    val profile: UserProfile?,
    val favorites: List<BackupFavorite>,
    val workouts: List<BackupWorkout>,
    val logs: List<BackupLog>
) {
    companion object {
        const val FORMAT = "titanfit-backup"
        const val CURRENT_VERSION = 1
    }
}

data class BackupFavorite(
    val exerciseCode: String,
    val isFavorite: Boolean,
    val lastUsedAt: Long?,
    val quickAddCount: Int
)

/** A custom workout template. [key] links logs to it inside one backup file. */
data class BackupWorkout(
    val key: Long,
    val name: String,
    val notes: String?,
    val isArchived: Boolean,
    val exercises: List<BackupWorkoutExercise>
)

data class BackupWorkoutExercise(
    val exerciseCode: String,
    val order: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
)

/** A logged session. It points either to a custom workout [workoutKey] or to a preset. */
data class BackupLog(
    val workoutKey: Long?,
    val presetCode: String?,
    val workoutName: String,
    val startedAt: Long,
    val finishedAt: Long?,
    val durationMin: Int?,
    val sets: List<BackupSet>
)

data class BackupSet(
    val exerciseCode: String,
    val exerciseOrder: Int,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val done: Boolean,
    val durationSeconds: Int?,
    val restSeconds: Int
)

data class ImportResult(
    val importedLogs: Int,
    val skippedDuplicateLogs: Int,
    val importedWorkouts: Int,
    val skippedSets: Int
)
