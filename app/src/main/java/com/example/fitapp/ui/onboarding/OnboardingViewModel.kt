package com.example.fitapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.ExperienceLevel
import com.example.fitapp.data.local.entity.FitnessGoal
import com.example.fitapp.data.local.entity.Gender
import com.example.fitapp.data.local.entity.MuscleFocus
import com.example.fitapp.data.local.entity.PreferredDuration
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.data.repository.isProgramCompatible
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.data.repository.WorkoutRepository
import com.example.fitapp.ui.programs.ProgramCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val workoutRepository: WorkoutRepository,
    private val databaseInitializer: DatabaseInitializer
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            databaseInitializer.tryInitialize()
        }
        val existing = userProfileRepository.loadProfile()
        _uiState.value = _uiState.value.copy(
            gender = existing.gender,
            age = existing.age,
            heightCm = existing.heightCm,
            weightKg = existing.weightKg,
            goal = existing.goal,
            location = existing.location,
            experience = existing.experience,
            daysPerWeek = existing.daysPerWeek,
            focus = existing.focus,
            preferredDuration = existing.preferredDuration
        )
    }

    fun setGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }

    fun setAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age.coerceIn(12, 100))
    }

    fun setHeight(heightCm: Double) {
        _uiState.value = _uiState.value.copy(heightCm = heightCm.coerceIn(100.0, 240.0))
    }

    fun setWeight(weightKg: Double) {
        _uiState.value = _uiState.value.copy(weightKg = weightKg.coerceIn(30.0, 250.0))
    }

    fun setGoal(goal: FitnessGoal) {
        _uiState.value = _uiState.value.copy(goal = goal)
    }

    fun setLocation(location: WorkoutLocation) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun setFocus(focus: MuscleFocus) {
        _uiState.value = _uiState.value.copy(focus = focus)
    }

    fun setExperience(experience: ExperienceLevel) {
        _uiState.value = _uiState.value.copy(experience = experience)
    }

    fun setPreferredDuration(duration: PreferredDuration) {
        _uiState.value = _uiState.value.copy(preferredDuration = duration)
    }

    fun setDaysPerWeek(days: Int) {
        _uiState.value = _uiState.value.copy(daysPerWeek = days.coerceIn(2, 6))
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current == 5) {
            // Переход на шаг 6: Анимация генерации плана
            _uiState.value = _uiState.value.copy(currentStep = 6, isGeneratingPlan = true)
            startPlanGenerationAnimation()
        } else if (current < 7) {
            val next = current + 1
            _uiState.value = _uiState.value.copy(currentStep = next)
        }
    }

    fun prevStep() {
        val prev = (_uiState.value.currentStep - 1).coerceAtLeast(1)
        _uiState.value = _uiState.value.copy(currentStep = prev)
    }

    private fun startPlanGenerationAnimation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                generationProgress = 0f,
                generationMessage = "Проверка оборудования в готовых тренировках..."
            )
            calculateRecommendations()
            _uiState.value = _uiState.value.copy(
                generationProgress = 1f,
                currentStep = 7,
                isGeneratingPlan = false
            )
        }
    }

    private suspend fun calculateRecommendations() {
        run {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val presets = workoutRepository.observePresets().first()
                val details = workoutRepository.getDetails(presets.map { it.id })
                val cards = presets.map { w ->
                    val detail = details[w.id]
                    ProgramCard(
                        workout = w,
                        exerciseCount = detail?.exercises?.size ?: 0,
                        totalSets = detail?.exercises?.sumOf { it.sets } ?: 0,
                        exercises = detail?.exercises.orEmpty()
                    )
                }
                val filtered = cards.filter { card ->
                    isProgramCompatible(card.exercises.map { it.equipmentCode }, _uiState.value.location)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendedPrograms = filtered,
                    selectedProgramId = filtered.firstOrNull()?.workout?.id
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, selectedProgramId = null, recommendedPrograms = emptyList(), errorMessage = "Не удалось загрузить программы. Вернитесь назад и повторите подбор.")
            }
        }
    }

    fun selectProgram(programId: Long) {
        _uiState.value = _uiState.value.copy(selectedProgramId = programId)
    }

    fun finishOnboarding(onSuccess: (Long?) -> Unit) {
        viewModelScope.launch {
            val updatedProfile = _uiState.value.tempProfile.copy(onboardingCompleted = true)
            userProfileRepository.saveProfile(updatedProfile)
            _uiState.value = _uiState.value.copy(isCompleted = true)
            onSuccess(_uiState.value.selectedProgramId)
        }
    }
}
