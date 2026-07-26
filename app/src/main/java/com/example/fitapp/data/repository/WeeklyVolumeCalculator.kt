package com.example.fitapp.data.repository

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/** Calculates progress buckets using local calendar weeks that start on Monday. */
internal object WeeklyVolumeCalculator {

    fun calculate(
        workouts: List<CompletedWorkoutVolume>,
        weeksCount: Int,
        now: Instant,
        zoneId: ZoneId
    ): List<WeeklyVolume> {
        if (weeksCount <= 0) return emptyList()

        val currentWeekStart = now.atZone(zoneId).toLocalDate()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val buckets = (weeksCount - 1 downTo 0).map { offset ->
            MutableWeeklyVolume(
                currentWeekStart.minusWeeks(offset.toLong())
                    .atStartOfDay(zoneId).toInstant().toEpochMilli()
            )
        }
        val bucketsByWeekStart = buckets.associateBy { it.weekStart }

        workouts.forEach { workout ->
            val weekStart = Instant.ofEpochMilli(workout.finishedAt).atZone(zoneId).toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(zoneId).toInstant().toEpochMilli()
            if (workout.hasCompletedSets) {
                bucketsByWeekStart[weekStart]?.let { bucket ->
                    bucket.totalVolume += workout.totalVolume
                    bucket.workoutCount++
                }
            }
        }

        return buckets.map { it.toWeeklyVolume() }
    }

    private class MutableWeeklyVolume(val weekStart: Long) {
        var totalVolume: Double = 0.0
        var workoutCount: Int = 0

        fun toWeeklyVolume() = WeeklyVolume(weekStart, totalVolume, workoutCount)
    }
}

internal data class CompletedWorkoutVolume(
    val finishedAt: Long,
    val totalVolume: Double,
    val hasCompletedSets: Boolean
)
