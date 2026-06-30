package com.example.fitapp.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.WorkoutLog
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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
        workoutLogRepository.observeAllLogs()
            .map { logs ->
                JournalUiState(
                    isLoading = false,
                    // Показываем только завершённые тренировки
                    entries = logs
                        .filter { it.finishedAt != null }
                        .map { it.toEntry() },
                    errorMessage = _errorMessage.value
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = JournalUiState()
            )

    fun deleteEntry(logId: Long) {
        viewModelScope.launch {
            try {
                workoutLogRepository.deleteLog(logId)
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось удалить: ${e.message}"
            }
        }
    }

    private fun WorkoutLog.toEntry(): JournalEntry {
        val dateText = dateFormat.format(Date(startedAt))
        val durationText = durationMin?.let { "$it мин" } ?: "—"
        return JournalEntry(log = this, dateText = dateText, durationText = durationText)
    }
}
