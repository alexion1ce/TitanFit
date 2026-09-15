package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Тренировка / программа.
 *
 * @param type     CUSTOM (пользовательская) или PRESET (встроенная)
 * @param notes    заметки пользователя
 */
@Entity(tableName = "workouts", indices = [Index(value = ["presetCode"], unique = true)])
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val notes: String? = null,
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false,
    val presetCode: String? = null
)
