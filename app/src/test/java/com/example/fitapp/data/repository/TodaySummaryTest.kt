package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TodaySummaryTest {
    // Wednesday 2026-10-14, 12:00 local time in Almaty (UTC+5).
    private val now = Instant.parse("2026-10-14T07:00:00Z")
    private val zone = ZoneId.of("Asia/Almaty")

    @Test fun `week strip starts on local Monday and marks trained days`() {
        val logs = listOf(log(1, workoutId = 1, finish = "2026-10-12T04:00:00Z"), log(2, workoutId = 1, finish = null))
        val week = calculateWeekStrip(logs, now, zone)
        assertEquals(7, week.size)
        assertEquals(LocalDate.of(2026, 10, 12), week.first().date)
        assertEquals(listOf(true, false, false, false, false, false, false), week.map { it.trained })
        assertEquals(LocalDate.of(2026, 10, 14), week.single { it.isToday }.date)
    }

    @Test fun `week totals split this and last week at local Monday`() {
        // 2026-10-11T19:30Z is Monday 00:30 in Almaty, so it belongs to this week.
        val logs = listOf(
            log(1, workoutId = 1, finish = "2026-10-11T19:30:00Z"),
            log(2, workoutId = 1, finish = "2026-10-11T18:30:00Z"),
            log(3, workoutId = 1, finish = "2026-10-01T10:00:00Z")
        )
        val sets = listOf(set(1, 1), set(2, 1), set(2, 1).copy(done = false), set(3, 1))
        val (count, thisWeek, lastWeek) = calculateWeekTotals(logs, sets, now, zone)
        assertEquals(1, count)
        assertEquals(200.0, thisWeek, 0.001)
        assertEquals(200.0, lastWeek, 0.001)
    }

    @Test fun `rotation suggests the workout done longest ago`() {
        val logs = listOf(
            log(1, workoutId = 10, finish = "2026-10-13T10:00:00Z"),
            log(2, workoutId = 20, finish = "2026-10-09T10:00:00Z"),
            log(3, workoutId = 10, finish = "2026-10-06T10:00:00Z")
        )
        assertEquals(20L, pickSuggestedWorkoutId(logs, setOf(10, 20, 30), selectedProgramId = 30, now = now))
    }

    @Test fun `onboarding program is offered without a recent rotation`() {
        val old = listOf(log(1, workoutId = 10, finish = "2026-08-01T10:00:00Z"))
        assertEquals(30L, pickSuggestedWorkoutId(old, setOf(10, 30), selectedProgramId = 30, now = now))
        assertEquals(10L, pickSuggestedWorkoutId(old, setOf(10, 30), selectedProgramId = null, now = now))
        assertNull(pickSuggestedWorkoutId(emptyList(), emptySet(), selectedProgramId = 30, now = now))
    }

    @Test fun `archived workouts are never suggested`() {
        val logs = listOf(log(1, workoutId = 10, finish = "2026-10-13T10:00:00Z"))
        assertEquals(30L, pickSuggestedWorkoutId(logs, setOf(30), selectedProgramId = 30, now = now))
    }

    @Test fun `recovery splits muscles by hours since the workout`() {
        val logs = listOf(
            log(1, workoutId = 1, finish = "2026-10-13T19:00:00Z"), // 12 h ago
            log(2, workoutId = 1, finish = "2026-10-11T19:00:00Z"), // 60 h ago
            log(3, workoutId = 1, finish = "2026-10-10T19:00:00Z")  // 84 h ago
        )
        val sets = listOf(
            set(1, exerciseId = 1),
            set(1, exerciseId = 2).copy(done = false),
            set(2, exerciseId = 3),
            set(3, exerciseId = 4)
        )
        val exercises = mapOf(
            1L to exercise(1, "chest", "triceps"),
            2L to exercise(2, "legs", null),
            3L to exercise(3, "back", "chest"),
            4L to exercise(4, "abs", null)
        )
        val recovery = calculateMuscleRecovery(logs, sets, exercises, now)
        assertEquals(setOf("chest"), recovery.fresh)
        assertEquals(setOf("triceps", "back"), recovery.recovering)
    }

    @Test fun `last workout is the latest finished one`() {
        val logs = listOf(
            log(1, workoutId = 1, finish = "2026-10-10T10:00:00Z"),
            log(2, workoutId = 2, finish = "2026-10-13T10:00:00Z"),
            log(3, workoutId = 3, finish = null)
        )
        val last = calculateLastWorkout(logs, listOf(set(2, 1), set(2, 1), set(1, 1)))!!
        assertEquals(2L, last.logId)
        assertEquals(2, last.completedSets)
        assertEquals(400.0, last.totalVolume, 0.001)
        assertNull(calculateLastWorkout(listOf(log(4, workoutId = 1, finish = null)), emptyList()))
    }

    private fun log(id: Long, workoutId: Long, finish: String?) = WorkoutLog(
        id = id,
        workoutId = workoutId,
        workoutName = "W$workoutId",
        startedAt = 0,
        finishedAt = finish?.let { Instant.parse(it).toEpochMilli() },
        durationMin = 40
    )

    private fun set(logId: Long, exerciseId: Long) = SetLog(
        logId = logId, exerciseId = exerciseId, setNumber = 1, weight = 20.0, reps = 10, done = true
    )

    private fun exercise(id: Long, primary: String, secondary: String?) = Exercise(
        id = id, code = "e$id", name = "E$id", description = "", primaryMuscleCode = primary,
        secondaryMuscleCode = secondary, equipmentCode = "barbell", difficulty = "", imageUrl = null,
        videoUrl = null, technique = ""
    )
}
