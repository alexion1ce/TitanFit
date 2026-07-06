package com.example.fitapp.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val current = _uiState.value
            try {
                _uiState.value = current.copy(
                    isLoading = false,
                    stats = workoutLogRepository.getOverallStats(),
                    weeklyVolume = workoutLogRepository.getWeeklyVolume(current.selectedPeriod.weeksCount),
                    recentWorkouts = workoutLogRepository.getRecentWorkoutSummaries(20),
                    records = workoutLogRepository.getPersonalRecords()
                )
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }

    fun onPeriodSelected(period: ProgressPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.value = _uiState.value.copy(selectedPeriod = period, isLoading = true)
        load()
    }

    fun toggleAllRecent() {
        _uiState.value = _uiState.value.copy(showAllRecent = !_uiState.value.showAllRecent)
    }

    fun toggleAllRecords() {
        _uiState.value = _uiState.value.copy(showAllRecords = !_uiState.value.showAllRecords)
    }

    fun resetProgress() {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.value = current.copy(isResetting = true, errorMessage = null)
            try {
                workoutLogRepository.resetProgress()
                _uiState.value = ProgressUiState(
                    isLoading = false,
                    selectedPeriod = current.selectedPeriod
                )
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isResetting = false,
                    errorMessage = "Ошибка сброса: ${e.message}"
                )
            }
        }
    }
}
