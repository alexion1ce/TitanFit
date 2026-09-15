package com.example.fitapp.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun refresh() {
        load()
    }



    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val current = _uiState.value
            try {
                _uiState.value = current.copy(
                    isLoading = false,
                    errorMessage = null,
                    stats = workoutLogRepository.getOverallStats(current.selectedPeriod.weeksCount),
                    weeklyVolume = workoutLogRepository.getWeeklyVolume(current.selectedPeriod.weeksCount),
                    recentWorkouts = workoutLogRepository.getRecentWorkoutSummaries(20),
                    records = workoutLogRepository.getPersonalRecords()
                )
            } catch (e: CancellationException) {
                throw e
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
        loadJob?.cancel()
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.value = current.copy(isResetting = true, errorMessage = null)
            try {
                workoutLogRepository.resetProgress()
                _uiState.value = ProgressUiState(
                    isLoading = false,
                    selectedPeriod = current.selectedPeriod
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isResetting = false,
                    errorMessage = "Ошибка сброса: ${e.message}"
                )
            }
        }
    }
}
