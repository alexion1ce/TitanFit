package com.example.fitapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.fitapp.data.local.AppDatabase
import com.example.fitapp.data.local.dao.EquipmentDao
import com.example.fitapp.data.local.dao.ExerciseDao
import com.example.fitapp.data.local.dao.MuscleGroupDao
import com.example.fitapp.data.local.dao.WorkoutDao
import com.example.fitapp.data.local.dao.WorkoutExerciseDao
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.local.entity.WorkoutExercise
import com.example.fitapp.data.local.entity.WorkoutType
import com.example.fitapp.data.seed.DatabaseSeeder
import com.example.fitapp.data.seed.WorkoutPresets
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Отвечает за первичное наполнение базы данных справочниками, упражнениями
 * и готовыми программами. Выполняется один раз за процесс; при ошибке
 * следующий вызов повторит попытку.
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    private val db: AppDatabase,
    private val muscleGroupDao: MuscleGroupDao,
    private val equipmentDao: EquipmentDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao
) {
    private val mutex = Mutex()
    @Volatile private var initialized = false

    suspend fun initializeIfNeeded() {
        if (initialized) return
        mutex.withLock {
            if (initialized) return
            db.withTransaction { initialize() }
            initialized = true
        }
    }

    /** Same as [initializeIfNeeded], but reports a failure instead of throwing. */
    suspend fun tryInitialize(): Boolean = try {
        initializeIfNeeded()
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(TAG, "Database initialization failed", e)
        false
    }

    private suspend fun initialize() {
        // Reference rows are added by code, so new muscle groups or equipment
        // reach existing databases before exercises that reference them.
        muscleGroupDao.insertMissing(DatabaseSeeder.muscleGroups)
        equipmentDao.insertMissing(DatabaseSeeder.equipment)
        archiveDeprecatedExercises()
        updateRenamedExercises()
        seedMissingExercises()
        seedPresets()
    }

    private suspend fun archiveDeprecatedExercises() {
        exerciseDao.archiveByCodes(listOf("good_morning"))
    }

    private suspend fun updateRenamedExercises() {
        exerciseDao.updateNameByCode("leg_curl", "Сгибания ног в тренажёре")
        exerciseDao.updateNameByCode("face_pull", "Тяга каната к лицу")
        exerciseDao.updateNameByCode("machine_row", "Горизонтальная тяга в тренажёре")
    }

    private suspend fun seedMissingExercises() {
        val existingCodes = exerciseDao.getExistingCodes().toSet()
        val missingExercises = DatabaseSeeder.exercises.filter { it.code !in existingCodes }
        if (missingExercises.isNotEmpty()) {
            exerciseDao.insertAll(missingExercises)
        }
    }

    /**
     * Создаёт готовые программы, сопоставляя коды упражнений с их ID в БД.
     */
    private suspend fun seedPresets() {
        // Карта: код упражнения -> его id в БД
        val codeToId = exerciseDao.getAllCodes().associate { it.code to it.id }

        for (preset in WorkoutPresets.presets) {
            val existing = workoutDao.getPreset(preset.code, preset.name)
            val updated = existing?.copy(
                name = preset.name,
                notes = preset.description,
                presetCode = preset.code
            )
            val workoutId = if (existing == null) {
                workoutDao.insert(Workout(
                    name = preset.name,
                    type = WorkoutType.PRESET.storageKey,
                    notes = preset.description,
                    presetCode = preset.code
                ))
            } else {
                if (updated != existing) workoutDao.update(requireNotNull(updated))
                existing.id
            }
            val workoutExercises = preset.exercises.mapIndexed { index, ex ->
                val exerciseId = requireNotNull(codeToId[ex.code]) { "Unknown preset exercise: ${ex.code}" }
                WorkoutExercise(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    order = index,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds
                )
            }
            val current = workoutExerciseDao.getByWorkout(workoutId)
            if (current.map { it.copy(id = 0) } != workoutExercises) {
                workoutExerciseDao.replaceWorkoutExercises(workoutId, workoutExercises)
            }
        }
    }

    private companion object {
        const val TAG = "DatabaseInitializer"
    }
}
