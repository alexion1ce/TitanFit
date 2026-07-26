package com.example.fitapp.ui.onboarding

import com.example.fitapp.data.local.entity.ExperienceLevel
import com.example.fitapp.data.local.entity.FitnessGoal
import com.example.fitapp.data.local.entity.Gender
import com.example.fitapp.data.local.entity.MuscleFocus
import com.example.fitapp.data.local.entity.PreferredDuration
import com.example.fitapp.data.local.entity.UserProfile
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.ui.programs.ProgramCard

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    val gender: Gender = Gender.MALE,
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val weightKg: Double = 75.0,
    val goal: FitnessGoal = FitnessGoal.MUSCLE,
    val location: WorkoutLocation = WorkoutLocation.GYM,
    val experience: ExperienceLevel = ExperienceLevel.BEGINNER,
    val daysPerWeek: Int = 3,
    val focus: MuscleFocus = MuscleFocus.FULL_BODY,
    val preferredDuration: PreferredDuration = PreferredDuration.MEDIUM,
    val isGeneratingPlan: Boolean = false,
    val generationProgress: Float = 0f,
    val generationMessage: String = "Анализ биометрии и нормы калорий...",
    val recommendedPrograms: List<ProgramCard> = emptyList(),
    val selectedProgramId: Long? = null,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
) {
    val tempProfile: UserProfile
        get() = UserProfile(
            gender = gender,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            goal = goal,
            location = location,
            experience = experience,
            daysPerWeek = daysPerWeek,
            focus = focus,
            preferredDuration = preferredDuration,
            onboardingCompleted = isCompleted
        )
}
