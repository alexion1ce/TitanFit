package com.example.fitapp.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.Difficulty
import com.example.fitapp.data.repository.EquipmentRepository
import com.example.fitapp.data.repository.ExerciseCatalogRepository
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val equipmentRepository: EquipmentRepository,
    private val catalogRepository: ExerciseCatalogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID передаётся через Navigation arguments (Long).
    private val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: -1L

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    init {
        loadExercise()
    }

    private fun loadExercise() {
        viewModelScope.launch {
            try {
                val exercise = exerciseRepository.getById(exerciseId)
                if (exercise == null) {
                    _uiState.value = ExerciseDetailUiState(
                        isLoading = false,
                        errorMessage = "Упражнение не найдено"
                    )
                    return@launch
                }
                catalogRepository.markUsed(exercise.id)

                val muscles = muscleGroupRepository.getAll().associateBy { it.code }
                val equipment = equipmentRepository.getAll().associateBy { it.code }

                _uiState.value = ExerciseDetailUiState(
                    isLoading = false,
                    exerciseCode = exercise.code,
                    name = exercise.name,
                    description = exercise.description,
                    technique = exercise.technique,
                    primaryMuscleCode = exercise.primaryMuscleCode,
                    primaryMuscle = muscles[exercise.primaryMuscleCode]?.name ?: "—",
                    primaryMuscleEmoji = muscles[exercise.primaryMuscleCode]?.emoji ?: "🏋️",
                    secondaryMuscleCode = exercise.secondaryMuscleCode,
                    secondaryMuscle = exercise.secondaryMuscleCode?.let { muscles[it]?.name },
                    equipment = equipment[exercise.equipmentCode]?.name ?: "—",
                    difficulty = Difficulty.fromName(exercise.difficulty),
                    videoUrl = exercise.videoUrl,
                    videoId = exercise.videoUrl?.let(::extractYouTubeId)
                )
            } catch (e: Exception) {
                _uiState.value = ExerciseDetailUiState(
                    isLoading = false,
                    errorMessage = "Не удалось загрузить: ${e.message}"
                )
            }
        }
    }

    /**
     * Извлекает 11-значный ID видео из разных форматов YouTube-ссылок:
     * youtu.be/<id>, watch?v=<id>, embed/<id>, shorts/<id>
     */
    private fun extractYouTubeId(url: String): String? {
        val patterns = listOf(
            Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
            Regex("""[?&]v=([A-Za-z0-9_-]{11})"""),
            Regex("""embed/([A-Za-z0-9_-]{11})"""),
            Regex("""shorts/([A-Za-z0-9_-]{11})""")
        )
        return patterns.firstNotNullOfOrNull { it.find(url)?.groupValues?.getOrNull(1) }
    }
}
