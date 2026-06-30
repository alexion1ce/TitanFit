package com.example.fitapp.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.local.entity.WorkoutExercise

/**
 * Тренировка со списком входящих в неё упражнений.
 * Получается через @Relation-запрос.
 */
data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<WorkoutExercise>
)
