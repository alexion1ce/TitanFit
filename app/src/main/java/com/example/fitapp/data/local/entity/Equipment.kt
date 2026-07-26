package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Тип оборудования: штанга, гантели, тренажёр и т.д.
 */
@Entity(tableName = "equipment", indices = [Index(value = ["code"], unique = true)])
data class Equipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String
)
