package com.example.fitapp.data.local.entity

import androidx.room.ColumnInfo
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
        Index(value = ["exerciseId"]),
        Index(value = ["logId", "exerciseOrder", "setNumber"])
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
    @ColumnInfo(defaultValue = "0") val exerciseOrder: Int = 0,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val done: Boolean = false,
    val durationSeconds: Int? = null,
    @ColumnInfo(defaultValue = "60") val restSeconds: Int = 60
)
