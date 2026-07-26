package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fitapp.data.local.entity.WorkoutExercise

@Dao
interface WorkoutExerciseDao {

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order`")
    suspend fun getByWorkout(workoutId: Long): List<WorkoutExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutExercise): Long

    /** Пакетная вставка упражнений в тренировку (используется при сохранении). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WorkoutExercise>)

    @Update
    suspend fun update(item: WorkoutExercise)

    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Удаляет все упражнения тренировки (перед полным пересохранением). */
    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteByWorkout(workoutId: Long)

    @Transaction
    suspend fun replaceWorkoutExercises(workoutId: Long, items: List<WorkoutExercise>) {
        deleteByWorkout(workoutId)
        insertAll(items)
    }
}
