package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.WorkoutLocation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgramRecommendationsTest {
    @Test fun homeBodyweightRejectsGymFullBodyEquipment() {
        assertFalse(isProgramCompatible(listOf("bodyweight", "barbell"), WorkoutLocation.HOME_BODYWEIGHT))
        assertTrue(isProgramCompatible(listOf("bodyweight"), WorkoutLocation.HOME_BODYWEIGHT))
    }

    @Test fun dumbbellHomeRequiresEveryExerciseToBeCompatible() {
        assertTrue(isProgramCompatible(listOf("bodyweight", "dumbbell"), WorkoutLocation.HOME_DUMBBELLS))
        assertFalse(isProgramCompatible(listOf("dumbbell", "barbell"), WorkoutLocation.HOME_DUMBBELLS))
        assertFalse(isProgramCompatible(listOf("dumbbell", "cable"), WorkoutLocation.HOME_DUMBBELLS))
    }

    @Test fun missingEquipmentDoesNotProduceARecommendation() {
        WorkoutLocation.entries.forEach { location ->
            assertFalse(isProgramCompatible(emptyList(), location))
            assertFalse(isProgramCompatible(listOf(""), location))
        }
        assertTrue(isProgramCompatible(listOf("barbell", "machine", "cable"), WorkoutLocation.GYM))
    }
}
