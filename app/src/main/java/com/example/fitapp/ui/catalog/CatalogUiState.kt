package com.example.fitapp.ui.catalog

import com.example.fitapp.data.local.entity.Exercise

/**
 * Карточка упражнения, обогащённая человекочитаемыми названиями
 * группы мышц и оборудования для отображения в UI.
 */
data class ExerciseCard(
    val exercise: Exercise,
    val muscleGroupName: String,
    val muscleEmoji: String,
    val equipmentName: String
)

data class CatalogUiState(
    val isLoading: Boolean = true,
    val exercises: List<ExerciseCard> = emptyList(),
    val muscleGroups: List<MuscleChip> = emptyList(),
    val equipment: List<EquipmentChip> = emptyList(),
    val selectedMuscle: String? = null,
    val selectedEquipment: String? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null
)
