package com.example.fitapp.ui.catalog

import com.example.fitapp.data.local.entity.Difficulty
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.Workout

data class ExerciseCard(
    val exercise: Exercise,
    val muscleGroupName: String,
    val muscleEmoji: String,
    val equipmentName: String,
    val isFavorite: Boolean = false,
    val lastUsedAt: Long? = null,
    val quickAddCount: Int = 0
)

enum class CatalogCollection(val label: String) {
    ALL("Все"),
    FAVORITES("Избранное"),
    RECENT("Недавние")
}

data class CatalogUiState(
    val isLoading: Boolean = true,
    val exercises: List<ExerciseCard> = emptyList(),
    val recommendedExercises: List<ExerciseCard> = emptyList(),
    val favoriteExercises: List<ExerciseCard> = emptyList(),
    val recentExercises: List<ExerciseCard> = emptyList(),
    val customWorkouts: List<Workout> = emptyList(),
    val muscleGroups: List<MuscleChip> = emptyList(),
    val equipment: List<EquipmentChip> = emptyList(),
    val difficulties: List<Difficulty> = Difficulty.entries,
    val selectedCollection: CatalogCollection = CatalogCollection.ALL,
    val selectedMuscle: String? = null,
    val selectedEquipment: String? = null,
    val selectedDifficulty: Difficulty? = null,
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val recommendationTitle: String = "Рекомендовано сегодня",
    val recommendationSubtitle: String = "Упражнения для быстрого старта",
    val errorMessage: String? = null
)
