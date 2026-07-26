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
    val exerciseName: String,
    val muscleName: String,
    val muscleEmoji: String,
    val primaryMuscleCode: String = "",
    val secondaryMuscleCode: String? = null,
    val order: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
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
        databaseInitializer.initializeIfNeeded()
        workoutDao.observeByType(WorkoutType.PRESET.storageKey).collect { emit(it) }
    }

    suspend fun getDetail(id: Long): WorkoutDetail? {
        val withEx = workoutDao.getWithExercises(id) ?: return null
        val exerciseMap = withEx.exercises.map { it.exerciseId }.distinct()
            .let { ids ->
                if (ids.isEmpty()) emptyMap()
                else exerciseRepository.getByIds(ids).associateBy { it.id }
            }
        val muscleNames = muscleGroupRepository.getAll().associate { it.code to it.name }
        val muscleEmojis = muscleGroupRepository.getAll().associate { it.code to it.emoji }

        return WorkoutDetail(
            id = withEx.workout.id,
            name = withEx.workout.name,
            notes = withEx.workout.notes,
            type = WorkoutType.fromKey(withEx.workout.type),
            exercises = withEx.exercises.sortedBy { it.order }.map { we ->
                val ex = exerciseMap[we.exerciseId]
                WorkoutExerciseItem(
                    workoutExerciseId = we.id,
                    exerciseId = we.exerciseId,
                    exerciseCode = ex?.code.orEmpty(),
                    exerciseName = ex?.name ?: "Удалённое упражнение",
                    muscleName = ex?.primaryMuscleCode?.let { muscleNames[it] } ?: "—",
                    muscleEmoji = ex?.primaryMuscleCode?.let { muscleEmojis[it] } ?: "🏋️",
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

    suspend fun deleteWorkout(id: Long) = workoutDao.deleteById(id)
}
