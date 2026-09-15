package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog

data class WorkoutProgress(
    val totalSets: Int,
    val doneSets: Int,
    val completedVolume: Double
) {
    val fraction: Float
        get() = if (totalSets == 0) 0f else doneSets.toFloat() / totalSets.toFloat()
}

fun createInitialSetLogs(
    logId: Long,
    orderedExercises: List<WorkoutExerciseItem>
): List<SetLog> = orderedExercises.flatMap { item ->
    require(item.sets in 1..100) { "Количество подходов должно быть от 1 до 100" }
    val targetReps = requireNotNull(parseTargetCount(item.reps)) { "Укажите корректное число повторений или секунд" }
    val isTimed = isDurationExercise(item.exerciseCode)
    List(item.sets) { index ->
        SetLog(
            logId = logId,
            exerciseId = item.exerciseId,
            exerciseOrder = item.order,
            setNumber = index + 1,
            weight = 0.0,
            reps = if (isTimed) 0 else targetReps,
            done = false,
            durationSeconds = if (isTimed) targetReps else null,
            restSeconds = item.restSeconds.coerceIn(0, 3600)
        )
    }
}

fun groupSetLogsInWorkoutOrder(sets: List<SetLog>): List<List<SetLog>> =
    sets.groupBy { it.exerciseOrder to it.exerciseId }
        .toSortedMap(compareBy<Pair<Int, Long>> { it.first }.thenBy { it.second })
        .values
        .map { exerciseSets -> exerciseSets.sortedBy { it.setNumber } }

fun calculateWorkoutProgress(sets: List<SetLog>): WorkoutProgress {
    val completed = sets.filter { it.done }
    return WorkoutProgress(
        totalSets = sets.size,
        doneSets = completed.size,
        completedVolume = completed.sumOf { it.trainingVolume() }
    )
}

fun isDurationExercise(code: String): Boolean = code == "plank" || code == "side_plank"

fun parseTargetCount(value: String): Int? {
    val match = Regex("""\s*(\d+)(?:\s*[-–]\s*(\d+))?(?:\s*(?:с|сек|s))?\s*""").matchEntire(value) ?: return null
    val first = match.groupValues[1].toIntOrNull()?.takeIf { it in 1..9999 } ?: return null
    val last = match.groupValues[2].takeIf { it.isNotEmpty() }?.toIntOrNull()
    if (match.groupValues[2].isNotEmpty() && (last == null || last !in first..9999)) return null
    return first
}

fun SetLog.trainingVolume(): Double = if (durationSeconds == null) weight * reps else 0.0
