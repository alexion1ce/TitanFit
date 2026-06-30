package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Выполненный подход в рамках конкретной тренировки-лога.
 *
 * @param logId        ссылка на WorkoutLog
 * @param exerciseId   упражнение
 * @param setNumber    номер подхода в этом упражнении
 * @param weight       фактический вес (кг)
 * @param reps         фактически выполненные повторения
 * @param done         отмечен ли подход как выполненный
 */
@Entity(
    tableName = "set_logs",
    indices = [
        Index(value = ["logId"]),
        Index(value = ["exerciseId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutLog::class,
            parentColumns = ["id"],
            childColumns = ["logId"],
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
data class SetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val logId: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val done: Boolean = false
)
