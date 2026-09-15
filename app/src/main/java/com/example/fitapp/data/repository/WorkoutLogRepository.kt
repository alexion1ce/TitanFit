package com.example.fitapp.data.repository

import androidx.room.withTransaction
import com.example.fitapp.data.local.AppDatabase
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
    val completedSets: Int
)

@Singleton
class WorkoutLogRepository @Inject constructor(
    private val db: AppDatabase,
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
    suspend fun resumeOrStartWorkout(workoutId: Long): Long = db.withTransaction {
        workoutLogDao.getUnfinishedByWorkoutId(workoutId)?.id
            ?: startWorkout(workoutId)
    }

    private suspend fun startWorkout(workoutId: Long): Long {
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
        val sets = createInitialSetLogs(logId, detail.exercises)
        setLogDao.insertAll(sets)
        return logId
    }

    suspend fun updateSet(setLog: SetLog) {
        setLogDao.update(setLog)
    }

    suspend fun updateSetWeight(setId: Long, weight: Double) {
        require(weight.isFinite() && weight >= 0.0)
        setLogDao.updateWeight(setId, weight)
    }

    suspend fun updateSetReps(setId: Long, reps: Int) {
        require(reps > 0)
        setLogDao.updateReps(setId, reps)
    }

    suspend fun updateSetDuration(setId: Long, seconds: Int) {
        require(seconds > 0)
        setLogDao.updateDuration(setId, seconds)
    }

    suspend fun updateSetDone(setId: Long, done: Boolean) = setLogDao.updateDone(setId, done)

    suspend fun addSets(logId: Long, exerciseId: Long, exerciseOrder: Int, count: Int) = db.withTransaction {
        val safeCount = count.coerceIn(1, 20)
        val exerciseSets = setLogDao.getByLog(logId)
            .filter { it.exerciseId == exerciseId && it.exerciseOrder == exerciseOrder }
            .sortedBy { it.setNumber }
        val lastSet = requireNotNull(exerciseSets.lastOrNull()) { "Упражнение сессии не найдено" }
        val nextSetNumber = lastSet.setNumber + 1
        val newSets = List(safeCount) { index ->
            lastSet.copy(
                id = 0,
                setNumber = nextSetNumber + index,
                done = false
            )
        }
        setLogDao.insertAll(newSets)
    }

    /** Завершает тренировку: фиксирует время окончания и длительность. */
    suspend fun finishWorkout(logId: Long): Boolean = db.withTransaction {
        val log = workoutLogDao.getById(logId) ?: return@withTransaction false
        if (log.finishedAt != null) return@withTransaction true
        val now = System.currentTimeMillis()
        val durationMin = ((now - log.startedAt) / 60_000L).toInt()
        workoutLogDao.update(
            log.copy(
                finishedAt = now,
                durationMin = durationMin.coerceAtLeast(0),
                restTimerTotalSeconds = null,
                restTimerEndsAt = null
            )
        )
        true
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
        val rows = groupSetLogsInWorkoutOrder(sets).map { exSets ->
            val exId = exSets.first().exerciseId
            val ex = exercises[exId]
            val completedSets = exSets.filter { it.done }
            LoggedExerciseRow(
                exerciseId = exId,
                exerciseName = ex?.name ?: "Удалённое упражнение",
                muscleEmoji = ex?.primaryMuscleCode?.let { muscles[it]?.emoji } ?: "🏋️",
                sets = exSets,
                topWeight = completedSets.filter { it.durationSeconds == null }.maxOfOrNull { it.weight } ?: 0.0,
                totalVolume = completedSets.sumOf { it.trainingVolume() }
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
                .sumOf { it.trainingVolume() }
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
        val allSets = logs.flatMap { log -> setLogDao.getByLog(log.id).filter { it.done && it.durationSeconds == null && it.weight > 0 } }
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
            val volume = doneSets.sumOf { it.trainingVolume() }
            RecentWorkoutSummary(
                logId = log.id,
                workoutName = log.workoutName,
                exerciseCount = doneSets.map { it.exerciseId }.distinct().size,
                durationMin = log.durationMin ?: 0,
                totalVolume = volume,
                completedSets = doneSets.size
            )
        }
    }

    /** Общая статистика для шапки экрана прогресса. */
    suspend fun getOverallStats(weeksCount: Int? = null): OverallStats {
        val logs = workoutLogDao.getAllFinished()
        val sets = logs.flatMap { setLogDao.getByLog(it.id) }
        return calculateOverallStats(logs, sets, weeksCount, Instant.now(), ZoneId.systemDefault())
    }
}
