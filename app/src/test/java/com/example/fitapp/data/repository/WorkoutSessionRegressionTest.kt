package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutSessionRegressionTest {
    @Test
    fun `colliding order does not merge different exercises`() {
        val groups = groupSetLogsInWorkoutOrder(listOf(
            set(1, 90, 0, 2), set(2, 10, 0, 1), set(3, 90, 0, 1)
        ))
        assertEquals(2, groups.size)
        assertTrue(groups.all { group -> group.map { it.exerciseId }.distinct().size == 1 })
        assertEquals(listOf(1, 2), groups.single { it.first().exerciseId == 90L }.map { it.setNumber })
    }

    @Test
    fun `same exercise in separate positions remains separate`() {
        val groups = groupSetLogsInWorkoutOrder(listOf(set(1, 90, 3, 1), set(2, 90, 0, 1)))
        assertEquals(listOf(0, 3), groups.map { it.first().exerciseOrder })
    }

    @Test
    fun `plank targets are seconds and rest is captured when session starts`() {
        for (code in listOf("plank", "side_plank")) {
            val sets = createInitialSetLogs(7, listOf(item(code, "30-45", 90)))
            assertEquals(2, sets.size)
            assertTrue(sets.all { it.durationSeconds == 30 && it.reps == 0 && it.weight == 0.0 })
            assertTrue(sets.all { it.restSeconds == 90 && it.logId == 7L })
        }
    }

    @Test
    fun `repetition exercises remain repetitions`() {
        val sets = createInitialSetLogs(7, listOf(item("squat", "8-12", 120)))
        assertTrue(sets.all { it.reps == 8 && it.durationSeconds == null && it.restSeconds == 120 })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero set prescription is rejected`() {
        createInitialSetLogs(7, listOf(item("squat", "8", 60).copy(sets = 0)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative set prescription is rejected`() {
        createInitialSetLogs(7, listOf(item("squat", "8", 60).copy(sets = -1)))
    }

    @Test
    fun `duration sets count towards completion but never volume`() {
        val progress = calculateWorkoutProgress(listOf(
            set(1, 90, 0, 1).copy(weight = 20.0, reps = 10, done = true),
            set(2, 10, 1, 1).copy(weight = 50.0, reps = 30, durationSeconds = 30, done = true),
            set(3, 10, 1, 2).copy(durationSeconds = 40, done = false)
        ))
        assertEquals(3, progress.totalSets)
        assertEquals(2, progress.doneSets)
        assertEquals(200.0, progress.completedVolume, 0.001)
    }

    private fun item(code: String, reps: String, rest: Int) = WorkoutExerciseItem(
        workoutExerciseId = 1, exerciseId = 90, exerciseCode = code,
        exerciseName = code, muscleName = "", muscleEmoji = "", order = 0,
        sets = 2, reps = reps, restSeconds = rest
    )

    private fun set(id: Long, exercise: Long, order: Int, number: Int) = SetLog(
        id = id, logId = 7, exerciseId = exercise, exerciseOrder = order,
        setNumber = number, weight = 0.0, reps = 10
    )
}
