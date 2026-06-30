package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Группа мышц: грудь, спина, ноги и т.д.
 * [code] — короткий стабильный код (например "chest") для поиска в seed-данных.
 */
@Entity(tableName = "muscle_groups", indices = [Index(value = ["code"], unique = true)])
data class MuscleGroup(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val emoji: String
)
