package com.example.fitapp.data.repository

import androidx.room.withTransaction
import com.example.fitapp.data.local.AppDatabase
import com.example.fitapp.data.local.WorkoutWithExercises
import com.example.fitapp.data.local.dao.WorkoutDao
import com.example.fitapp.data.local.dao.WorkoutExerciseDao
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.local.entity.WorkoutExercise
import com.example.fitapp.data.local.entity.WorkoutType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Данные упражнения в составе тренировки, обогащённые названием
 * исходного упражнения (для отображения в редакторе и журнале).
 */
data class WorkoutExerciseItem(
    val workoutExerciseId: Long,
    val exerciseId: Long,
    val exerciseCode: String = "",
    val equipmentCode: String = "",
    val exerciseName: String,
    val muscleName: String,
    val muscleEmoji: String,
    val primaryMuscleCode: String = "",
    val secondaryMuscleCode: String? = null,
    val order: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    /** Editor-only row identity; lets the same exercise appear twice. Not stored. */
    val editorKey: Long = 0
)

data class WorkoutDetail(
    val id: Long,
    val name: String,
    val notes: String?,
    val type: WorkoutType,
    val exercises: List<WorkoutExerciseItem>
)

@Singleton
class WorkoutRepository @Inject constructor(
    private val db: AppDatabase,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val databaseInitializer: DatabaseInitializer
) {

    fun observeCustomWorkouts(): Flow<List<Workout>> =
        workoutDao.observeByType(WorkoutType.CUSTOM.storageKey)

    /** Готовые встроенные программы (пресеты). */
    fun observePresets(): Flow<List<Workout>> = flow {
        // A failed seed must not crash the screen; existing presets are still shown.
        databaseInitializer.tryInitialize()
        workoutDao.observeByType(WorkoutType.PRESET.storageKey).collect { emit(it) }
    }

    suspend fun getDetail(id: Long): WorkoutDetail? {
        val withEx = workoutDao.getWithExercises(id) ?: return null
        return toDetails(listOf(withEx)).single()
    }

    /** Loads several workouts with shared exercise and muscle lookups. */
    suspend fun getDetails(ids: List<Long>): Map<Long, WorkoutDetail> {
        if (ids.isEmpty()) return emptyMap()
        return toDetails(workoutDao.getWithExercisesByIds(ids)).associateBy { it.id }
    }

    private suspend fun toDetails(workouts: List<WorkoutWithExercises>): List<WorkoutDetail> {
        val exerciseMap = workouts.flatMap { it.exercises }.map { it.exerciseId }.distinct()
            .let { ids ->
                if (ids.isEmpty()) emptyMap()
                else exerciseRepository.getByIds(ids).associateBy { it.id }
            }
        val muscles = muscleGroupRepository.getAll().associateBy { it.code }

        return workouts.map { withEx ->
            WorkoutDetail(
                id = withEx.workout.id,
                name = withEx.workout.name,
                notes = withEx.workout.notes,
                type = WorkoutType.fromKey(withEx.workout.type),
                exercises = withEx.exercises.sortedBy { it.order }.map { we ->
                    val ex = exerciseMap[we.exerciseId]
                    val muscle = ex?.primaryMuscleCode?.let { muscles[it] }
                    WorkoutExerciseItem(
                        workoutExerciseId = we.id,
                        exerciseId = we.exerciseId,
                        exerciseCode = ex?.code.orEmpty(),
                        equipmentCode = ex?.equipmentCode.orEmpty(),
                        exerciseName = ex?.name ?: "Удалённое упражнение",
                        muscleName = muscle?.name ?: "—",
                        muscleEmoji = muscle?.emoji ?: "🏋️",
                        primaryMuscleCode = ex?.primaryMuscleCode.orEmpty(),
                        secondaryMuscleCode = ex?.secondaryMuscleCode,
                        order = we.order,
                        sets = we.sets,
                        reps = we.reps,
                        restSeconds = we.restSeconds
                    )
                }
            )
        }
    }

    /** Создаёт новую тренировку и возвращает её id. */
    suspend fun createWorkout(name: String, notes: String?): Long =
        workoutDao.insert(
            Workout(
                name = name,
                type = WorkoutType.CUSTOM.storageKey,
                notes = notes
            )
        )

    /**
     * Сохраняет состав тренировки: заменяет все упражнения заданным списком.
     * [items] содержит exerciseId и параметры подходов; order рассчитывается по позиции.
     */
    suspend fun saveExercises(workoutId: Long, items: List<WorkoutExerciseItem>) {
        require(items.isNotEmpty()) { "Добавьте хотя бы одно упражнение" }
        require(items.all { it.sets in 1..100 && it.restSeconds in 0..3600 && parseTargetCount(it.reps) != null }) {
            "Проверьте подходы, повторения и отдых"
        }
        val entities = items.mapIndexed { index, item ->
            WorkoutExercise(
                workoutId = workoutId,
                exerciseId = item.exerciseId,
                order = index,
                sets = item.sets,
                reps = item.reps,
                restSeconds = item.restSeconds
            )
        }
        workoutExerciseDao.replaceWorkoutExercises(workoutId, entities)
    }

    suspend fun saveWorkoutWithExercises(
        workoutId: Long?,
        name: String,
        notes: String?,
        items: List<WorkoutExerciseItem>
    ): Long = db.withTransaction {
        val id = if (workoutId == null) {
            createWorkout(name, notes)
        } else {
            renameWorkout(workoutId, name, notes)
            workoutId
        }
        saveExercises(id, items)
        id
    }

    suspend fun renameWorkout(id: Long, name: String, notes: String?) {
        val workout = workoutDao.getById(id) ?: return
        workoutDao.update(workout.copy(name = name, notes = notes))
    }

    // A template's deletion must never delete the user's workout history.
    suspend fun deleteWorkout(id: Long) = workoutDao.archiveById(id)
}
