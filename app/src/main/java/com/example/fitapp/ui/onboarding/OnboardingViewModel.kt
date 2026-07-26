package com.example.fitapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.ExperienceLevel
import com.example.fitapp.data.local.entity.FitnessGoal
import com.example.fitapp.data.local.entity.Gender
import com.example.fitapp.data.local.entity.MuscleFocus
import com.example.fitapp.data.local.entity.PreferredDuration
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.data.repository.WorkoutRepository
import com.example.fitapp.ui.programs.ProgramCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
            databaseInitializer.initializeIfNeeded()
            calculateRecommendations()
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
            val messages = listOf(
                "Анализ биометрии и нормы калорий...",
                "Фильтрация имеющегося снаряжения...",
                "Учет акцентных мышечных групп...",
                "Формирование оптимальных дневных сплитов...",
                "Персональный план успешно сформирован!"
            )
            for (i in messages.indices) {
                val progress = (i + 1) / messages.size.toFloat()
                _uiState.value = _uiState.value.copy(
                    generationProgress = progress,
                    generationMessage = messages[i]
                )
                delay(350)
            }
            delay(200)
            calculateRecommendations()
            _uiState.value = _uiState.value.copy(
                currentStep = 7,
                isGeneratingPlan = false
            )
        }
    }

    private fun calculateRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val presets = workoutRepository.observePresets().first()
                val cards = presets.map { w ->
                    val detail = workoutRepository.getDetail(w.id)
                    ProgramCard(
                        workout = w,
                        exerciseCount = detail?.exercises?.size ?: 0,
                        totalSets = detail?.exercises?.sumOf { it.sets } ?: 0,
                        exercises = detail?.exercises.orEmpty()
                    )
                }
                val isHome = _uiState.value.location != WorkoutLocation.GYM
                val isDumbbells = _uiState.value.location == WorkoutLocation.HOME_DUMBBELLS
                val focus = _uiState.value.focus

                val filtered: List<ProgramCard> = cards.sortedByDescending { card ->
                    var score = 0
                    val name = card.workout.name.lowercase()

                    if (isHome) {
                        if (isDumbbells) {
                            if (name.contains("гантел")) score += 10
                            if (name.contains("свой вес")) score += 3
                        } else {
                            if (name.contains("свой вес")) score += 10
                        }
                    } else {
                        if (!name.contains("дом")) score += 8
                        if (_uiState.value.experience == ExperienceLevel.BEGINNER && name.contains("full body")) score += 6
                        if (_uiState.value.experience != ExperienceLevel.BEGINNER && (name.contains("ppl") || name.contains("upper"))) score += 6
                    }

                    // Акцентные группы мышц
                    when (focus) {
                        MuscleFocus.ARM_CHEST -> if (name.contains("push") || name.contains("верх")) score += 4
                        MuscleFocus.LEGS_GLUTES -> if (name.contains("legs") || name.contains("низ")) score += 4
                        MuscleFocus.UPPER_BODY -> if (name.contains("upper") || name.contains("push") || name.contains("pull")) score += 4
                        MuscleFocus.CORE_ABS -> if (name.contains("кор") || name.contains("full body")) score += 4
                        MuscleFocus.FULL_BODY -> if (name.contains("full body")) score += 4
                    }

                    score
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendedPrograms = filtered,
                    selectedProgramId = filtered.firstOrNull()?.workout?.id
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
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
