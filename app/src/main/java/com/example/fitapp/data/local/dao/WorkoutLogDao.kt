package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitapp.data.local.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {

    @Query("SELECT * FROM workout_logs ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE id = :id")
    suspend fun getById(id: Long): WorkoutLog?

    /** Все завершённые тренировки (с заполненным finishedAt), по убыванию даты. */
    @Query("SELECT * FROM workout_logs WHERE finishedAt IS NOT NULL ORDER BY startedAt DESC")
    suspend fun getAllFinished(): List<WorkoutLog>

    @Query("SELECT * FROM workout_logs WHERE workoutId = :workoutId AND finishedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getUnfinishedByWorkoutId(workoutId: Long): WorkoutLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WorkoutLog): Long

    @Update
    suspend fun update(log: WorkoutLog)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM workout_logs")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM workout_logs")
    suspend fun count(): Int
}
