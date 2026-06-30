package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Тренировка / программа.
 *
 * @param type     CUSTOM (пользовательская) или PRESET (встроенная)
 * @param notes    заметки пользователя
 */
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val notes: String? = null
)
