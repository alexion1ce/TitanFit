package com.example.fitapp.ui.builder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.ui.components.userMessage
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
    private var nextEditorKey = 1L
    private fun newKey() = nextEditorKey++
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
     * и добавляет в конец списка. Повторное упражнение допускается только из пикера:
     * пользователь выбрал его явно.
     */
    private suspend fun addExerciseIds(ids: Set<Long>, allowDuplicates: Boolean) {
        val exercises = exerciseRepository.getByIds(ids.toList())
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }

        val current = _uiState.value.exercises.toMutableList()
        val existingIds = current.map { it.exerciseId }.toSet()

        for (ex in exercises) {
            if (allowDuplicates || ex.id !in existingIds) {
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
                        restSeconds = 60,
                        editorKey = newKey()
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
                    exercises = detail.exercises.map { it.copy(editorKey = newKey()) }
                )
            } catch (e: Exception) {
                Log.e("WorkoutEditorViewModel", "Operation failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Не удалось загрузить тренировку."
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
        _uiState.value = _uiState.value.copy(exercises = reordered, drafts = _uiState.value.drafts - removed.editorKey)
    }

    fun onExerciseParametersChanged(index: Int, draft: ExerciseParameterDraft) {
        val state = _uiState.value
        val item = state.exercises.getOrNull(index) ?: return
        _uiState.value = state.copy(drafts = state.drafts + (item.editorKey to draft))
    }
    fun onMoveExercise(fromIndex: Int, toIndex: Int) {
        val current = _uiState.value.exercises.toMutableList()
        if (fromIndex !in current.indices || toIndex !in current.indices) return
        val item = current.removeAt(fromIndex)
        current.add(toIndex, item)
        val reordered = current.mapIndexed { i, ex -> ex.copy(order = i) }
        _uiState.value = _uiState.value.copy(exercises = reordered)
    }

    fun addPickedExerciseIds(ids: Set<Long>) = addIds(ids, allowDuplicates = true)

    /** Exercise passed from the catalog; added once even if the screen is recreated. */
    fun addInitialExercise(id: Long) {
        if (savedStateHandle.get<Boolean>(KEY_INITIAL_ADDED) == true) return
        savedStateHandle[KEY_INITIAL_ADDED] = true
        addIds(setOf(id), allowDuplicates = false)
    }

    private fun addIds(ids: Set<Long>, allowDuplicates: Boolean) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            try {
                addExerciseIds(ids, allowDuplicates)
            } catch (e: Exception) {
                Log.e("WorkoutEditorViewModel", "Operation failed", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Не удалось добавить упражнения."
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

        val drafts = state.exercises.map { state.drafts[it.editorKey] ?: it.parameterDraft() }
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
                Log.e("WorkoutEditorViewModel", "Operation failed", e)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = userMessage(e, "Не удалось сохранить тренировку. Попробуйте ещё раз.")
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
        private const val KEY_INITIAL_ADDED = "initial_exercise_added"
    }
}
