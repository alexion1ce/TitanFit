package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitapp.data.local.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY name")
    fun observeAll(): Flow<List<Exercise>>

    /**
     * Фильтрация списка упражнений.
     * Если [muscleCode] или [equipmentCode] равны null — фильтр не применяется.
     */
    @Query(
        """
        SELECT * FROM exercises
        WHERE (:muscleCode IS NULL OR primaryMuscleCode = :muscleCode OR secondaryMuscleCode = :muscleCode)
          AND (:equipmentCode IS NULL OR equipmentCode = :equipmentCode)
          AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY name
        """
    )
    fun observeFiltered(
        muscleCode: String?,
        equipmentCode: String?,
        difficulty: String?
    ): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): Exercise?

    @Query("SELECT * FROM exercises WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<Exercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Exercise>)

    /** Все упражнения с id и кодом — для сопоставления пресетов. */
    @Query("SELECT id, code FROM exercises")
    suspend fun getAllCodes(): List<ExerciseCode>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}

/** Лёгкая проекция упражнения для seed-логики. */
data class ExerciseCode(val id: Long, val code: String)
