package com.example.fitapp.data.repository

import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyVolumeCalculatorTest {

    private val zone = ZoneId.of("Europe/Berlin")

    @Test
    fun `groups completed workouts into calendar weeks starting on Monday`() {
        val result = WeeklyVolumeCalculator.calculate(
            workouts = listOf(workout("2026-07-08T10:00:00Z", 500.0), workout("2026-06-30T10:00:00Z", 320.0)),
            weeksCount = 2,
            now = Instant.parse("2026-07-10T12:00:00Z"),
            zoneId = zone
        )

        assertEquals(320.0, result[0].totalVolume, 0.0)
        assertEquals(1, result[0].workoutCount)
        assertEquals(500.0, result[1].totalVolume, 0.0)
        assertEquals(1, result[1].workoutCount)
    }

    @Test
    fun `uses timezone aware Monday boundary across daylight saving time`() {
        val result = WeeklyVolumeCalculator.calculate(
            workouts = listOf(workout("2026-03-29T21:30:00Z", 10.0), workout("2026-03-29T22:30:00Z", 20.0)),
            weeksCount = 2,
            now = Instant.parse("2026-03-31T12:00:00Z"),
            zoneId = zone
        )

        assertEquals(10.0, result[0].totalVolume, 0.0)
        assertEquals(20.0, result[1].totalVolume, 0.0)
    }

    @Test
    fun `does not count workouts without completed sets`() {
        val result = WeeklyVolumeCalculator.calculate(
            workouts = listOf(workout("2026-07-08T10:00:00Z", 60.0), workout("2026-07-08T10:00:00Z", 0.0)),
            weeksCount = 1,
            now = Instant.parse("2026-07-10T12:00:00Z"),
            zoneId = zone
        )

        assertEquals(60.0, result.single().totalVolume, 0.0)
        assertEquals(1, result.single().workoutCount)
    }

    private fun workout(finishedAt: String, totalVolume: Double, hasCompletedSets: Boolean = totalVolume > 0.0) = CompletedWorkoutVolume(
        finishedAt = Instant.parse(finishedAt).toEpochMilli(),
        totalVolume = totalVolume,
        hasCompletedSets = hasCompletedSets
    )
}
