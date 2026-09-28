package com.example.fitapp.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.WorkoutLog
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("ru"))

    val uiState: StateFlow<JournalUiState> =
        combine(workoutLogRepository.observeAllLogs(), _errorMessage) { logs, error ->
            val (finished, unfinished) = logs.partition { it.finishedAt != null }
            JournalUiState(
                isLoading = false,
                entries = finished.map { it.toEntry() },
                unfinished = unfinished.map { it.toEntry() },
                errorMessage = error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = JournalUiState()
        )

    fun deleteEntry(logId: Long) = runAction("Не удалось удалить запись. Попробуйте ещё раз.") {
        workoutLogRepository.deleteLog(logId)
    }

    /** Saves an unfinished session to the journal with the sets marked so far. */
    fun finishUnfinished(logId: Long) = runAction("Не удалось завершить тренировку. Попробуйте ещё раз.") {
        if (!workoutLogRepository.finishWorkout(logId)) error("Log $logId not found")
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun runAction(failureMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                _errorMessage.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, failureMessage, e)
                _errorMessage.value = failureMessage
            }
        }
    }

    private fun WorkoutLog.toEntry(): JournalEntry {
        val dateText = dateFormat.format(Date(startedAt))
        val durationText = durationMin?.let { "$it мин" } ?: "—"
        return JournalEntry(log = this, dateText = dateText, durationText = durationText)
    }

    private companion object {
        const val TAG = "JournalViewModel"
    }
}
