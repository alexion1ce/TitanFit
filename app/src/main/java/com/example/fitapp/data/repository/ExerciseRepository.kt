package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.ExerciseDao
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.seed.DatabaseSeeder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun observeAll(): Flow<List<Exercise>> = exerciseDao.observeAll()

    fun observeFiltered(
        muscleCode: String?,
        equipmentCode: String?,
        difficulty: String?
    ): Flow<List<Exercise>> =
        exerciseDao.observeFiltered(muscleCode, equipmentCode, difficulty)

    suspend fun getById(id: Long): Exercise? = exerciseDao.getById(id)

    suspend fun getByIds(ids: List<Long>): List<Exercise> =
        if (ids.isEmpty()) emptyList() else exerciseDao.getByIds(ids)

    /**
     * Заполняет базу начальными данными, если она пуста.
     * Вызывается один раз при запуске приложения.
     */
    suspend fun seedIfEmpty() {
        if (exerciseDao.count() == 0) {
            exerciseDao.insertAll(DatabaseSeeder.exercises)
        }
    }
}
