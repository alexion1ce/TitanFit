package com.example.fitapp.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import android.util.Log
import com.example.fitapp.data.backup.BackupRepository
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
    private val workoutLogRepository: WorkoutLogRepository,
    private val backupRepository: BackupRepository
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
                val snapshot = workoutLogRepository.getProgressSnapshot(current.selectedPeriod.weeksCount)
                _uiState.value = current.copy(
                    isLoading = false,
                    errorMessage = null,
                    stats = snapshot.stats,
                    weeklyVolume = snapshot.weeklyVolume,
                    recentWorkouts = snapshot.recentWorkouts,
                    records = snapshot.records
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Progress load failed", e)
                _uiState.value = current.copy(
                    isLoading = false,
                    errorMessage = "Не удалось посчитать статистику. Попробуйте открыть экран ещё раз."
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
                Log.e(TAG, "Progress reset failed", e)
                _uiState.value = current.copy(
                    isResetting = false,
                    errorMessage = "Не удалось сбросить прогресс. Данные не изменены."
                )
            }
        }
    }

    fun exportData(uri: Uri) = runBackup {
        backupRepository.exportTo(uri)
        "Данные сохранены в файл. Храните его вне телефона, например в облаке."
    }

    fun importData(uri: Uri) = runBackup {
        val result = backupRepository.importFrom(uri)
        load()
        buildString {
            append("Импортировано тренировок в журнал: ${result.importedLogs}")
            if (result.importedWorkouts > 0) append(", своих программ: ${result.importedWorkouts}")
            if (result.skippedDuplicateLogs > 0) append(". Уже были на телефоне: ${result.skippedDuplicateLogs}")
            if (result.skippedSets > 0) append(". Пропущено подходов с неизвестными упражнениями: ${result.skippedSets}")
            append('.')
        }
    }

    fun dismissBackupMessage() {
        _uiState.value = _uiState.value.copy(backupMessage = null)
    }

    private fun runBackup(action: suspend () -> String) {
        if (_uiState.value.isBackupBusy) return
        _uiState.value = _uiState.value.copy(isBackupBusy = true, backupMessage = null)
        viewModelScope.launch {
            val message = try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Backup rejected", e)
                e.message ?: "Файл не подходит для импорта."
            } catch (e: Exception) {
                Log.e(TAG, "Backup failed", e)
                "Не удалось выполнить операцию с файлом. Данные на телефоне не изменены."
            }
            _uiState.value = _uiState.value.copy(isBackupBusy = false, backupMessage = message)
        }
    }

    private companion object {
        const val TAG = "ProgressViewModel"
    }
}
