package com.example.fitapp.ui.today

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.data.repository.WorkoutLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodayViewModel @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    // Set changes do not touch workout_logs, so the screen also reloads on resume.
    private val refreshTick = MutableStateFlow(0)

    val uiState: StateFlow<TodayUiState> = combine(
        workoutLogRepository.observeAllLogs(),
        userProfileRepository.profileFlow,
        userProfileRepository.selectedProgramIdFlow,
        refreshTick
    ) { logs, profile, selectedProgramId, _ -> Triple(logs, profile.daysPerWeek, selectedProgramId) }
        .mapLatest { (logs, weeklyGoal, selectedProgramId) ->
            try {
                TodayUiState(
                    isLoading = false,
                    snapshot = workoutLogRepository.getTodaySnapshot(logs, selectedProgramId, weeklyGoal)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Today load failed", e)
                TodayUiState(isLoading = false, errorMessage = "Не удалось загрузить данные. Попробуйте открыть экран ещё раз.")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodayUiState()
        )

    fun refresh() {
        refreshTick.value += 1
    }

    private companion object {
        const val TAG = "TodayViewModel"
    }
}
