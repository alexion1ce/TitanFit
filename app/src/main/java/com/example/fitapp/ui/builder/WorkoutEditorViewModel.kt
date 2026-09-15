package com.example.fitapp.ui.builder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import com.example.fitapp.data.repository.WorkoutExerciseItem
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutEditorViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutIdArg: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    private val _uiState = MutableStateFlow(WorkoutEditorUiState())
    val uiState: StateFlow<WorkoutEditorUiState> = _uiState.asStateFlow()

    init {
        if (workoutIdArg == -1L) {
            _uiState.value = WorkoutEditorUiState(
                isLoading = false,
                isNewWorkout = true,
                workoutName = "Новая тренировка"
            )
        } else {
            loadWorkout(workoutIdArg)
        }

        // Слушаем результат пикера: ключ "picked_exercise_ids" устанавливается
        // в NavGraph через navController.previousBackStackEntry?.savedStateHandle.
        // StateFlow сам фильтрует одинаковые значения, поэтому distinctUntilChanged не нужен.
    }

    /**
     * Превращает набор ID упражнений в WorkoutExerciseItem с дефолтными параметрами
     * и добавляет в текущий список (без дублей).
     */
    private suspend fun addExerciseIds(ids: Set<Long>) {
        val exercises = exerciseRepository.getByIds(ids.toList())
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }

        val current = _uiState.value.exercises.toMutableList()
        val existingIds = current.map { it.exerciseId }.toSet()

        for (ex in exercises) {
            if (ex.id !in existingIds) {
                val muscle = muscles[ex.primaryMuscleCode]
                current.add(
                    WorkoutExerciseItem(
                        workoutExerciseId = 0,
                        exerciseId = ex.id,
                        exerciseCode = ex.code,
                        equipmentCode = ex.equipmentCode,
                        exerciseName = ex.name,
                        muscleName = muscle?.name ?: "—",
                        muscleEmoji = muscle?.emoji ?: "🏋️",
                        order = current.size,
                        sets = 3,
                        reps = "12",
                        restSeconds = 60
                    )
                )
            }
        }
        val reordered = current.mapIndexed { i, item -> item.copy(order = i) }
        _uiState.value = _uiState.value.copy(exercises = reordered)
    }

    private fun loadWorkout(id: Long) {
        viewModelScope.launch {
            try {
                val detail = workoutRepository.getDetail(id)
                if (detail == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Тренировка не найдена"
                    )
                    return@launch
                }
                _uiState.value = WorkoutEditorUiState(
                    isLoading = false,
                    isNewWorkout = false,
                    workoutId = detail.id,
                    workoutName = detail.name,
                    workoutNotes = detail.notes ?: "",
                    exercises = detail.exercises
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(workoutName = name)
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(workoutNotes = notes)
    }

    fun onRemoveExercise(index: Int) {
        val current = _uiState.value.exercises.toMutableList()
        if (index !in current.indices) return
        val removed = current.removeAt(index)
        val reordered = current.mapIndexed { i, ex -> ex.copy(order = i) }
        _uiState.value = _uiState.value.copy(exercises = reordered, drafts = _uiState.value.drafts - removed.exerciseId)
    }

    fun onExerciseParametersChanged(index: Int, draft: ExerciseParameterDraft) {
        val state = _uiState.value
        val item = state.exercises.getOrNull(index) ?: return
        _uiState.value = state.copy(drafts = state.drafts + (item.exerciseId to draft))
    }
    fun onMoveExercise(fromIndex: Int, toIndex: Int) {
        val current = _uiState.value.exercises.toMutableList()
        if (fromIndex !in current.indices || toIndex !in current.indices) return
        val item = current.removeAt(fromIndex)
        current.add(toIndex, item)
        val reordered = current.mapIndexed { i, ex -> ex.copy(order = i) }
        _uiState.value = _uiState.value.copy(exercises = reordered)
    }

    fun addPickedExerciseIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            try {
                addExerciseIds(ids)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Ошибка добавления упражнений: ${e.message}"
                )
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        if (state.workoutName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Введите название тренировки")
            return
        }
        if (state.exercises.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Добавьте хотя бы одно упражнение")
            return
        }

        val drafts = state.exercises.map { state.drafts[it.exerciseId] ?: it.parameterDraft() }
        if (drafts.any { !it.isValid }) {
            _uiState.value = state.copy(errorMessage = "Проверьте параметры: подходы 1–100, повторения или секунды 1–9999 (можно диапазон 8–12), отдых 0–3600 секунд")
            return
        }
        val items = state.exercises.zip(drafts) { item, draft ->
            item.copy(sets = draft.sets.toInt(), reps = draft.reps.trim(), restSeconds = draft.rest.toInt())
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            try {
                val id = workoutRepository.saveWorkoutWithExercises(
                    workoutId = state.workoutId,
                    name = state.workoutName,
                    notes = state.workoutNotes,
                    items = items
                )
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    workoutId = id,
                    isNewWorkout = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Ошибка сохранения: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    private fun updateExerciseAt(index: Int, transform: (WorkoutExerciseItem) -> WorkoutExerciseItem) {
        val current = _uiState.value.exercises.toMutableList()
        if (index in current.indices) {
            current[index] = transform(current[index])
            _uiState.value = _uiState.value.copy(exercises = current)
        }
    }

    companion object {
        /** Ключ в SavedStateHandle для передачи выбранных упражнений из пикера. */
        const val KEY_PICKED_IDS = "picked_exercise_ids"
    }
}
