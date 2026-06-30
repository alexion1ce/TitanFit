package com.example.fitapp.data.local.entity

/**
 * Тип тренировки.
 * CUSTOM — создана пользователем в конструкторе.
 * PRESET — встроенная готовая программа (Фаза 3).
 */
enum class WorkoutType(val storageKey: String) {
    CUSTOM("CUSTOM"),
    PRESET("PRESET");

    companion object {
        fun fromKey(key: String): WorkoutType =
            entries.find { it.storageKey.equals(key, ignoreCase = true) } ?: CUSTOM
    }
}
