package com.example.fitapp.ui.programs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.data.repository.isProgramCompatible
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val currentLocation: StateFlow<WorkoutLocation> =
        userProfileRepository.profileFlow
            .map { it.location }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutLocation.GYM)

    fun toggleLocation() {
        val next = when (userProfileRepository.loadProfile().location) {
            WorkoutLocation.GYM -> WorkoutLocation.HOME_DUMBBELLS
            WorkoutLocation.HOME_DUMBBELLS -> WorkoutLocation.HOME_BODYWEIGHT
            WorkoutLocation.HOME_BODYWEIGHT -> WorkoutLocation.GYM
        }
        userProfileRepository.updateLocation(next)
    }

    val uiState: StateFlow<ProgramsUiState> =
        combine(workoutRepository.observePresets(), userProfileRepository.profileFlow) { workouts, profile ->
                val details = workoutRepository.getDetails(workouts.map { it.id })
                val cards = workouts.map { w ->
                    val detail = details[w.id]
                    ProgramCard(
                        workout = w,
                        exerciseCount = detail?.exercises?.size ?: 0,
                        totalSets = detail?.exercises?.sumOf { it.sets } ?: 0,
                        exercises = detail?.exercises.orEmpty()
                    )
                }
                val ranked = cards.filter { card ->
                    isProgramCompatible(card.exercises.map { it.equipmentCode }, profile.location)
                }
                ProgramsUiState(
                    isLoading = false,
                    programs = ranked
                )
            }
            .catch { e ->
                Log.e("ProgramsViewModel", "Failed to load programs", e)
                emit(ProgramsUiState(isLoading = false, errorMessage = "Не удалось загрузить программы. Попробуйте открыть экран ещё раз."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProgramsUiState()
            )
}
