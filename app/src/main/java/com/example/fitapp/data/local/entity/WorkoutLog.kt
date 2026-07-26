package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запись о выполненной тренировке (одна сессия в зале).
 *
 * @param startedAt   epoch-миллисекунды начала
 * @param finishedAt  epoch-миллисекунды окончания (null если ещё идёт)
 * @param durationMin фактическая длительность в минутах (заполняется при завершении)
 */
@Entity(
    tableName = "workout_logs",
    indices = [Index(value = ["workoutId"])],
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val workoutName: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val durationMin: Int? = null
)
