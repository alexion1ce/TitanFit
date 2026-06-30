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
    @Query("SELECT * FROM workouts WHERE type = :type ORDER BY name")
    fun observeByType(type: String): Flow<List<Workout>>

    @Query("SELECT COUNT(*) FROM workouts WHERE type = :type")
    suspend fun countByType(type: String): Int

    /** Кол-во пресетов (для проверки первичного наполнения). */
    @Query("SELECT COUNT(*) FROM workouts WHERE type = 'PRESET'")
    suspend fun countPresets(): Int

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): Workout?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWithExercises(id: Long): WorkoutWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
