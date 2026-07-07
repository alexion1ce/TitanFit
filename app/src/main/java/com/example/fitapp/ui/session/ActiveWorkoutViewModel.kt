package com.example.fitapp.ui.session

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import com.example.fitapp.data.repository.WorkoutLogRepository
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val workoutLogDao: WorkoutLogDao,
    private val setLogDao: SetLogDao,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // workoutId — какой шаблон запускаем (если logId нет, создаём новый лог).
    private val workoutIdArg: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startSession()
    }

    private fun startSession() {
        viewModelScope.launch {
            try {
                // Создаём новый лог тренировки из шаблона
                val logId = workoutLogRepository.startWorkout(workoutIdArg)
                loadSession(logId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Не удалось начать тренировку: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadSession(logId: Long) {
        val log = workoutLogDao.getById(logId)
        if (log == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Лог тренировки не найден"
            )
            return
        }

        val sets = setLogDao.getByLog(logId)
        val exerciseIds = sets.map { it.exerciseId }.distinct()
        val exercises = exerciseRepository.getByIds(exerciseIds).associateBy { it.id }
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }
        val detail = workoutRepository.getDetail(workoutIdArg)
        // Параметры отдыха по упражнению
        val restByExercise = detail?.exercises?.associate { it.exerciseId to it.restSeconds } ?: emptyMap()

        // Группируем подходы по упражнению (в порядке первого появления)
        val orderedExerciseIds = sets.map { it.exerciseId }.distinct()
        val groups = orderedExerciseIds.mapNotNull { exId ->
            val ex = exercises[exId] ?: return@mapNotNull null
            val muscle = muscles[ex.primaryMuscleCode]
            ExerciseSetGroup(
                exerciseId = exId,
                exerciseName = ex.name,
                muscleName = muscle?.name ?: "—",
                muscleEmoji = muscle?.emoji ?: "🏋️",
                restSeconds = restByExercise[exId] ?: 60,
                sets = sets.filter { it.exerciseId == exId }.sortedBy { it.setNumber }
            )
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            logId = logId,
            workoutName = log.workoutName,
            startedAt = log.startedAt,
            groups = groups,
            errorMessage = null
        )
    }

    /** Отметить подход выполненным (или снять отметку) и запустить таймер отдыха. */
    fun toggleSetDone(setLog: SetLog) {
        viewModelScope.launch {
            val updated = setLog.copy(done = !setLog.done)
            workoutLogRepository.updateSet(updated)
            // Обновляем локальное состояние
            updateSetInState(updated)
            // Если отметили как выполненный — запускаем таймер отдыха
            if (updated.done) {
                val restSec = _uiState.value.groups
                    .find { it.exerciseId == setLog.exerciseId }?.restSeconds ?: 60
                startRestTimer(restSec)
            }
        }
    }

    fun onWeightChanged(setLog: SetLog, weight: Double) {
        viewModelScope.launch {
            val updated = setLog.copy(weight = weight)
            workoutLogRepository.updateSet(updated)
            updateSetInState(updated)
        }
    }

    fun onRepsChanged(setLog: SetLog, reps: Int) {
        viewModelScope.launch {
            val updated = setLog.copy(reps = reps)
            workoutLogRepository.updateSet(updated)
            updateSetInState(updated)
        }
    }

    fun addSets(exerciseId: Long, count: Int) {
        viewModelScope.launch {
            try {
                val logId = _uiState.value.logId
                workoutLogRepository.addSets(logId, exerciseId, count)
                loadSession(logId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Не удалось добавить подходы: ${e.message}"
                )
            }
        }
    }

    private fun startRestTimer(totalSeconds: Int) {
        timerJob?.cancel()
        val endsAtMillis = System.currentTimeMillis() + totalSeconds * 1_000L
        _uiState.value = _uiState.value.copy(
            restTimer = RestTimerState(
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds,
                isActive = true,
                endsAtMillis = endsAtMillis
            )
        )
        RestTimerNotifications.scheduleFinishedNotification(appContext, endsAtMillis)
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = remainingSecondsUntil(endsAtMillis)
                _uiState.value = _uiState.value.copy(
                    restTimer = _uiState.value.restTimer.copy(remainingSeconds = remaining)
                )
                if (remaining <= 0) break
                delay(500)
            }
            _uiState.value = _uiState.value.copy(
                restTimer = _uiState.value.restTimer.copy(remainingSeconds = 0, isActive = false)
            )
        }
    }

    private fun remainingSecondsUntil(endsAtMillis: Long): Int {
        val millisLeft = endsAtMillis - System.currentTimeMillis()
        return if (millisLeft <= 0L) 0 else ((millisLeft + 999L) / 1_000L).toInt()
    }

    fun stopRestTimer() {
        timerJob?.cancel()
        RestTimerNotifications.cancelFinishedNotification(appContext)
        _uiState.value = _uiState.value.copy(
            restTimer = RestTimerState(isActive = false)
        )
    }

    fun finishWorkout() {
        viewModelScope.launch {
            RestTimerNotifications.cancelFinishedNotification(appContext)
            val saved = workoutLogRepository.finishWorkout(_uiState.value.logId)
            _uiState.value = if (saved) {
                _uiState.value.copy(isFinished = true)
            } else {
                _uiState.value.copy(errorMessage = "Не удалось сохранить тренировку в журнал")
            }
        }
    }

    private fun updateSetInState(updated: SetLog) {
        val groups = _uiState.value.groups.map { group ->
            if (group.exerciseId == updated.exerciseId) {
                group.copy(sets = group.sets.map { if (it.id == updated.id) updated else it })
            } else group
        }
        _uiState.value = _uiState.value.copy(groups = groups)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
