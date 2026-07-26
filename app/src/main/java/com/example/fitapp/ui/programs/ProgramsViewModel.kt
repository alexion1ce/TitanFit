package com.example.fitapp.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userProfileRepository: UserProfileRepository,
    private val databaseInitializer: DatabaseInitializer
) : ViewModel() {

    init {
        viewModelScope.launch {
            databaseInitializer.initializeIfNeeded()
        }
    }

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

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProgramsUiState> =
        combine(workoutRepository.observePresets(), userProfileRepository.profileFlow) { workouts, profile ->
                val cards = workouts.map { w ->
                    val detail = workoutRepository.getDetail(w.id)
                    ProgramCard(
                        workout = w,
                        exerciseCount = detail?.exercises?.size ?: 0,
                        totalSets = detail?.exercises?.sumOf { it.sets } ?: 0,
                        exercises = detail?.exercises.orEmpty()
                    )
                }
                val ranked = cards.sortedByDescending { card ->
                    val name = card.workout.name.lowercase()
                    var score = 0
                    when (profile.location) {
                        WorkoutLocation.HOME_DUMBBELLS -> if (name.contains("dumbbell") || name.contains("гантел")) score += 10
                        WorkoutLocation.HOME_BODYWEIGHT -> if (name.contains("body") || name.contains("вес")) score += 10
                        WorkoutLocation.GYM -> if (!name.contains("home") && !name.contains("дом")) score += 4
                    }
                    if (profile.experience.name == "BEGINNER" && name.contains("full body")) score += 6
                    score
                }
                ProgramsUiState(
                    isLoading = false,
                    programs = ranked,
                    errorMessage = _errorMessage.value
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProgramsUiState()
            )
}
