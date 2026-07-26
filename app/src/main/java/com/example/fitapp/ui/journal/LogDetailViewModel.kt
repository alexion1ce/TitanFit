package com.example.fitapp.ui.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val logId: Long = savedStateHandle.get<Long>("logId") ?: -1L
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    private val _uiState = MutableStateFlow(LogDetailUiState())
    val uiState: StateFlow<LogDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val detail = workoutLogRepository.getLogDetail(logId)
                if (detail == null) {
                    _uiState.value = LogDetailUiState(
                        isLoading = false,
                        errorMessage = "Запись не найдена"
                    )
                    return@launch
                }
                _uiState.value = LogDetailUiState(
                    isLoading = false,
                    workoutName = detail.log.workoutName,
                    dateText = dateFormat.format(Date(detail.log.startedAt)),
                    durationText = detail.log.durationMin?.let { "$it мин" } ?: "—",
                    totalSets = detail.totalSets,
                    doneSets = detail.doneSets,
                    totalVolume = detail.totalVolume,
                    exercises = detail.exercises
                )
            } catch (e: Exception) {
                _uiState.value = LogDetailUiState(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }
}
