package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

internal fun calculateOverallStats(
    logs: List<WorkoutLog>,
    sets: List<SetLog>,
    weeksCount: Int?,
    now: Instant,
    zone: ZoneId
): OverallStats {
    val start = weeksCount?.let {
        require(it > 0)
        now.atZone(zone).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .minusWeeks((it - 1).toLong()).atStartOfDay(zone).toInstant().toEpochMilli()
    } ?: Long.MIN_VALUE
    val periodLogs = logs.filter { it.finishedAt != null && it.finishedAt in start..now.toEpochMilli() }
    val ids = periodLogs.map { it.id }.toSet()
    val periodSets = sets.filter { it.logId in ids }
    val completed = periodSets.filter { it.done }
    val completedIds = completed.map { it.logId }.toSet()
    return OverallStats(
        totalWorkouts = completedIds.size,
        totalSets = completed.size,
        totalVolume = completed.sumOf { it.trainingVolume() },
        totalMinutes = periodLogs.filter { it.id in completedIds }.sumOf { it.durationMin ?: 0 },
        completionRate = if (periodSets.isEmpty()) 0 else completed.size * 100 / periodSets.size
    )
}

data class OverallStats(
    val totalWorkouts: Int,
    val totalSets: Int,
    val totalVolume: Double,
    val totalMinutes: Int,
    val completionRate: Int = 0
)
