package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
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
    val date: Long
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

    /** Завершает тренировку: фиксирует время окончания и длительность. */
    suspend fun finishWorkout(logId: Long) {
        val log = workoutLogDao.getById(logId) ?: return
        val now = System.currentTimeMillis()
        val durationMin = ((now - log.startedAt) / 60_000L).toInt()
        workoutLogDao.update(
            log.copy(
                finishedAt = now,
                durationMin = durationMin
            )
        )
    }

    suspend fun deleteLog(logId: Long) {
        workoutLogDao.deleteById(logId)
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

        // Собираем все выполненные подходы с привязкой к дате лога
        val logById = logs.associateBy { it.id }
        val allSets = logs.flatMap { log -> setLogDao.getByLog(log.id).filter { it.done } }
        if (allSets.isEmpty()) return emptyList()

        // Группируем по неделе: понедельник как начало недели
        val now = System.currentTimeMillis()
        val msPerWeek = 7 * 24 * 60 * 60 * 1000L
        val result = mutableListOf<WeeklyVolume>()

        for (i in weeksCount - 1 downTo 0) {
            val weekEnd = now - (i * msPerWeek)
            val weekStart = weekEnd - msPerWeek

            val setsInWeek = allSets.filter { setLog ->
                val logDate = logById[setLog.logId]?.startedAt ?: 0L
                logDate in weekStart until weekEnd
            }
            val volume = setsInWeek.sumOf { it.weight * it.reps }
            val workoutCount = setsInWeek.map { it.logId }.distinct().size
            result.add(WeeklyVolume(weekStart, volume, workoutCount))
        }
        return result
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
                val best = sets.maxBy { it.weight }
                val ex = exercises[exId]
                PersonalRecord(
                    exerciseId = exId,
                    exerciseName = ex?.name ?: "—",
                    muscleEmoji = ex?.primaryMuscleCode?.let { muscles[it]?.emoji } ?: "🏋️",
                    maxWeight = best.weight,
                    date = logById[best.logId]?.startedAt ?: 0L
                )
            }
            .sortedByDescending { it.maxWeight }
            .take(limit)
    }

    /** Общая статистика для шапки экрана прогресса. */
    suspend fun getOverallStats(): OverallStats {
        val logs = workoutLogDao.getAllFinished()
        if (logs.isEmpty()) return OverallStats(0, 0, 0.0, 0)

        val logById = logs.associateBy { it.id }
        val allSets = logs.flatMap { log -> setLogDao.getByLog(log.id).filter { it.done } }
        val totalVolume = allSets.sumOf { it.weight * it.reps }
        val totalMinutes = logs.sumOf { it.durationMin ?: 0 }

        return OverallStats(
            totalWorkouts = logs.size,
            totalSets = allSets.size,
            totalVolume = totalVolume,
            totalMinutes = totalMinutes
        )
    }
}

data class OverallStats(
    val totalWorkouts: Int,
    val totalSets: Int,
    val totalVolume: Double,
    val totalMinutes: Int
)
