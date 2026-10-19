package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/** Один день в полоске недели на экране «Сегодня». */
data class WeekDayStatus(
    val date: LocalDate,
    val trained: Boolean,
    val isToday: Boolean
)

/** Незавершённая сессия, которую можно продолжить. */
data class UnfinishedSession(
    val logId: Long,
    val workoutId: Long,
    val workoutName: String,
    val startedAt: Long,
    val doneSets: Int,
    val totalSets: Int
)

/** Тренировка, которую экран «Сегодня» предлагает начать. */
data class SuggestedWorkout(
    val workoutId: Long,
    val name: String,
    val exerciseCount: Int,
    val muscleNames: List<String>,
    val lastDoneAt: Long?
)

/** Последняя завершённая тренировка. */
data class LastWorkoutSummary(
    val logId: Long,
    val workoutName: String,
    val finishedAt: Long,
    val durationMin: Int,
    val completedSets: Int,
    val totalVolume: Double
)

/** Нагрузка на мышцы за последние дни (коды из muscle_groups). */
data class MuscleRecovery(
    /** Нагружены менее 48 часов назад. */
    val fresh: Set<String>,
    /** Нагружены 48–72 часа назад или как вспомогательные. */
    val recovering: Set<String>
)

data class TodaySnapshot(
    val week: List<WeekDayStatus>,
    val workoutsThisWeek: Int,
    val weeklyGoal: Int,
    val volumeThisWeek: Double,
    val volumeLastWeek: Double,
    val unfinished: UnfinishedSession?,
    val suggested: SuggestedWorkout?,
    val lastWorkout: LastWorkoutSummary?,
    val recovery: MuscleRecovery,
    /** Названия мышц по коду, для подписи под картой. */
    val muscleNames: Map<String, String>
)

private const val HOUR_MS = 60L * 60L * 1000L
private const val ROTATION_WINDOW_DAYS = 28L

/** Monday-to-Sunday strip of the current local week with trained days marked. */
internal fun calculateWeekStrip(
    finishedLogs: List<WorkoutLog>,
    now: Instant,
    zone: ZoneId
): List<WeekDayStatus> {
    val today = now.atZone(zone).toLocalDate()
    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val trainedDays = finishedLogs.mapNotNull { log ->
        log.finishedAt?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
    }.toSet()
    return (0L until 7L).map { offset ->
        val date = monday.plusDays(offset)
        WeekDayStatus(date = date, trained = date in trainedDays, isToday = date == today)
    }
}

/** Finished-workout count and volume for the current and the previous local week. */
internal fun calculateWeekTotals(
    finishedLogs: List<WorkoutLog>,
    sets: List<SetLog>,
    now: Instant,
    zone: ZoneId
): Triple<Int, Double, Double> {
    val monday = now.atZone(zone).toLocalDate()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val thisWeekStart = monday.atStartOfDay(zone).toInstant().toEpochMilli()
    val lastWeekStart = monday.minusWeeks(1).atStartOfDay(zone).toInstant().toEpochMilli()
    val nowMs = now.toEpochMilli()
    val volumeByLog = sets.filter { it.done }.groupBy { it.logId }
        .mapValues { (_, logSets) -> logSets.sumOf { it.trainingVolume() } }

    val thisWeek = finishedLogs.filter { it.finishedAt != null && it.finishedAt in thisWeekStart..nowMs }
    val lastWeek = finishedLogs.filter { it.finishedAt != null && it.finishedAt >= lastWeekStart && it.finishedAt < thisWeekStart }
    return Triple(
        thisWeek.size,
        thisWeek.sumOf { volumeByLog[it.id] ?: 0.0 },
        lastWeek.sumOf { volumeByLog[it.id] ?: 0.0 }
    )
}

/**
 * Picks the workout to offer next.
 *
 * Workouts finished in the last four weeks form the user's rotation: the one done longest
 * ago comes next. Without a rotation, the program chosen in onboarding is offered, then the
 * most recently finished workout. Only ids in [availableIds] (not archived) are returned.
 */
internal fun pickSuggestedWorkoutId(
    finishedLogs: List<WorkoutLog>,
    availableIds: Set<Long>,
    selectedProgramId: Long?,
    now: Instant
): Long? {
    val lastDone = finishedLogs
        .filter { it.finishedAt != null && it.workoutId in availableIds }
        .groupBy { it.workoutId }
        .mapValues { (_, logs) -> logs.maxOf { it.finishedAt!! } }
    val windowStart = now.toEpochMilli() - ROTATION_WINDOW_DAYS * 24 * HOUR_MS
    val rotation = lastDone.filterValues { it >= windowStart }
    if (rotation.isNotEmpty()) return rotation.minBy { it.value }.key
    if (selectedProgramId != null && selectedProgramId in availableIds) return selectedProgramId
    return lastDone.maxByOrNull { it.value }?.key
}

/** Muscles worked by done sets of workouts finished in the last 72 hours. */
internal fun calculateMuscleRecovery(
    finishedLogs: List<WorkoutLog>,
    sets: List<SetLog>,
    exercises: Map<Long, Exercise>,
    now: Instant
): MuscleRecovery {
    val nowMs = now.toEpochMilli()
    val finishedAtByLog = finishedLogs.mapNotNull { log -> log.finishedAt?.let { log.id to it } }.toMap()
    val fresh = mutableSetOf<String>()
    val recovering = mutableSetOf<String>()
    sets.filter { it.done }.forEach { set ->
        val finishedAt = finishedAtByLog[set.logId] ?: return@forEach
        val hoursAgo = (nowMs - finishedAt) / HOUR_MS
        if (hoursAgo < 0 || hoursAgo >= 72) return@forEach
        val exercise = exercises[set.exerciseId] ?: return@forEach
        if (hoursAgo < 48) {
            fresh += exercise.primaryMuscleCode
            exercise.secondaryMuscleCode?.let { recovering += it }
        } else {
            recovering += exercise.primaryMuscleCode
        }
    }
    return MuscleRecovery(fresh = fresh, recovering = recovering - fresh)
}

/** Latest finished workout (logs in any order). */
internal fun calculateLastWorkout(finishedLogs: List<WorkoutLog>, sets: List<SetLog>): LastWorkoutSummary? {
    val last = finishedLogs.filter { it.finishedAt != null }.maxByOrNull { it.finishedAt!! } ?: return null
    val done = sets.filter { it.logId == last.id && it.done }
    return LastWorkoutSummary(
        logId = last.id,
        workoutName = last.workoutName,
        finishedAt = last.finishedAt!!,
        durationMin = last.durationMin ?: 0,
        completedSets = done.size,
        totalVolume = done.sumOf { it.trainingVolume() }
    )
}
