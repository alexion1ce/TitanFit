package com.example.fitapp.ui.session

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.groupSetLogsInWorkoutOrder
import com.example.fitapp.data.repository.MuscleGroupRepository
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository,
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val workoutLogDao: WorkoutLogDao,
    private val setLogDao: SetLogDao,
    @ApplicationContext private val appContext: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val workoutIdArg: Long = savedStateHandle.get<Long>("workoutId") ?: -1L
    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState = _uiState.asStateFlow()
    private var timerJob: Job? = null
    private val pendingWrites = SessionWriteQueue()
    private var foreground = false

    init {
        viewModelScope.launch {
            try {
                loadSession(workoutLogRepository.resumeOrStartWorkout(workoutIdArg))
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Не удалось начать тренировку: ${e.message}")
            }
        }
    }

    fun setForeground(value: Boolean) {
        foreground = value
        RestTimerNotifications.isForeground = value
        val timer = _uiState.value.restTimer
        if (value && timer.isActive && timer.endsAtMillis > System.currentTimeMillis()) {
            val exact = RestTimerNotifications.scheduleFinishedNotification(appContext, timer.endsAtMillis)
            _uiState.value = _uiState.value.copy(timerNotice = RestTimerNotifications.backgroundNotice(appContext, exact))
        }
    }

    private fun enqueue(write: suspend () -> Unit) {
        pendingWrites.add(write)
        viewModelScope.launch { flushWrites() }
    }

    private suspend fun flushWrites(): Boolean {
        _uiState.value = _uiState.value.copy(isSaving = true)
        return try {
            pendingWrites.flush()
            _uiState.value = _uiState.value.copy(saveError = null)
            true
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(saveError = "Изменения не сохранены. Повторите сохранение перед выходом.")
            false
        } finally {
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun retrySave() { viewModelScope.launch { flushWrites() } }
    private fun currentSet(id: Long) = _uiState.value.groups.asSequence()
        .flatMap { it.sets.asSequence() }.firstOrNull { it.id == id }
    private fun draftFor(set: SetLog) = _uiState.value.drafts[set.id] ?: SetInputDraft.from(set)

    private suspend fun loadSession(logId: Long) {
        val log = workoutLogDao.getById(logId) ?: error("Тренировка не найдена")
        val sets = setLogDao.getByLog(logId)
        val exercises = exerciseRepository.getByIds(sets.map { it.exerciseId }.distinct()).associateBy { it.id }
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }
        val groups = groupSetLogsInWorkoutOrder(sets).mapNotNull { exerciseSets ->
            val first = exerciseSets.firstOrNull() ?: return@mapNotNull null
            val ex = exercises[first.exerciseId] ?: return@mapNotNull null
            val muscle = muscles[ex.primaryMuscleCode]
            ExerciseSetGroup(
                exerciseId = ex.id, exerciseOrder = first.exerciseOrder,
                exerciseCode = ex.code, primaryMuscleCode = ex.primaryMuscleCode,
                secondaryMuscleCode = ex.secondaryMuscleCode, equipmentCode = ex.equipmentCode,
                exerciseName = ex.name, muscleName = muscle?.name ?: "—",
                muscleEmoji = muscle?.emoji ?: "🏋️", restSeconds = first.restSeconds, sets = exerciseSets
            )
        }
        _uiState.value = _uiState.value.copy(isLoading = false, logId = logId,
            workoutName = log.workoutName, startedAt = log.startedAt, groups = groups, errorMessage = null)
        if (savedStateHandle.get<Long>(KEY_TIMER_ENDS_AT) == null && log.restTimerEndsAt != null && log.restTimerTotalSeconds != null) {
            savedStateHandle[KEY_TIMER_TOTAL_SECONDS] = log.restTimerTotalSeconds
            savedStateHandle[KEY_TIMER_ENDS_AT] = log.restTimerEndsAt
        }
        restoreRestTimerIfNeeded()
    }

    fun toggleSetDone(setLog: SetLog) {
        if (_uiState.value.isClosing) return
        val current = currentSet(setLog.id) ?: return
        if (!current.done && !draftFor(current).isValid(current.durationSeconds != null)) return
        val updated = current.copy(done = !current.done)
        updateSetInState(updated)
        enqueue { workoutLogRepository.updateSetDone(updated.id, updated.done) }
        if (updated.done) startRestTimer(updated.restSeconds)
    }

    fun onWeightTextChanged(setLog: SetLog, text: String) {
        if (_uiState.value.isClosing) return
        val current = currentSet(setLog.id) ?: return
        val draft = draftFor(current).copy(weight = text)
        _uiState.value = _uiState.value.copy(drafts = _uiState.value.drafts + (current.id to draft))
        val value = parseSetWeight(text) ?: return
        updateSetInState(current.copy(weight = value))
        enqueue { workoutLogRepository.updateSetWeight(current.id, value) }
    }

    fun onCountTextChanged(setLog: SetLog, text: String) {
        if (_uiState.value.isClosing) return
        val current = currentSet(setLog.id) ?: return
        val draft = draftFor(current).copy(count = text)
        _uiState.value = _uiState.value.copy(drafts = _uiState.value.drafts + (current.id to draft))
        val value = parseSetCount(text) ?: return
        if (current.durationSeconds != null) {
            updateSetInState(current.copy(durationSeconds = value))
            enqueue { workoutLogRepository.updateSetDuration(current.id, value) }
        } else {
            updateSetInState(current.copy(reps = value))
            enqueue { workoutLogRepository.updateSetReps(current.id, value) }
        }
    }

    fun addSets(exerciseId: Long, exerciseOrder: Int, count: Int) {
        if (_uiState.value.isClosing) return
        _uiState.value = _uiState.value.copy(isClosing = true)
        viewModelScope.launch {
            try {
                if (!flushWrites()) return@launch
                val logId = _uiState.value.logId
                workoutLogRepository.addSets(logId, exerciseId, exerciseOrder, count)
                loadSession(logId)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(saveError = "Не удалось добавить подходы. Попробуйте ещё раз.")
            } finally { _uiState.value = _uiState.value.copy(isClosing = false) }
        }
    }

    private fun updateSetInState(updated: SetLog) {
        _uiState.value = _uiState.value.copy(groups = _uiState.value.groups.map { group ->
            group.copy(sets = group.sets.map { if (it.id == updated.id) updated else it })
        })
    }

    private fun validDrafts(): Boolean {
        val valid = _uiState.value.groups.flatMap { it.sets }.all { draftFor(it).isValid(it.durationSeconds != null) }
        if (!valid) _uiState.value = _uiState.value.copy(saveError = "Исправьте пустые или недопустимые значения подходов.")
        return valid
    }

    private fun closeSession(action: suspend () -> Unit) {
        if (_uiState.value.isClosing || !validDrafts()) return
        _uiState.value = _uiState.value.copy(isClosing = true)
        viewModelScope.launch {
            try { if (flushWrites()) action()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(saveError = "Не удалось сохранить тренировку. Попробуйте ещё раз.")
            } finally { _uiState.value = _uiState.value.copy(isClosing = false) }
        }
    }

    fun finishWorkout() = finishWorkout(false)
    fun saveWorkoutAndExit() = finishWorkout(true)
    private fun finishWorkout(exitAfterSave: Boolean) = closeSession {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(restTimer = RestTimerState())
        RestTimerNotifications.cancelFinishedNotification(appContext)
        clearSavedTimer()
        workoutLogDao.updateRestTimer(_uiState.value.logId, null, null)
        val saved = workoutLogRepository.finishWorkout(_uiState.value.logId)
        _uiState.value = if (saved) _uiState.value.copy(isFinished = true, shouldExit = exitAfterSave)
            else _uiState.value.copy(saveError = "Не удалось сохранить тренировку в журнал")
    }

    fun cancelWorkout() {
        if (_uiState.value.isClosing) return
        _uiState.value = _uiState.value.copy(isClosing = true)
        viewModelScope.launch {
            try {
                pendingWrites.discardAnd {
                    timerJob?.cancel()
                    RestTimerNotifications.cancelFinishedNotification(appContext)
                    clearSavedTimer()
                    if (_uiState.value.logId > 0L) workoutLogRepository.deleteLog(_uiState.value.logId)
                }
                _uiState.value = _uiState.value.copy(shouldExit = true)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(saveError = "Не удалось отменить тренировку. Попробуйте ещё раз.")
            } finally { _uiState.value = _uiState.value.copy(isClosing = false) }
        }
    }

    fun continueLater() = closeSession {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(shouldExit = true)
    }

    private fun startRestTimer(totalSeconds: Int) {
        if (totalSeconds <= 0) { stopRestTimer(); return }
        startRestTimer(totalSeconds, System.currentTimeMillis() + totalSeconds * 1_000L)
    }

    private fun startRestTimer(totalSeconds: Int, endsAtMillis: Long) {
        timerJob?.cancel()
        val exact = RestTimerNotifications.scheduleFinishedNotification(appContext, endsAtMillis)
        _uiState.value = _uiState.value.copy(
            restTimer = RestTimerState(totalSeconds, remainingSecondsUntil(endsAtMillis), true, endsAtMillis),
            timerNotice = RestTimerNotifications.backgroundNotice(appContext, exact))
        savedStateHandle[KEY_TIMER_TOTAL_SECONDS] = totalSeconds
        savedStateHandle[KEY_TIMER_ENDS_AT] = endsAtMillis
        persistRestTimer(totalSeconds, endsAtMillis)
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = remainingSecondsUntil(endsAtMillis)
                _uiState.value = _uiState.value.copy(restTimer = _uiState.value.restTimer.copy(remainingSeconds = remaining))
                if (remaining <= 0) break
                delay(250)
            }
            if (foreground) RestTimerNotifications.signalForeground(appContext, endsAtMillis)
            clearSavedTimer()
            persistRestTimer(null, null)
            _uiState.value = _uiState.value.copy(restTimer = _uiState.value.restTimer.copy(remainingSeconds = 0, isActive = false))
        }
    }

    private fun remainingSecondsUntil(endsAtMillis: Long): Int {
        val left = endsAtMillis - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 999L) / 1_000L).toInt()
    }

    fun stopRestTimer() {
        timerJob?.cancel()
        RestTimerNotifications.cancelFinishedNotification(appContext)
        _uiState.value = _uiState.value.copy(restTimer = RestTimerState())
        clearSavedTimer()
        persistRestTimer(null, null)
    }

    private fun restoreRestTimerIfNeeded() {
        if (_uiState.value.restTimer.isActive) return
        val total = savedStateHandle.get<Int>(KEY_TIMER_TOTAL_SECONDS) ?: return
        val end = savedStateHandle.get<Long>(KEY_TIMER_ENDS_AT) ?: return
        if (remainingSecondsUntil(end) <= 0) { clearSavedTimer(); persistRestTimer(null, null); return }
        startRestTimer(total, end)
    }

    private fun clearSavedTimer() {
        savedStateHandle.remove<Int>(KEY_TIMER_TOTAL_SECONDS)
        savedStateHandle.remove<Long>(KEY_TIMER_ENDS_AT)
    }
    private fun persistRestTimer(total: Int?, end: Long?) {
        val logId = _uiState.value.logId
        if (logId > 0L) enqueue { workoutLogDao.updateRestTimer(logId, total, end) }
    }
    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
    private companion object {
        const val KEY_TIMER_TOTAL_SECONDS = "rest_timer_total_seconds"
        const val KEY_TIMER_ENDS_AT = "rest_timer_ends_at"
    }
}
