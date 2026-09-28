package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fitapp.data.local.WorkoutWithExercises
import com.example.fitapp.data.local.entity.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    /** Все тренировки заданного типа. */
    @Query("SELECT * FROM workouts WHERE type = :type AND isArchived = 0 ORDER BY name")
    fun observeByType(type: String): Flow<List<Workout>>

    /** Includes archived workouts: used by backup, which must keep history links. */
    @Query("SELECT * FROM workouts WHERE type = :type ORDER BY id")
    suspend fun getAllByType(type: String): List<Workout>

    @Query("SELECT COUNT(*) FROM workouts WHERE type = :type AND isArchived = 0")
    suspend fun countByType(type: String): Int

    /** Кол-во пресетов (для проверки первичного наполнения). */
    @Query("SELECT COUNT(*) FROM workouts WHERE type = 'PRESET' AND isArchived = 0")
    suspend fun countPresets(): Int

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): Workout?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWithExercises(id: Long): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id IN (:ids)")
    suspend fun getWithExercisesByIds(ids: List<Long>): List<WorkoutWithExercises>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Query("UPDATE workouts SET isArchived = 1 WHERE id = :id")
    suspend fun archiveById(id: Long)

    @Query("SELECT * FROM workouts WHERE type = 'PRESET' AND (presetCode = :code OR (presetCode IS NULL AND name = :legacyName)) ORDER BY presetCode IS NULL, id LIMIT 1")
    suspend fun getPreset(code: String, legacyName: String): Workout?
}
