package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitapp.data.local.entity.MuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleGroupDao {

    @Query("SELECT * FROM muscle_groups ORDER BY name")
    fun observeAll(): Flow<List<MuscleGroup>>

    @Query("SELECT * FROM muscle_groups ORDER BY name")
    suspend fun getAll(): List<MuscleGroup>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MuscleGroup>)

    @Query("SELECT COUNT(*) FROM muscle_groups")
    suspend fun count(): Int
}
