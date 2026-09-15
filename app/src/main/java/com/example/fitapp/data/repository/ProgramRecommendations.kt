package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.WorkoutLocation

/** A whole workout must be compatible: never silently remove required exercises. */
fun isProgramCompatible(equipmentCodes: List<String>, location: WorkoutLocation): Boolean {
    if (equipmentCodes.isEmpty() || equipmentCodes.any { it.isBlank() }) return false
    val available = when (location) {
        WorkoutLocation.GYM -> return true
        WorkoutLocation.HOME_BODYWEIGHT -> setOf("bodyweight")
        WorkoutLocation.HOME_DUMBBELLS -> setOf("bodyweight", "dumbbell")
    }
    return equipmentCodes.all { it in available }
}
