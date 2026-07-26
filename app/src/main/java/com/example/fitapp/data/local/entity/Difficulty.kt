package com.example.fitapp.data.local.entity

/**
 * Уровень сложности упражнения.
 */
enum class Difficulty(val displayName: String) {
    BEGINNER("Новичок"),
    INTERMEDIATE("Средний"),
    ADVANCED("Продвинутый");

    companion object {
        fun fromName(name: String): Difficulty =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: BEGINNER
    }
}
