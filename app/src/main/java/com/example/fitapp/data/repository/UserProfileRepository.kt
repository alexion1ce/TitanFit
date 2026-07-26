package com.example.fitapp.data.repository

import android.content.Context
import com.example.fitapp.data.local.entity.ExperienceLevel
import com.example.fitapp.data.local.entity.FitnessGoal
import com.example.fitapp.data.local.entity.Gender
import com.example.fitapp.data.local.entity.MuscleFocus
import com.example.fitapp.data.local.entity.PreferredDuration
import com.example.fitapp.data.local.entity.UserProfile
import com.example.fitapp.data.local.entity.WorkoutLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("fitapp_user_profile", Context.MODE_PRIVATE)

    private val _profileFlow = MutableStateFlow(loadProfile())
    val profileFlow: StateFlow<UserProfile> = _profileFlow.asStateFlow()

    fun loadProfile(): UserProfile {
        val genderStr = prefs.getString(KEY_GENDER, Gender.MALE.name) ?: Gender.MALE.name
        val age = prefs.getInt(KEY_AGE, 25)
        val height = prefs.getFloat(KEY_HEIGHT, 175f).toDouble()
        val weight = prefs.getFloat(KEY_WEIGHT, 75f).toDouble()
        val goalStr = prefs.getString(KEY_GOAL, FitnessGoal.MUSCLE.name) ?: FitnessGoal.MUSCLE.name
        val locStr = prefs.getString(KEY_LOCATION, WorkoutLocation.GYM.name) ?: WorkoutLocation.GYM.name
        val expStr = prefs.getString(KEY_EXPERIENCE, ExperienceLevel.BEGINNER.name) ?: ExperienceLevel.BEGINNER.name
        val focusStr = prefs.getString(KEY_FOCUS, MuscleFocus.FULL_BODY.name) ?: MuscleFocus.FULL_BODY.name
        val durStr = prefs.getString(KEY_DURATION, PreferredDuration.MEDIUM.name) ?: PreferredDuration.MEDIUM.name
        val days = prefs.getInt(KEY_DAYS, 3)
        val completed = prefs.getBoolean(KEY_COMPLETED, false)

        return UserProfile(
            gender = runCatching { Gender.valueOf(genderStr) }.getOrDefault(Gender.MALE),
            age = age,
            heightCm = height,
            weightKg = weight,
            goal = runCatching { FitnessGoal.valueOf(goalStr) }.getOrDefault(FitnessGoal.MUSCLE),
            location = runCatching { WorkoutLocation.valueOf(locStr) }.getOrDefault(WorkoutLocation.GYM),
            experience = runCatching { ExperienceLevel.valueOf(expStr) }.getOrDefault(ExperienceLevel.BEGINNER),
            daysPerWeek = days,
            focus = runCatching { MuscleFocus.valueOf(focusStr) }.getOrDefault(MuscleFocus.FULL_BODY),
            preferredDuration = runCatching { PreferredDuration.valueOf(durStr) }.getOrDefault(PreferredDuration.MEDIUM),
            onboardingCompleted = completed
        )
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_GENDER, profile.gender.name)
            .putInt(KEY_AGE, profile.age)
            .putFloat(KEY_HEIGHT, profile.heightCm.toFloat())
            .putFloat(KEY_WEIGHT, profile.weightKg.toFloat())
            .putString(KEY_GOAL, profile.goal.name)
            .putString(KEY_LOCATION, profile.location.name)
            .putString(KEY_EXPERIENCE, profile.experience.name)
            .putString(KEY_FOCUS, profile.focus.name)
            .putString(KEY_DURATION, profile.preferredDuration.name)
            .putInt(KEY_DAYS, profile.daysPerWeek)
            .putBoolean(KEY_COMPLETED, profile.onboardingCompleted)
            .apply()
        _profileFlow.value = profile
    }

    fun updateLocation(location: WorkoutLocation) {
        val current = loadProfile()
        saveProfile(current.copy(location = location))
    }

    fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    companion object {
        private const val KEY_GENDER = "gender"
        private const val KEY_AGE = "age"
        private const val KEY_HEIGHT = "height"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_GOAL = "goal"
        private const val KEY_LOCATION = "location"
        private const val KEY_EXPERIENCE = "experience"
        private const val KEY_FOCUS = "muscle_focus"
        private const val KEY_DURATION = "preferred_duration"
        private const val KEY_DAYS = "days"
        private const val KEY_COMPLETED = "onboarding_v2_completed"
    }
}

