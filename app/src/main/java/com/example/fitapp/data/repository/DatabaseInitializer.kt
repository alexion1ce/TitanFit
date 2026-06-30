package com.example.fitapp.data.repository

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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Отвечает за первичное наполнение базы данных справочниками, упражнениями
 * и готовыми программами. Запускается один раз при старте приложения.
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    private val muscleGroupDao: MuscleGroupDao,
    private val equipmentDao: EquipmentDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao
) {
    suspend fun initializeIfNeeded() {
        if (muscleGroupDao.count() == 0) {
            muscleGroupDao.insertAll(DatabaseSeeder.muscleGroups)
        }
        if (equipmentDao.count() == 0) {
            equipmentDao.insertAll(DatabaseSeeder.equipment)
        }
        if (exerciseDao.count() == 0) {
            exerciseDao.insertAll(DatabaseSeeder.exercises)
        }
        if (workoutDao.countPresets() == 0) {
            seedPresets()
        }
    }

    /**
     * Создаёт готовые программы, сопоставляя коды упражнений с их ID в БД.
     */
    private suspend fun seedPresets() {
        // Карта: код упражнения -> его id в БД
        val codeToId = exerciseDao.getAllCodes().associate { it.code to it.id }

        for (preset in WorkoutPresets.presets) {
            val workoutId = workoutDao.insert(
                Workout(
                    name = preset.name,
                    type = WorkoutType.PRESET.storageKey,
                    notes = preset.description
                )
            )
            val workoutExercises = preset.exercises.mapIndexedNotNull { index, ex ->
                val exerciseId = codeToId[ex.code] ?: return@mapIndexedNotNull null
                WorkoutExercise(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    order = index,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds
                )
            }
            workoutExerciseDao.insertAll(workoutExercises)
        }
    }
}
