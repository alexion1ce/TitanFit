package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Упражнение внутри конкретной тренировки с параметрами подходов.
 *
 * @param order       порядок в списке тренировки (для сортировки)
 * @param sets        количество подходов
 * @param reps        целевые повторения (строка, т.к. может быть «8-12» или «30с»)
 * @param restSeconds отдых между подходами в секундах
 */
@Entity(
    tableName = "workout_exercises",
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["exerciseId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
)
