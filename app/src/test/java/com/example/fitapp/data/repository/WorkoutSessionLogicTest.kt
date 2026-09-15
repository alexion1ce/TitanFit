package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutSessionLogicTest {

    @Test
    fun `active session keeps selected program order instead of exercise ids`() {
        val program = listOf(
            workoutItem(exerciseId = 90, order = 0, name = "Приседания со штангой"),
            workoutItem(exerciseId = 10, order = 1, name = "Жим лёжа штангой"),
            workoutItem(exerciseId = 40, order = 2, name = "Тяга штанги")
        )

        val created = createInitialSetLogs(logId = 7, orderedExercises = program)
        val databaseLikeOrder = created.sortedBy { it.exerciseId }
        val activeOrder = groupSetLogsInWorkoutOrder(databaseLikeOrder)
            .map { it.first().exerciseId }

        assertEquals(listOf(90L, 10L, 40L), activeOrder)
    }

    @Test
    fun `progress and volume include only completed sets`() {
        val sets = listOf(
            set(id = 1, done = true, weight = 20.0, reps = 10),
            set(id = 2, done = false, weight = 100.0, reps = 10),
            set(id = 3, done = true, weight = 30.0, reps = 5)
        )

        val progress = calculateWorkoutProgress(sets)

        assertEquals(3, progress.totalSets)
        assertEquals(2, progress.doneSets)
        assertEquals(350.0, progress.completedVolume, 0.001)
        assertEquals(2f / 3f, progress.fraction, 0.001f)
    }

    private fun workoutItem(exerciseId: Long, order: Int, name: String) = WorkoutExerciseItem(
        workoutExerciseId = order.toLong() + 1,
        exerciseId = exerciseId,
        exerciseName = name,
        muscleName = "",
        muscleEmoji = "",
        order = order,
        sets = 2,
        reps = "8-12",
        restSeconds = 60
    )

    private fun set(id: Long, done: Boolean, weight: Double, reps: Int) = SetLog(
        id = id,
        logId = 1,
        exerciseId = 1,
        exerciseOrder = 0,
        setNumber = id.toInt(),
        weight = weight,
        reps = reps,
        done = done
    )
}
