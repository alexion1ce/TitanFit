package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/** Строка упражнения в деталях выполненной тренировки (для журнала). */
data class LoggedExerciseRow(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleEmoji: String,
    val sets: List<SetLog>,
    val topWeight: Double,
    val totalVolume: Double
)

/** Детали одной выполненной тренировки со статистикой. */
data class WorkoutLogDetail(
    val log: WorkoutLog,
    val exercises: List<LoggedExerciseRow>,
    val totalSets: Int,
    val doneSets: Int,
    val totalVolume: Double
)

/** Сводка по неделе для графика прогресса. */
data class WeeklyVolume(
    val weekStart: Long,
    val totalVolume: Double,
    val workoutCount: Int
)

/** Личный рекорд по упражнению. */
data class PersonalRecord(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleEmoji: String,
    val maxWeight: Double,
    val estimated1RM: Double,
    val date: Long
)

/** Короткая аналитическая строка последней тренировки для экрана прогресса. */
data class RecentWorkoutSummary(
    val logId: Long,
    val workoutName: String,
    val exerciseCount: Int,
    val durationMin: Int,
    val totalVolume: Double,
    val caloriesEstimate: Int
)

@Singleton
class WorkoutLogRepository @Inject constructor(
    private val workoutLogDao: WorkoutLogDao,
    private val setLogDao: SetLogDao,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository
) {

    fun observeAllLogs(): Flow<List<WorkoutLog>> = workoutLogDao.observeAll()

    /**
     * Начинает новую тренировку: создаёт WorkoutLog и предзаполняет SetLog
     * подходами из шаблона тренировки (параметры берутся из WorkoutExercise).
     *
     * @return id созданного лога
     */
    suspend fun startWorkout(workoutId: Long): Long {
        val detail = workoutRepository.getDetail(workoutId)
            ?: throw IllegalArgumentException("Тренировка $workoutId не найдена")

        val logId = workoutLogDao.insert(
            WorkoutLog(
                workoutId = workoutId,
                workoutName = detail.name,
                startedAt = System.currentTimeMillis()
            )
        )

        // Предзаполняем подходы: для каждого упражнения по N подходов
        val sets = mutableListOf<SetLog>()
        for (item in detail.exercises) {
            // Парсим целевые повторения (берём первое число из строки типа "8-12")
            val targetReps = item.reps.substringBefore("-").trim().toIntOrNull() ?: 10
            repeat(item.sets) { i ->
                sets.add(
                    SetLog(
                        logId = logId,
                        exerciseId = item.exerciseId,
                        setNumber = i + 1,
                        weight = 0.0,
                        reps = targetReps,
                        done = false
                    )
                )
            }
        }
        setLogDao.insertAll(sets)
        return logId
    }

    suspend fun updateSet(setLog: SetLog) {
        setLogDao.update(setLog)
    }

    suspend fun addSets(logId: Long, exerciseId: Long, count: Int) {
        val safeCount = count.coerceIn(1, 20)
        val exerciseSets = setLogDao.getByLog(logId)
            .filter { it.exerciseId == exerciseId }
            .sortedBy { it.setNumber }
        val lastSet = exerciseSets.lastOrNull()
        val nextSetNumber = (lastSet?.setNumber ?: 0) + 1
        val newSets = List(safeCount) { index ->
            SetLog(
                logId = logId,
                exerciseId = exerciseId,
                setNumber = nextSetNumber + index,
                weight = lastSet?.weight ?: 0.0,
                reps = lastSet?.reps ?: 10,
                done = false
            )
        }
        setLogDao.insertAll(newSets)
    }

    /** Завершает тренировку: фиксирует время окончания и длительность. */
    suspend fun finishWorkout(logId: Long): Boolean {
        val log = workoutLogDao.getById(logId) ?: return false
        val now = System.currentTimeMillis()
        val durationMin = ((now - log.startedAt) / 60_000L).toInt()
        workoutLogDao.update(
            log.copy(
                finishedAt = now,
                durationMin = durationMin
            )
        )
        return true
    }

    suspend fun deleteLog(logId: Long) {
        workoutLogDao.deleteById(logId)
    }

    suspend fun resetProgress() {
        workoutLogDao.deleteAll()
    }

    // ===================== ЖУРНАЛ =====================

    /** Детали конкретной выполненной тренировки со всеми подходами и статистикой. */
    suspend fun getLogDetail(logId: Long): WorkoutLogDetail? {
        val log = workoutLogDao.getById(logId) ?: return null
        val sets = setLogDao.getByLog(logId)
        if (sets.isEmpty()) {
            return WorkoutLogDetail(log, emptyList(), 0, 0, 0.0)
        }

        // Подгружаем названия упражнений и эмодзи мышц
        val exerciseIds = sets.map { it.exerciseId }.distinct()
        val exercises = exerciseRepository.getByIds(exerciseIds).associateBy { it.id }
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }

        // Группируем подходы по упражнению
        val rows = sets.groupBy { it.exerciseId }
            .toSortedMap(compareBy { id -> exercises[id]?.name ?: "" })
            .map { (exId, exSets) ->
                val ex = exercises[exId]
                LoggedExerciseRow(
                    exerciseId = exId,
                    exerciseName = ex?.name ?: "Удалённое упражнение",
                    muscleEmoji = ex?.primaryMuscleCode?.let { muscles[it]?.emoji } ?: "🏋️",
                    sets = exSets.sortedBy { it.setNumber },
                    topWeight = exSets.maxOf { it.weight },
                    totalVolume = exSets.sumOf { it.weight * it.reps }
                )
            }

        return WorkoutLogDetail(
            log = log,
            exercises = rows,
            totalSets = sets.size,
            doneSets = sets.count { it.done },
            totalVolume = rows.sumOf { it.totalVolume }
        )
    }

    // ===================== ПРОГРЕСС =====================

    /** Суммарный объём (вес × повторения) по неделям за последние [weeksCount] недель. */
    suspend fun getWeeklyVolume(weeksCount: Int = 8): List<WeeklyVolume> {
        val logs = workoutLogDao.getAllFinished()
        if (logs.isEmpty()) return emptyList()

        val workouts = logs.mapNotNull { log ->
            val finishedAt = log.finishedAt ?: return@mapNotNull null
            val completedSets = setLogDao.getByLog(log.id).filter { it.done }
            val totalVolume = completedSets
                .asSequence()
                .sumOf { it.weight * it.reps }
            CompletedWorkoutVolume(
                finishedAt = finishedAt,
                totalVolume = totalVolume,
                hasCompletedSets = completedSets.isNotEmpty()
            )
        }
        if (workouts.none { it.hasCompletedSets }) return emptyList()

        return WeeklyVolumeCalculator.calculate(
            workouts = workouts,
            weeksCount = weeksCount,
            now = Instant.ofEpochMilli(System.currentTimeMillis()),
            zoneId = ZoneId.systemDefault()
        )
    }

    /** Топ-N личных рекордов по максимальному весу. */
    suspend fun getPersonalRecords(limit: Int = 5): List<PersonalRecord> {
        val logs = workoutLogDao.getAllFinished()
        if (logs.isEmpty()) return emptyList()

        val logById = logs.associateBy { it.id }
        val allSets = logs.flatMap { log -> setLogDao.getByLog(log.id).filter { it.done } }
        if (allSets.isEmpty()) return emptyList()

        val exerciseIds = allSets.map { it.exerciseId }.distinct()
        val exercises = exerciseRepository.getByIds(exerciseIds).associateBy { it.id }
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }

        return allSets
            .groupBy { it.exerciseId }
            .map { (exId, sets) ->
                val bestWeightSet = sets.maxBy { it.weight }
                val best1RMSet = sets.maxBy { it.weight * (1.0 + it.reps / 30.0) }
                val estimated1RM = best1RMSet.weight * (1.0 + best1RMSet.reps / 30.0)
                val ex = exercises[exId]
                PersonalRecord(
                    exerciseId = exId,
                    exerciseName = ex?.name ?: "—",
                    muscleEmoji = ex?.primaryMuscleCode?.let { muscles[it]?.emoji } ?: "🏋️",
                    maxWeight = bestWeightSet.weight,
                    estimated1RM = estimated1RM,
                    date = logById[bestWeightSet.logId]?.startedAt ?: 0L
                )
            }
            .sortedByDescending { it.maxWeight }
            .take(limit)
    }

    /** Последние завершённые тренировки со сводными метриками. */
    suspend fun getRecentWorkoutSummaries(limit: Int = 3): List<RecentWorkoutSummary> {
        val logs = workoutLogDao.getAllFinished().take(limit)
        if (logs.isEmpty()) return emptyList()

        return logs.map { log ->
            val doneSets = setLogDao.getByLog(log.id).filter { it.done }
            val volume = doneSets.sumOf { it.weight * it.reps }
            RecentWorkoutSummary(
                logId = log.id,
                workoutName = log.workoutName,
                exerciseCount = doneSets.map { it.exerciseId }.distinct().size,
                durationMin = log.durationMin ?: 0,
                totalVolume = volume,
                caloriesEstimate = (volume * 0.08).toInt().coerceAtLeast(0)
            )
        }
    }

    /** Общая статистика для шапки экрана прогресса. */
    suspend fun getOverallStats(): OverallStats {
        val logs = workoutLogDao.getAllFinished()
        if (logs.isEmpty()) return OverallStats(0, 0, 0.0, 0, 0)

        val logById = logs.associateBy { it.id }
        val allLogsSets = logs.flatMap { log -> setLogDao.getByLog(log.id) }
        val doneSets = allLogsSets.filter { it.done }
        val totalVolume = doneSets.sumOf { it.weight * it.reps }
        val totalMinutes = logs.sumOf { it.durationMin ?: 0 }
        val completionRate = if (allLogsSets.isNotEmpty()) {
            ((doneSets.size.toDouble() / allLogsSets.size) * 100).toInt().coerceIn(0, 100)
        } else 0

        return OverallStats(
            totalWorkouts = logs.size,
            totalSets = doneSets.size,
            totalVolume = totalVolume,
            totalMinutes = totalMinutes,
            completionRate = completionRate
        )
    }
}

data class OverallStats(
    val totalWorkouts: Int,
    val totalSets: Int,
    val totalVolume: Double,
    val totalMinutes: Int,
    val completionRate: Int = 0
)
