package com.example.fitapp.ui.navigation

/**
 * Маршруты навигации приложения.
 */
object Destinations {
    const val CATALOG = "catalog"
    const val PROGRAMS = "programs"
    const val JOURNAL = "journal"
    const val PROGRESS = "progress"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}"
    const val MY_WORKOUTS = "my_workouts"
    const val WORKOUT_EDITOR = "workout_editor/{workoutId}"
    const val EXERCISE_PICKER = "exercise_picker"
    const val PROGRAM_DETAIL = "program_detail/{workoutId}"
    const val ACTIVE_WORKOUT = "active_workout/{workoutId}"
    const val LOG_DETAIL = "log_detail/{logId}"

    fun exerciseDetail(id: Long) = "exercise/$id"

    /** workoutId = -1 для новой тренировки. */
    fun workoutEditor(workoutId: Long) = "workout_editor/$workoutId"

    fun programDetail(workoutId: Long) = "program_detail/$workoutId"

    fun activeWorkout(workoutId: Long) = "active_workout/$workoutId"

    fun logDetail(logId: Long) = "log_detail/$logId"
}
